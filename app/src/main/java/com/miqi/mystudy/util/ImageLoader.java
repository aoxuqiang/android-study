package com.miqi.mystudy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by aoxuqiang on 2017/12/30.
 */
public class ImageLoader {
    private Handler mHandler  = new Handler();
    /**
     *  通过线程的方式加载图片
     * @param iv
     * @param url   图片的url
     */
    public void showImageByThread(final ImageView iv, final String url){
        new Thread(){
            @Override
            public void run() {
                final Bitmap bitmap = getBitmapFromUrl(url);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(url.equals(iv.getTag()))
                            iv.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    /**
     * 通过 ulr 获取  Bitmap
     * @param urlStr   url
     * @return Bitmap
     */
    private Bitmap getBitmapFromUrl(String urlStr){
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            Thread.sleep(1000); //模拟网络延迟
            conn.disconnect();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
