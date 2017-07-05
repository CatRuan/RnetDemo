package com.rd.rnet.download;

import android.content.Context;
import android.util.Log;


import com.rd.rnet.callback.FileProgressCallBack;
import com.rd.rnet.exception.ExceptionHandle;
import com.rd.rnet.utils.LoadingPage;

import rx.Subscriber;

/**
 * Created by miya95 on 2016/12/5.
 * 文件下载观察者
 */
public class FileSubscriber<T> extends Subscriber<T> {
    private Context context;
    private FileProgressCallBack fileCallBack;

    public FileSubscriber(FileProgressCallBack fileCallBack, Context context) {
        this.fileCallBack = fileCallBack;
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fileCallBack != null)
            fileCallBack.onStart();
       //Log.i("Subscriber", "onStart");
    }

    @Override
    public void onCompleted() {
        if (fileCallBack != null)
            fileCallBack.onCompleted();
        // Log.i("Subscriber", "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        LoadingPage.hideLoadingDialog();
        if (fileCallBack != null)
            fileCallBack.onFail(ExceptionHandle.handleException(context, e));
        // Log.i("Subscriber", "onError");
    }

    @Override
    public void onNext(T t) {
        LoadingPage.hideLoadingDialog();
        if (fileCallBack != null)
            fileCallBack.onSuccess();
        //  Log.i("Subscriber", "onNext");
    }

}
