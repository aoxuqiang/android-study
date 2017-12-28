package com.miqi.mystudy.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Toast统一管理类
 * Created by MIQI on 2017/11/18.
 */
public class ToastUtil {

    private ToastUtil()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 显示带图片和文字的Toast
     * @param context
     * @param imageId
     * @param message
     */
    public static void showImageWithText(Context context, int imageId, CharSequence message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        ImageView img = new ImageView(context);
        img.setImageResource(imageId);
        LinearLayout toast_layout = (LinearLayout) toast.getView();
        toast_layout.setPadding(90, 70, 90 ,70);
        toast_layout.addView(img, 0);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextSize(16);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) text.getLayoutParams();
        lp.setMargins(0,20,0,0);
        text.setLayoutParams(lp);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}