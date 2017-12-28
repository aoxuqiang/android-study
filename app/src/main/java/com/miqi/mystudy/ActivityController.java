package com.miqi.mystudy;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来管理所有的Activity
 * Created by MIQI on 2017/11/18.
 */
public class ActivityController {
    public static List<AppCompatActivity> activities = new ArrayList<AppCompatActivity>();

    /**
     *  添加activity
     * @param activity
     */
    public static void addActivity(AppCompatActivity activity){
        activities.add(activity);
    }

    /**
     * 移除activity
     * @param activity
     */
    public static void removeActivity(AppCompatActivity activity){
        activities.remove(activity);
    }

    /**
     *  管理所有的Activity,用来做退出
     */
    public static void finishAll(){
        for(AppCompatActivity activity : activities){
            activity.finish();
        }
    }
}
