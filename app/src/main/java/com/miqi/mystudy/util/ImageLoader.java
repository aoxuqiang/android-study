package com.miqi.mystudy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aoxuqiang on 2017/12/30.
 */
public class ImageLoader {

    private Handler mHandler  = new Handler();
    private LruCache<String,Bitmap> imgCache;

    public ImageLoader(){
        initCacheMemory();
    }
    /**
     * 初始化内存缓存
     */
    private void initCacheMemory() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//运行的最大内存
        int cacheSize = maxMemory / 4; //取四分之一作为图片的缓存大小
        imgCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每次存入时调用，返回bitmap的大小
                return value.getByteCount();
            }
        };
    }

    /**
     *  从缓存中读取bitmap
     * @param url
     * @return
     */
    private Bitmap getFromCache(String url){
        return imgCache.get(url);
    }

    /**
     *  添加缓存
     * @param url
     * @param bitmap
     */
    private void putInCache(String url,Bitmap bitmap){
        imgCache.put(url,bitmap);
    }

    public void loadImage(ImageView iv,String url){
        //先从缓存中读取
        Bitmap bitmap = getFromCache(url);
        //如果缓存中没有
        if(bitmap == null){
            showImageByAsyncTask(iv,url);
        }else{
            if(url.equals(iv.getTag()))
                iv.setImageBitmap(bitmap);
        }
    }

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

    public void showImageByAsyncTask(ImageView iv,String url){
        new DownloadImgTask(iv,url).execute(url);
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
            //将bitmap加入到缓存中
            if(bitmap!=null){
                putInCache(urlStr,bitmap);
            }
            //Thread.sleep(500); //模拟网络延迟
            conn.disconnect();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class DownloadImgTask extends AsyncTask<String,Void,Bitmap>{
        ImageView iv;
        String url;
        public DownloadImgTask(ImageView iv,String url){
            this.iv = iv;
            this.url = url;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(url.equals(iv.getTag()))
                iv.setImageBitmap(bitmap);
        }
    }
}
