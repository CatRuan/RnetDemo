package com.rd.rnet.exception;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;

import com.rd.rnet.utils.NetworkUtils;

import java.io.FileNotFoundException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Created by ruand on 2017/5/17.
 * 异常处理类
 */

public class ExceptionHandle {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;


    private static final String NET_ERROR = "net error";//网络异常
    private static final String NET_NOT_CONNECT = "net not nonnect";//无网络连接
    private static final String NET_TIME_OUT = "net time out";//网络超时
    private static final String CONNECT_TIME_OUT = "connect time out";//连接超时
    private static final String CONNECT_FAIL = "connect fail";//连接失败
    private static final String FILE_NOT_FIND = "file not find";//找不到指定文件
    private static final String PARSE_ERROR = "parse error";//解析错误
    private static final String SSL_HANDLE_SHAKE_EX = "ssl handle shake exception";//证书验证失败
    private static final String UNKNOWN_ERROR = "unknown error";//未知错误
    private static final String REQUEST_INTERRUPT = "request interrupt";//请求被打断


    public static String handleException(Context context, Throwable e) {

        Log.i("ExceptionHandle", "e->" + e);
        Log.i("ExceptionHandle", "msg" + e.getMessage());

        if (e instanceof HttpException
                || null != e.getMessage()
                && e.getMessage().contains("HttpException")) {
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    return NET_ERROR;

                case FORBIDDEN:
                    return NET_ERROR;

                case NOT_FOUND:
                    return CONNECT_FAIL;

                case REQUEST_TIMEOUT:
                    return CONNECT_TIME_OUT;

                case GATEWAY_TIMEOUT:
                    return CONNECT_TIME_OUT;

                case INTERNAL_SERVER_ERROR:
                    return NET_ERROR;

                case BAD_GATEWAY:
                    return NET_ERROR;

                case SERVICE_UNAVAILABLE:
                    return "service unavailable";

            }

        } else if (e instanceof FileNotFoundException
                || null != e.getMessage()
                && e.getMessage().contains("FileNotFoundException")) {
            return FILE_NOT_FIND;
        } else if (e instanceof InterruptedIOException
                || null != e.getMessage()
                && e.getMessage().contains("java.io.InterruptedIOException")) {
            return REQUEST_INTERRUPT;
        } else if (e instanceof ParseException
                || null != e.getMessage()
                && e.getMessage().contains("JSONException")
                || e.getMessage().contains("ParseException")) {

            return PARSE_ERROR;

        } else if (null != e.getMessage()
                && e.getMessage().contains("java.net.SocketTimeoutException")) {
            return CONNECT_TIME_OUT;
        } else if (e instanceof ConnectException
                || null != e.getMessage()
                && e.getMessage().contains("ConnectException")) {

            return CONNECT_FAIL;
        } else if (e instanceof SSLHandshakeException
                || null != e.getMessage()
                && e.getMessage().contains("javax.net.ssl.SSLHandshakeException")) {

            return SSL_HANDLE_SHAKE_EX;
        } else if (e instanceof UnknownHostException
                || null != e.getMessage()
                && e.getMessage().contains("java.net.UnknownHostException")) {
            return checkNetState(context);
        } else if (null != e.getMessage()
                && e.getMessage().contains("java.net.SocketException")) {
            return checkNetState(context);
        } else {
            return UNKNOWN_ERROR;

        }
        return NET_ERROR;
    }

    private static String checkNetState(Context context) {
        switch (NetworkUtils.getNetState(context)) {
            case NetworkUtils.NET_ERROR:
                return NET_ERROR;
            case NetworkUtils.NET_TIMEOUT:
                return NET_TIME_OUT;
            case NetworkUtils.NET_NOT_CONNECTED:
                return NET_NOT_CONNECT;
            default:
                return NET_ERROR;
        }
    }

}
