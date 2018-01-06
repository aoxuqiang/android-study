package com.miqi.mystudy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqi.mystudy.adapter.NewsAdapter;
import com.miqi.mystudy.bean.CommonResponse;
import com.miqi.mystudy.bean.MoocRsp;
import com.miqi.mystudy.okhttp.OkHttpUtil;
import com.miqi.mystudy.retrofit.ApiService;
import com.miqi.mystudy.retrofit.RetrofitClient;
import com.miqi.mystudy.util.ImageLoader;
import com.miqi.mystudy.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.lv_news)
    ListView lv_news;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //sendRequest(); //测试发送同步GET请求
        //sendAsyncRequest();//测试发送异步GET请求
        //postRequest();
        //testRetrofit();
        //testGetBitMapFromUrl();
        initListView();
    }

    private void sendRequest(){ //发送同步GET请求,必须在子线程中进行
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.sendRequest();
            }
        }).start();
    }

    private void sendAsyncRequest(){ //发送异步GET请求
        OkHttpUtil.sendAsyncRequest();
    }

    private void postRequest(){ //发送同步POST请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.postRequest();
            }
        }).start();
    }

    private void testRetrofit() {
        ApiService service = RetrofitClient.getInstance().get(ApiService.class);
        service.login("admin","admin").subscribeOn(Schedulers.io())//请求在子线程
                .observeOn(AndroidSchedulers.mainThread()) //回调在UI线程
                .subscribe(new Observer<CommonResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommonResponse commonResponse) {
                        Log.e("onNext",commonResponse.toString());
                        tv.setText(commonResponse.toString());
                    }
                });
    }

    private void testGetBitMapFromUrl(){
        String imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515223809&di=1296cc04da405c452b39ec8a50d46d26&imgtype=jpg&er=1&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fzhidao%2Fwh%253D450%252C600%2Fsign%3D44eb28c9d32a60595245e91e1d0418ad%2Fa8773912b31bb051edfd7728327adab44bede0d1.jpg";
        new ImageLoader(this).showImageByThread(iv,imgUrl);
    }

    private String url = "http://www.imooc.com/api/teacher?type=4&num=30";
    private void initListView(){
        new MyAsyncTask().execute(url);
    }
    class MyAsyncTask extends AsyncTask<String,Void,MoocRsp>{

        @Override
        protected MoocRsp doInBackground(String... strings) {
            return getJsonData(strings[0]);
        }

        @Override
        protected void onPostExecute(MoocRsp moocRsp) {
            if(moocRsp == null){
                LogUtil.e("请求数据","失败");
                return;
            }
            List<MoocRsp.DataBean> news = moocRsp.getData();
            NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this,news);
            lv_news.setAdapter(newsAdapter);
            lv_news.setOnScrollListener(newsAdapter);
        }
    }
    private MoocRsp getJsonData(String s) {
        MoocRsp result = null;
        String str = "";
        try {
            URL url = new URL(s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is,"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while( (line = br.readLine())!= null){
                str += line;
            }
            //进行解析
            result = new Gson().fromJson(str,MoocRsp.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //重写返回事件，不销毁Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
