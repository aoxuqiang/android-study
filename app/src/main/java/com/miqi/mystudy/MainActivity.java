package com.miqi.mystudy;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);

        String url = "http://write.blog.csdn.net/postlist/0/0/enabled/1";

        OkHttpClient mHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        /* okhttp3.Response response = null;*/

         /*response = mHttpClient.newCall(request).execute();*/
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("okHttp", json);
                tv.setText(json); //这里会报错，证明 okttp的回调不是运行在UI线程
            }
        });

    }
}
