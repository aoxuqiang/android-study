package com.miqi.mystudy;

import android.app.Application;
import android.content.Context;

/**
 * Created by aoxuqiang on 2017/12/29.
 */

public class BaseApplication extends Application {
    public static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = getApplicationContext();
    }
}