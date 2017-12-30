package com.miqi.mystudy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqi.mystudy.R;
import com.miqi.mystudy.bean.MoocRsp;
import com.miqi.mystudy.util.ImageLoader;

import java.util.List;

/**
 * Created by aoxuqiang on 2017/12/30.
 */

public class NewsAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoocRsp.DataBean> datas;
    public NewsAdapter(Context context, List<MoocRsp.DataBean> data){
        this.mContext = context;
        this.datas = data;
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private int count =0;
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder ;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news,null);
            Log.e("position-->"+i,convertView.toString());
            System.out.println("创建了"+ (++count)+"次");
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.news_iv);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        }else{
            Log.e("cv not null, positon-->"+i,convertView.toString());
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imgUrl = datas.get(i).getPicSmall();
        viewHolder.iv.setTag(imgUrl);
        new ImageLoader().showImageByThread(viewHolder.iv,imgUrl);
        //viewHolder.iv.setImageResource(R.mipmap.ic_launcher);
        viewHolder.tv_title.setText(datas.get(i).getName());
        viewHolder.tv_content.setText(datas.get(i).getDescription());
        return convertView;
    }

    class ViewHolder{
        ImageView iv;
        TextView tv_title;
        TextView tv_content;
    }
}
