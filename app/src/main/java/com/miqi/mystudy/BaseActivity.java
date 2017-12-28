package com.miqi.mystudy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.miqi.mystudy.util.LogUtil;


/**
 * Created by MIQI on 2017/11/18.
 */

public class BaseActivity extends AppCompatActivity {
    private String tag;
    public BaseActivity(){
        tag = this.getClass().getSimpleName();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(tag,"life------------>onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(tag,"life------------>onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.i(tag,"life------------>onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(tag,"life------------>onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(tag,"life------------>onDestroy");
    }
}
