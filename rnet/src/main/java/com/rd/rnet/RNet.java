package com.rd.rnet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.rd.rnet.callback.FileCallBack;
import com.rd.rnet.callback.FileProgressCallBack;
import com.rd.rnet.callback.NetCallback;
import com.rd.rnet.cookie.CookiesManager;
import com.rd.rnet.download.FileSubscriber;
import com.rd.rnet.exception.ExceptionHandle;
import com.rd.rnet.interceptor.BaseInterceptor;
import com.rd.rnet.interceptor.ProgressInterceptor;
import com.rd.rnet.utils.LoadingPage;
import com.rd.rnet.utils.SSLSocketFactoryUtils;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by ruand on 2017/5/16.
 * 网络请求工具
 */

public class RNet {

    private static Retrofit mRetrofit;
    private volatile static RNet mNetClient;

    private RNet(Application ctx, String url, boolean trustCertificate) {
        this(ctx, url, null, trustCertificate);
    }

    private RNet(Application ctx, String url, Map<String, String> headers, boolean trustCertificate) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.cookieJar(new CookiesManager(ctx))
                .addInterceptor(new ProgressInterceptor())
                .addInterceptor(new BaseInterceptor(headers))
                .hostnameVerifier(new SSLSocketFactoryUtils.TrustAllHostnameVerifier());
        if (trustCertificate) {
            clientBuilder.sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager());
        }
        OkHttpClient client = clientBuilder.build();
        builder.client(client);
        mRetrofit = builder.build();
    }

    /**
     * @param ctx              context application best
     * @param url              base url
     * @param trustCertificate trust all certificates ,this is just for debug .do not open it when your app ready to online
     * @return the singleton of lightNet
     */
    public static RNet getInstance(Application ctx, String url, boolean trustCertificate) {
        if (mNetClient == null) {
            synchronized (RNet.class) {
                if (mNetClient == null) {
                    mNetClient = new RNet(ctx, url, trustCertificate);
                }
            }
        }
        return mNetClient;
    }

    /**
     * @param ctx              context application best
     * @param url              base url
     * @param headers          common header
     * @param trustCertificate this is just for debug to trust all certificates  .do not open it when your app ready to online
     * @return the singleton of lightNet
     */
    public static RNet getInstance(Application ctx, String url, Map<String, String> headers, boolean trustCertificate) {
        if (mNetClient == null) {
            synchronized (RNet.class) {
                if (mNetClient == null) {
                    mNetClient = new RNet(ctx, url, headers, trustCertificate);
                }
            }
        }
        return mNetClient;
    }

    /**
     * 创建请求示例
     *
     * @param service the service of retrofit
     * @param <T>     the class of the retrofit interface
     * @return the instance of retrofit service
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("api service is null!");
        }
        if (mRetrofit == null) {
            throw new RuntimeException("retrofit is null! has you init RNet ?");
        }
        return mRetrofit.create(service);
    }


    /**
     * get请求
     *
     * @param observable 被观察者
     * @param callback   请求回调
     * @return 订阅
     */
    public Subscription get(Observable<? extends Object> observable
            , final NetCallback callback) {
        Subscription subscription = observable.subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(callback);
        return subscription;
    }

    /**
     * post请求
     *
     * @param observable 被观察者
     * @param callback   请求回调
     * @return 订阅
     */
    public Subscription post(Observable<? extends Object> observable
            , final NetCallback callback) {
        return get(observable, callback);
    }

    /**
     * 文件下载，带进度条
     *
     * @param context    上下文
     * @param observable 被观察者
     * @param callBack   文件回调
     */
    public Subscription downloadWithProgress(Context context, Observable<Response<ResponseBody>> observable, final FileProgressCallBack callBack) {
        Subscription subscription = observable.subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .doOnNext(new Action1<Response<ResponseBody>>() {
                    @Override
                    public void call(Response<ResponseBody> response) {
                        callBack.saveFile(response.body());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new FileSubscriber<Response<ResponseBody>>(callBack, context));
        return subscription;
    }

    /**
     * 文件普通下载
     *
     * @param context    上下文
     * @param observable 被观察者
     * @param callBack   文件回调
     */
    public Subscription download(final Context context, Observable<Response<ResponseBody>> observable, final FileCallBack callBack) {
        Subscription subscription = observable.subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .doOnNext(new Action1<Response<ResponseBody>>() {
                    @Override
                    public void call(Response<ResponseBody> response) {
                        Log.i("RNet", "response->" + response);
                        callBack.saveFile(response.body());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new Subscriber<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingPage.hideLoadingDialog();
                        e.printStackTrace();
                        callBack.onFail(ExceptionHandle.handleException(context, e));
                    }

                    @Override
                    public void onNext(Response<ResponseBody> response) {
                        LoadingPage.hideLoadingDialog();
                        if (response.code() == 200) {
                            callBack.onSuccess();
                        } else {
                            callBack.onFail(response.errorBody() + "");
                        }
                    }
                });
        return subscription;
    }

    private static Gson mGson;
    public RequestBody objectToRequestBody(Object obj) {
        if(null==mGson){
            mGson = new Gson();
        }
        String jsonStr = mGson.toJson(obj);
        Log.i("RNet", "发送数据->" + jsonStr);
        return RequestBody.create(MediaType.parse("application/json"), jsonStr);
    }

    /**
     * show loading dialog
     *
     * @param activity activity
     */
    public void showLoadingDialog(Activity activity) {
        LoadingPage.showLoadingDialog(activity);
    }

    /**
     * cancel request
     *
     * @param subscription subscription
     */
    public void cancelRequest(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if(null!=mGson){
            mGson = null;
        }
    }

}
