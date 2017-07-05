package com.rd.rnet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ruand on 2017/5/18.
 * 网络工具类
 */

public class NetworkUtils {

    public static final int NET_OK = 1; // NetworkAvailable
    public static final int NET_TIMEOUT = 2; // no NetworkAvailable
    public static final int NET_NOT_CONNECTED = 5; // Net no connect
    public static final int NET_ERROR = 4; //net error
    private static final int TIMEOUT = 3000; // TIMEOUT


    /**
     * 返回当前网络状态
     *
     * @param context
     * @return
     */
    public static int getNetState(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if (networkinfo != null) {
                    if (networkinfo.isAvailable() && networkinfo.isConnected()) {
                        if (!connectionNetwork())
                            return NET_TIMEOUT;
                        else
                            return NET_OK;
                    } else {
                        return NET_NOT_CONNECTED;
                    }
                } else {
                    return NET_NOT_CONNECTED;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NET_ERROR;
    }

    /**
     * ping "http://www.baidu.com"
     *
     * @return
     */
    static private boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
            httpUrl = (HttpURLConnection) new URL("http://www.baidu.com")
                    .openConnection();
            httpUrl.setConnectTimeout(TIMEOUT);
            httpUrl.connect();
            result = true;
        } catch (IOException e) {
        } finally {
            if (null != httpUrl) {
                httpUrl.disconnect();
            }
            httpUrl = null;
        }
        return result;
    }

}
