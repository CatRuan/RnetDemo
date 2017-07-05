package com.rd.rnet.callback;



import com.rd.rnet.download.FileLoadEvent;
import com.rd.rnet.download.RxBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action1;

/**
 * Created by miya95 on 2016/12/5.
 * 下载（/上传）文件回调,返回进度
 */
public abstract class FileProgressCallBack extends FileCallBack {

    private String destFileDir;
    private String destFileName;

    public FileProgressCallBack(String destFileDir, String destFileName) {
        super(destFileDir, destFileName);
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        subscribeLoadProgress();
    }

    public abstract void progress(long progress, long total);

    public abstract void onStart();

    public abstract void onCompleted();

    public void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //将catch到的异常重新抛出，使Rxjava的OnError捕获到该异常
            throw Exceptions.propagate(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw Exceptions.propagate(e);
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                //Log.e("saveFile", e.getMessage());
                throw Exceptions.propagate(e);
            }
            unSubscribe();
        }
    }

    /**
     * 订阅加载的进度条
     */
    public void subscribeLoadProgress() {
        Subscription subscription = RxBus.getInstance().doSubscribe(FileLoadEvent.class, new Action1<FileLoadEvent>() {
            @Override
            public void call(FileLoadEvent fileLoadEvent) {
                progress(fileLoadEvent.getBytesLoaded(), fileLoadEvent.getTotal());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //将catch到的异常重新抛出，使Rxjava的OnError捕获到该异常
                throw Exceptions.propagate(throwable);
            }
        });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    public void unSubscribe() {
        RxBus.getInstance().unSubscribe(this);
    }

}
