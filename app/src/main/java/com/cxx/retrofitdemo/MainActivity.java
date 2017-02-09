package com.cxx.retrofitdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.cxx.retrofitdemo.adapter.MoviesAdapter;
import com.cxx.retrofitdemo.bean.Movie;
import com.cxx.retrofitdemo.bean.HttpResult;
import com.cxx.retrofitdemo.interfaces.IMoviesServies;
import com.cxx.retrofitdemo.interfaces.ISubscriberOnNextListener;
import com.cxx.retrofitdemo.net.HttpMethods;
import com.cxx.retrofitdemo.net.ProgressSubscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 现在再写一个新的网络请求，步骤是这样的：
 * 1. 在Service中定义一个新的方法。
 * 2. 在HttpMethods封装对应的请求（代码基本可以copy）
 * 3. 创建一个SubscriberOnNextListener处理请求数据并刷新UI。
 * ...
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.rv_movies)
    RecyclerView rvMovies;

    private MoviesAdapter adapter;
    private List<Movie> list = new ArrayList<Movie>();
    private Movie movie;

    public static String TAG = "RetrofitDemo";
    private String URL = "https://api.douban.com/v2/movie/";//目标地址：https://api.douban.com/v2/movie/top250?start=0&count=10

    //private Subscriber<HttpResult<List<Movie>>> subscriber;
    private Subscriber<List<Movie>> subscriber;

    //加入progressbar。通过ProgressSubscriber继承Subscriber,并通过接口的方式回调onNext
    private ISubscriberOnNextListener mSubscriberOnNextListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new MoviesAdapter(this,list);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //此listener同于方法四
        mSubscriberOnNextListener = new ISubscriberOnNextListener<List<Movie>>() {
            @Override
            public void onNext(List<Movie> movies) {
                list.addAll(movies);
                adapter.notifyDataSetChanged();
            }
        };

        //getMovies(); //方法一 （未封装）
        //getMoviesByRxJava();//方法二 （未封装）
        //getMoviesByHttpMethods();//方法三  对RxJava进行了封装
        getMoviesByHttpMethodsAddProgress();//方法四  对RxJava进行了封装并加上了ProgressBar


    }

    private void getMoviesByHttpMethodsAddProgress() {
        HttpMethods.getInstance().getMovies(new ProgressSubscriber<List<Movie>>(mSubscriberOnNextListener,MainActivity.this),0,10);
    }

    /**
     * 对Retrofit+RxJava进行了封装
     */
    private void getMoviesByHttpMethods() {
        subscriber = new Subscriber<List<Movie>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Movie> movies) {
                list.addAll(movies);
                adapter.notifyDataSetChanged();
            }
        };
        HttpMethods.getInstance().getMovies(subscriber, 91, 30);

        //subscriber.unsubscribe(); //此方法是取消一个Http请求。
        // Subscriber一旦调用了unsubscribe方法之后，就没有用了。且当事件传递到onError或者onCompleted之后，也会自动的解绑
    }

    /**
    * Retrofit+RxJava方式请求网络数据
     */
    private void getMoviesByRxJava() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //
                .build();

        IMoviesServies moviesServies = retrofit.create(IMoviesServies.class);
        moviesServies.getMoviesByRxjava(0, 15)
                .subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<HttpResult<List<Movie>>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Movie>> httpResult) {
                        list.addAll(httpResult.getSubjects());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    /**
    * Retrofit方式请求网络数据
    */
    private void getMovies(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IMoviesServies moviesServies = retrofit.create(IMoviesServies.class);
        Call<HttpResult<List<Movie>>> call = moviesServies.getMovies(0,20);
        call.enqueue(new Callback<HttpResult<List<Movie>>>() {
            @Override
            public void onResponse(Call<HttpResult<List<Movie>>> call, Response<HttpResult<List<Movie>>> response) {
                Log.e(TAG, "onResponse: "+response.body().getTotal());
                list.addAll(response.body().getSubjects());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<HttpResult<List<Movie>>> call, Throwable t) {

            }
        });
    }
}
