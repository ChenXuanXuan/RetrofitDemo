package com.cxx.retrofitdemo.bean;

/**
 * Created by 陈宣宣 on 2016/11/24.
 *
 * 此类可看做是有着相同格式的Http请求数据的实体类，通过泛型进行了封装
 * 在使用的时候要给出一个明确的类型
 */
public class HttpResult<T> {

    //通常接口api中都包含这两个，此demo的接口没有返回这两个参数，先放在这里，是为了写通用的封装
    private int resultCode;
    private String resultMessage;

    private int count;
    private int start;
    private int total;
    //private List<Movie> subjects;//不使用泛型
    private T subjects;//泛型  对相同格式的Http请求数据进行封装

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public T getSubjects() {
        return subjects;
    }

    public void setSubjects(T subjects) {
        this.subjects = subjects;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
