package com.miqi.mystudy;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.miqi.mystudy.bean.CommonResponse;
import com.miqi.mystudy.okhttp.OkHttpUtil;
import com.miqi.mystudy.retrofit.ApiService;
import com.miqi.mystudy.retrofit.RetrofitClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    @BindView(R.id.tv)
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //sendRequest(); //测试发送同步GET请求
        //sendAsyncRequest();//测试发送异步GET请求
        //postRequest();

        testRetrofit();
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

    private void testRetrofit() {
        ApiService service = RetrofitClient.getInstance().get(ApiService.class);
        service.login("admin","admin").subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread()) //回调在UI线程
                .subscribe(new Observer<CommonResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommonResponse commonResponse) {
                        Log.e("onNext",commonResponse.toString());
                        tv.setText(commonResponse.toString());
                    }
                });
    }
}
