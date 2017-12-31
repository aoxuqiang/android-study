package com.miqi.mystudy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import libcore.io.DiskLruCache;


/**
 * Created by aoxuqiang on 2017/12/30.
 */
public class ImageLoader {
    // 文件缓存默认 10M
    static final int DISK_CACHE_DEFAULT_SIZE = 10 * 1024 * 1024;
    private Handler mHandler = new Handler();
    // 一级内存缓存基于 LruCache
    private LruCache<String, Bitmap> memCache;
    // 二级文件缓存基于 DiskLruCache
    private DiskLruCache diskCache;

    public ImageLoader(Context context) {
        initMemoryCache();
        initDiskCache(context);
    }

    public void loadImage(ImageView iv, String url) {
        //先从内存缓存中读取
        Bitmap bitmap = getFromMemoryCache(url);
        //如果缓存中没有
        if (bitmap == null) {
            LogUtil.e("读取缓存", "内存缓存中没有");
            //从文件缓存中取
            bitmap = getBitmapFromDisk(url);
            if (bitmap == null) {
                LogUtil.e("读取缓存", "文件缓存中没有");
                showImageByAsyncTask(iv, url);
            } else {
                LogUtil.e("读取缓存", "文件缓存中有");
                //放入内存缓存中
                putInMemoryCache(url, bitmap);
                LogUtil.e("文件缓存中有", "加入到内存缓存中");
                if (url.equals(iv.getTag()))
                    iv.setImageBitmap(bitmap);
            }
        } else {
            LogUtil.e("读取缓存", "内存缓存中有");
            if (url.equals(iv.getTag()))
                iv.setImageBitmap(bitmap);
        }
    }

    /**
     * 初始化内存缓存(一级缓存）
     */
    private void initMemoryCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//运行的最大内存
        int cacheSize = maxMemory / 4; //取四分之一作为图片的缓存大小
        memCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每次存入时调用，返回bitmap的大小
                return value.getByteCount();
            }
        };
    }

    /**
     * 初始化 文件缓存（二级缓存）
     */
    private void initDiskCache(Context context) {
        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            diskCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_DEFAULT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存文件
     *
     * @param context
     * @param uniqueName
     * @return
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 从缓存中读取bitmap
     *
     * @param url
     * @return
     */
    private Bitmap getFromMemoryCache(String url) {
        return memCache.get(url);
    }

    /**
     * 添加缓存
     *
     * @param url
     * @param bitmap
     */
    private void putInMemoryCache(String url, Bitmap bitmap) {
        memCache.put(url, bitmap);
    }

    /**
     * 从文件缓存中拿
     *
     * @param url
     */
    public Bitmap getBitmapFromDisk(String url) {
        try {
            String key = hashKeyForDisk(url);
            DiskLruCache.Snapshot snapShot = diskCache.get(key);
            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存入缓存（内存缓存，磁盘缓存）
     */
    public void putBitmapToDisk(String url, Bitmap bitmap) {
        // 判断是否存在DiskLruCache缓存，若没有存入
        String key = hashKeyForDisk(url);
        try {
            DiskLruCache.Editor editor = diskCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    LogUtil.e("放入文件缓存中", "成功");
                    editor.commit();
                } else {
                    LogUtil.e("放入文件缓存中", "失败");
                    editor.abort();
                }
            }
            diskCache.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * 通过线程的方式加载图片
     *
     * @param iv
     * @param url 图片的url
     */
    public void showImageByThread(final ImageView iv, final String url) {

        new Thread() {
            @Override
            public void run() {
                final Bitmap bitmap = getBitmapFromUrl(url);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equals(iv.getTag()))
                            iv.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    public void showImageByAsyncTask(ImageView iv, String url) {
        new DownloadImgTask(iv, url).execute(url);
    }

    /**
     * 通过 ulr 获取  Bitmap
     *
     * @param urlStr url
     * @return Bitmap
     */
    private Bitmap getBitmapFromUrl(String urlStr) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            //将bitmap加入到缓存中
            if (bitmap != null) {
                putBitmapToDisk(urlStr, bitmap);
                putInMemoryCache(urlStr, bitmap);
            }
            //Thread.sleep(500); //模拟网络延迟
            conn.disconnect();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {
        ImageView iv;
        String url;

        public DownloadImgTask(ImageView iv, String url) {
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
            if (url.equals(iv.getTag()))
                iv.setImageBitmap(bitmap);
        }
    }
}
