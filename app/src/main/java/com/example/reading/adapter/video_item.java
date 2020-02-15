package com.example.reading.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.reading.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class video_item extends BaseAdapter {
    private static final String TAG = "video_item";
    public Context con;
    public ArrayList<Map<String,Object>> list;
    public LayoutInflater inflater;
    public video_item(Context context, ArrayList<Map<String,Object>> list){
        this.con=context;
        this.list=list;
        this.inflater=LayoutInflater.from(con);

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.video_item,null);
            holder.jcVideoPlayerStandard= (JCVideoPlayerStandard) convertView.findViewById(R.id.party_video);
            holder.vdescription = convertView.findViewById(R.id.vdescription);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
//加载视频
        Log.d(TAG, "getView: -------------"+"开始设置值2");
        holder.vdescription.setText(list.get(position).get("description").toString());
        holder.jcVideoPlayerStandard.setUp(list.get(position).get("url").toString(),
                JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
                list.get(position).get("title").toString());
        //判断获取的图片是否为空
        if (TextUtils.isEmpty(list.get(position).get("pic").toString())){
            //空时
            Picasso
                    .with(con)
                    .cancelRequest(holder.jcVideoPlayerStandard.thumbImageView);
            holder.jcVideoPlayerStandard.thumbImageView.
                    setImageDrawable(con.getResources().
                            getDrawable(R.mipmap.kong));//当图片为空时显示的图片

        }else {
            //这需要的图片是显示在视频没播放的情况下显示的图片
            Picasso
                    .with(con)
                    .load(list.get(position).get("pic").toString())
                    .placeholder(con.getResources().getDrawable(R.mipmap.kong))//图片加载中显示
                    .into(holder.jcVideoPlayerStandard.thumbImageView);
        }

        return convertView;
    }
    class ViewHolder{
        JCVideoPlayerStandard jcVideoPlayerStandard;
        TextView vdescription;
    }
}
