package com.miqi.mystudy.retrofit;


import com.google.gson.GsonBuilder;
import com.miqi.mystudy.okhttp.CookiesManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aoxuqiang on 2017/12/29.
 */

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.131.2:8080/";
    OkHttpClient client = new OkHttpClient.Builder().cookieJar(new CookiesManager()).build();
    GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitClient instance = null;
    private Retrofit mRetrofit = null;

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null)
                    instance = new RetrofitClient();
            }
        }
        return instance;
    }

    private RetrofitClient() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public <T> T get(Class<T> t) {
        return mRetrofit.create(t);
    }
}
