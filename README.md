## android-study
>### 安卓常见异常 ###

1. android.os.NetworkOnMainThreadException -- 在主线程进行网络请求
2. 
>###安卓异步加载


- **为什么要使用异步加载？**
	1. 还是安卓单线程模型
	2. 耗时操作阻塞UI线程
- **异步加载最常见的两种方式**
	1. 多线程、线程池
	2. AsyncTask
>### OkHttp

##### OkHttp的使用
**1. 同步GET请求**

     String url  = "www.baidu.com";
     OkHttpClient client = new OkHttpClient();
     Request request = new Request.Builder().url(url).build();
     try{
        Response response = call.execute();
        LogUtil.e("response",response.body().toString());
     } catch (IOException e) {
        e.printStackTrace();
     }
**注意：** 安卓中必须在子线程中运行，不然会报错！

**2. 异步GET请求**

    String url = "http://write.blog.csdn.net/postlist/0/0/enabled/1";
    Request request = new Request.Builder().url(url).build();
    // okhttp的回调运行在子线程
    mClient.newCall(request).enqueue(new Callback() {
    	@Override
    	public void onFailure(Call call, IOException e) {
    
    	}
    
    	@Override
    	public void onResponse(Call call, Response response) throws IOException {
    		String json = response.body().string();
    		Log.d("okHttp", json);
    	}
    });

>### Retrofit

### MVP

### MVVM
