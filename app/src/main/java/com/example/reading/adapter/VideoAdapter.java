package com.example.reading.adapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.reading.Bean.VideoDetails;
import com.example.reading.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static android.content.ContentValues.TAG;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapterHolder>{
    private LayoutInflater inflater;
    private Context mContext;
    private List<VideoDetails> videoDetails=new ArrayList<>();

    public VideoAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setVideoAdapter(List<VideoDetails> videoDetails1) {
        videoDetails.clear();
        this.videoDetails.addAll(videoDetails1);
        Log.i(TAG, "Video_item_test开始设置"+this.videoDetails.size());
    }

    @NonNull
    @Override
    public VideoAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_item, parent, false);
        VideoAdapterHolder viewHolder = new VideoAdapterHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterHolder holder, int position) {
        JCVideoPlayerStandard.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JCVideoPlayerStandard.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        Log.i(TAG, "Video_item_testHolder: 执行第"+position+"边");
        final VideoDetails videoDetails2 = videoDetails.get(position);
        Log.i(TAG, "Video_item_testHolder: " + videoDetails2.getTid());
        holder.vdescription.setText("介绍: "+videoDetails2.getDescription());
        holder.jcVideoPlayerStandard.setUp(videoDetails2.getRurl(),JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,videoDetails2.getTitle());
        Log.d(TAG, "Video_item_testHolder: -------------------"+videoDetails2.getRurl());
        if (TextUtils.isEmpty(videoDetails2.getImg())){
            //空时
            Log.d(TAG, "onBindViewHolder: 视频图片为:"+videoDetails2.getImg());
            Picasso.with(mContext).cancelRequest(holder.jcVideoPlayerStandard.thumbImageView);
            holder.jcVideoPlayerStandard.thumbImageView.setImageDrawable(mContext.getResources().
                    getDrawable(R.mipmap.kong));//当图片为空时显示的图片
        }else{
            //这需要的图片是显示在视频没播放的情况下显示的图片
            Picasso
                    .with(mContext)
                    .load(videoDetails2.getImg())
                    .placeholder(mContext.getResources().getDrawable(R.mipmap.kong))//图片加载中显示
                    .into(holder.jcVideoPlayerStandard.thumbImageView);
        }
    }


    @Override
    public int getItemCount() {
        return videoDetails.size();
    }
}
class VideoAdapterHolder extends RecyclerView.ViewHolder{

    TextView vdescription;
    JCVideoPlayerStandard jcVideoPlayerStandard;
    public VideoAdapterHolder(View itemView) {
        super(itemView);
        vdescription = itemView.findViewById(R.id.vdescription);
        jcVideoPlayerStandard = itemView.findViewById(R.id.party_video);
        jcVideoPlayerStandard.SAVE_PROGRESS = false;
    }
}
