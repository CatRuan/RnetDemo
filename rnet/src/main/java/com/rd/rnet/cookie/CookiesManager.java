package com.rd.rnet.cookie;

import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by ruand on 2017/6/12.
 * cookie manager
 * http://www.jianshu.com/p/41b4cbe1dbec
 */

public class CookiesManager implements CookieJar {

    private static PersistentCookieStore cookieStore;

    public CookiesManager(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cookieStore == null) {
                    cookieStore = new PersistentCookieStore(context);
                }
            }
        }).start();

    }

    @Override
    public void saveFromResponse(final HttpUrl url, final List<Cookie> cookies) {

        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}
