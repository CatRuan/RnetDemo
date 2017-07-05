package com.rd.rnet.callback;


import android.content.Context;
import android.util.Log;

import com.rd.rnet.exception.ExceptionHandle;
import com.rd.rnet.utils.LoadingPage;

import rx.Subscriber;

/**
 * Created by ruand on 2017/5/9.
 * 请求（get/post）
 */

public abstract class NetCallback<T> extends Subscriber<T> {

    private Context mContext;

    public NetCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onCompleted() {
        Log.i("NetCallback", "onCompleted");


    }

    @Override
    public void onError(Throwable e) {
        //请求失败
        e.printStackTrace();
        // Log.i("NetCallback", "onError");
        onFail(ExceptionHandle.handleException(mContext, e));
        LoadingPage.hideLoadingDialog();

    }

    @Override
    public void onNext(T response) {
        //Log.i("NetCallback", "response");
        //请求成功
        onSuccess(response);
        LoadingPage.hideLoadingDialog();
    }


    public abstract void onSuccess(T response);

    public abstract void onFail(String message);

}

