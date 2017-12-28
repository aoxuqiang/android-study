package com.miqi.mystudy.okhttp;

import android.util.Log;

import com.miqi.mystudy.util.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by aoxuqiang on 2017/12/28.
 */

public class OkHttpUtil {
    private static final String URL = "http://www.baidu.com";
    private static OkHttpClient mClient = new OkHttpClient();
    private OkHttpUtil(){}

    /**
     * 发送同步请求,安卓中必须在子线程中进行
     */
    public static void sendRequest(){
        Request request = new Request.Builder().url(URL).build();
        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();
            LogUtil.e("response",response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送异步请求
     */
    public static void sendAsyncRequest(){
        String url = "http://write.blog.csdn.net/postlist/0/0/enabled/1";
        Request request = new Request.Builder().url(url).build();
        // okhttp的回调运行在子线程
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("okHttp", json);
            }
        });
    }

    /**
     *  发送 POST 同步请求
     */
    public static void postRequest(){
        String url = "http://192.168.131.2:8080/login";
        RequestBody body = new FormBody.Builder()
                .add("account", "admin")
                .add("passwd", "admin")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
