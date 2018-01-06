package com.miqi.mystudy.okhttp;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.NetworkPolicy;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created by aoxuqiang on 2018/1/2.
 */

public class OkHttpDownloader implements Downloader {
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private static final String PICASSO_CACHE = "picasso-cache";

    private static OkHttpClient.Builder defaultOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        return builder;
    }

    private static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    private final OkHttpClient client;
    private final OkHttpClient.Builder builder;

    public OkHttpDownloader(final Context context) {
        this(createDefaultCacheDir(context));
    }

    public OkHttpDownloader(OkHttpClient.Builder builder){
        this.builder = builder;
        this.client = this.builder.build();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = 0l;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                available = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
            }else{
                available = ((long)statFs.getBlockCount()) * statFs.getBlockSize();
            }
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    public OkHttpDownloader(final File cacheDir){
        this(cacheDir,calculateDiskCacheSize(cacheDir));
    }

    protected final OkHttpClient getClient() {
        return client;
    }

    protected final OkHttpClient.Builder getBuilder() {
        return builder;
    }

    public OkHttpDownloader(final File cacheDir,long maxSize){
        this(defaultOkHttpClientBuilder());
        try {
            builder.cache(new okhttp3.Cache(cacheDir, maxSize));
        } catch (Exception ignored) {
        }
    }

    public OkHttpDownloader(final Context context, final long maxSize) {
        this(createDefaultCacheDir(context), maxSize);
    }

    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        CacheControl cacheControl = null;
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE;
            } else {
                CacheControl.Builder builder = new CacheControl.Builder();
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache();
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore();
                }
                cacheControl = builder.build();
            }
        }

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().url(uri.toString());
        if (cacheControl != null) {
            builder.cacheControl(cacheControl);
        }

        okhttp3.Response response = client.newCall(builder.build()).execute();
        int responseCode = response.code();
        if (responseCode >= 300) {
            response.body().close();
            throw new ResponseException(responseCode + " " + response.message(), networkPolicy,
                    responseCode);
        }

        boolean fromCache = response.cacheResponse() != null;

        ResponseBody responseBody = response.body();
        return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }

    @Override
    public void shutdown() {
        okhttp3.Cache cache = client.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}
