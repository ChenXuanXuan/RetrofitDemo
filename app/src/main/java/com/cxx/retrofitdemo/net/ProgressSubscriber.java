package com.cxx.retrofitdemo.net;

import android.content.Context;
import android.widget.Toast;

import com.cxx.retrofitdemo.interfaces.ISubscriberOnNextListener;
import com.cxx.retrofitdemo.interfaces.ProgressCancelListener;

import rx.Subscriber;

/**
 * Created by 陈宣宣 on 2016/11/29.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private Context context;
    private ISubscriberOnNextListener mSubscriberOnNextListener;

    private ProgressDialogHandler mProgressDialogHandler;

    public ProgressSubscriber(ISubscriberOnNextListener mSubscriberOnNextListener,Context context){
        this.context = context;
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;

        mProgressDialogHandler = new ProgressDialogHandler(context,this,true);
    }

    //加载和关闭进度条的方法
    private void showProgressDialog(){
        if (mProgressDialogHandler != null){
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }
    private void dismissProgressDialog(){
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
        //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(context, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        dismissProgressDialog();
    }

    @Override
    public void onNext(T t) {
        mSubscriberOnNextListener.onNext(t);
    }

    @Override
    public void onCancelProgress() {
        //取消订阅
        if (!this.isUnsubscribed()){
            this.unsubscribe();
        }
    }


}
