package com.miqi.mystudy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.miqi.mystudy.util.SPUtil;

/**
 *  启动屏
 * Created by aoxuqiang on 2017/12/31.
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
        boolean isFirstOpen = (boolean) SPUtil.get(this,AppConstants.FIRST_OPEN,true);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //设置全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_splash);
        //延时两秒进入主页
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHomeActivity();
            }
        }, 2000);
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
