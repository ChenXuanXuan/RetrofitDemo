package com.cxx.retrofitdemo.net;

import com.cxx.retrofitdemo.bean.Movie;
import com.cxx.retrofitdemo.bean.HttpResult;
import com.cxx.retrofitdemo.interfaces.IMoviesServies;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 陈宣宣 on 2016/11/28.
 */
public class HttpMethods {

    public static final String BASE_URL = "https://api.douban.com/v2/movie/";
    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private IMoviesServies moviesServies;

    /*
    *构造方法私有
    * */
    private HttpMethods(){
        //手动创建一个OKHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        moviesServies = retrofit.create(IMoviesServies.class);
    }

    /*
    * 创建并获取单例
    * */
    private static HttpMethods httpMethods;
    public static HttpMethods getInstance(){
        if(httpMethods == null){
            synchronized (HttpMethods.class){
                if(httpMethods == null){
                    httpMethods = new HttpMethods();
                }
            }
        }
        return httpMethods;
    }


    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * 在resultCode != 0的时候，抛出个自定义的ApiException。这样就会进入到subscriber的onError中，我们可以在onError中处理错误信息。
     * 另外，请求成功时，需要将data数据转换为目标数据类型传递给subscriber，因为，Activity和Fragment只想拿到和他们真正相关的数据。
     * 使用Observable的map方法可以完成这一功能。
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>,T>{

        @Override
        public T call(HttpResult<T> httpResult) {
            if(httpResult.getResultCode() !=0){
                throw new ApiException(httpResult.getResultCode());
            }
            return httpResult.getSubjects();//通常情况下返回通用的data,只是名字不一样而已
        }
    }


    /**
     * 用于获取豆瓣电影Top250的数据
     * @param subscriber 由调用者传过来的观察者对象
     * @param start 起始位置
     * @param count 获取长度
     */
    public void getMovies(Subscriber<List<Movie>> subscriber, int start, int count){
        moviesServies.getMoviesByRxjava(start,count)
                .map(new HttpResultFunc<List<Movie>>())//Observable的map方法,完成HttpResultFunc中的resultCode预处理功能
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(subscriber);
    }

}
