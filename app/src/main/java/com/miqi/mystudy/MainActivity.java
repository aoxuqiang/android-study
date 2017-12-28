package com.miqi.mystudy;

import android.os.Bundle;
import android.widget.TextView;

import com.miqi.mystudy.okhttp.OkHttpUtil;

public class MainActivity extends BaseActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        sendRequest(); //测试发送同步GET请求
        sendAsyncRequest();//测试发送异步GET请求
        postRequest();
    }

    private void sendRequest(){ //发送同步GET请求,必须在子线程中进行
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.sendRequest();
            }
        }).start();
    }

    private void sendAsyncRequest(){ //发送异步GET请求
        OkHttpUtil.sendAsyncRequest();
    }

    private void postRequest(){ //发送同步POST请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.postRequest();
            }
        }).start();
    }
}
