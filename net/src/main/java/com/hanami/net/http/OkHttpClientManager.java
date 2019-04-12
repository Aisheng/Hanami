package com.hanami.net.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author lidaisheng
 * @date 2019/4/7
 */
public class OkHttpClientManager {

    private OkHttpClient mOkHttpClient;

    private static OkHttpClientManager mInstance;
    private Handler mDelivery;
    private Gson mGson;


    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        //cookie enabled
        //mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }




}
