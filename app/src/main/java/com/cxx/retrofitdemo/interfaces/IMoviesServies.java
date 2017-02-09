package com.cxx.retrofitdemo.interfaces;

import com.cxx.retrofitdemo.bean.Movie;
import com.cxx.retrofitdemo.bean.HttpResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 陈宣宣 on 2016/11/25.
 */
public interface IMoviesServies {

    /**
    * Retrofit方式接口
    * @param start 起始位置
    * @param count 获取长度
    */
    @GET("top250") //接口名
    Call<HttpResult<List<Movie>>> getMovies(@Query("start") int start, @Query("count") int count);//参数

    /*
    * Retrofit+Rxjava方式接口
    * */
    @GET("top250")
    Observable<HttpResult<List<Movie>>> getMoviesByRxjava(@Query("start") int start, @Query("count") int count);
}
