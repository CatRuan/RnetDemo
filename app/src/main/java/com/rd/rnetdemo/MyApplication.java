package com.rd.rnetdemo;

import android.app.Application;
import android.os.StrictMode;

import com.rd.rnet.RNet;

import java.util.HashMap;

/**
 * Created by ruand on 2017/6/28.
 *
 */

public class MyApplication extends Application {

    private HashMap<String, String> mHeaders;
    private static final String BASE_URL = "https://github.com/";
    public static RNet mRNet;
    public static NetService mNetService;
    private static MyApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initStrictMode();
        initNetWork();
    }

    private void initNetWork() {
        mHeaders = new HashMap<>();
//        mHeaders.put("Content-Type", "application/json;encoding-utf-8");
//        mHeaders.put("Accept", "application/json");
        mRNet = RNet.getInstance(this, BASE_URL, mHeaders, false);
        mNetService = mRNet.create(NetService.class);
    }


    public static MyApplication getInstance() {
        return mApp;
    }

    public RNet getRNet() {
        return mRNet;
    }


    public NetService getNetService() {
        return mNetService;
    }

    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

}
