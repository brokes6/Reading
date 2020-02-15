package com.example.reading.adapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.reading.Bean.VideoDetails;
import com.example.reading.R;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static android.content.ContentValues.TAG;

public class Video_item_test extends RecyclerView.Adapter<Video_item_testHolder>{
    private LayoutInflater inflater;
    private Context mContext;
    private List<VideoDetails> videoDetails=new ArrayList<>();

    public Video_item_test(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setVideo_item_test(List<VideoDetails> videoDetails1) {
        videoDetails.clear();
        this.videoDetails.addAll(videoDetails1);
        Log.i(TAG, "Video_item_test开始设置"+this.videoDetails.size());
    }

    @NonNull
    @Override
    public Video_item_testHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_item, parent, false);
        Video_item_testHolder viewHolder = new Video_item_testHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Video_item_testHolder holder, int position) {
        JCVideoPlayerStandard.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JCVideoPlayerStandard.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        Log.i(TAG, "Video_item_testHolder: 执行第"+position+"边");
        final VideoDetails videoDetails2 = videoDetails.get(position);
        Log.i(TAG, "Video_item_testHolder: " + videoDetails2.getTid());
        holder.vdescription.setText("介绍: "+videoDetails2.getDescription());
        holder.jcVideoPlayerStandard.setUp(videoDetails2.getRurl(),JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,videoDetails2.getTitle());
        Log.d(TAG, "Video_item_testHolder: -------------------"+videoDetails2.getRurl());
//        if (TextUtils.isEmpty(videoDetails2.getRimg())){
//
//        }
    }


    @Override
    public int getItemCount() {
        return videoDetails.size();
    }
}
class Video_item_testHolder extends RecyclerView.ViewHolder{

    TextView vdescription;
    JCVideoPlayerStandard jcVideoPlayerStandard;
    public Video_item_testHolder(View itemView) {
        super(itemView);
        vdescription = itemView.findViewById(R.id.vdescription);
        jcVideoPlayerStandard = itemView.findViewById(R.id.party_video);
        jcVideoPlayerStandard.SAVE_PROGRESS = false;

    }
}
