package com.example.reading.Fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.reading.R;
import com.example.reading.databinding.VideoBinding;
import com.example.reading.util.JzViewOutlineProvider;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * 观看视频页面
 */
public class VideoFragment extends Fragment {
    VideoBinding binding;
    SensorManager sensorManager;
    String s1="http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.video,container,false);
        initView();
        initData();
        return binding.getRoot();
    }
    public void initView(){
        JCVideoPlayerStandard.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JCVideoPlayerStandard.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        /**
         * 参数1：视频路径
         * 参数2：播放器类型
         * 参数3：视频标题  可为空
         */
        binding.playerListVideo.setUp("http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4", JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "黑色毛衣");
        binding.playerListVideo.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(getActivity()).load("https://gss2.bdstatic.com/-fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=0c5a25bad1ca7bcb6976cf7ddf600006/6d81800a19d8bc3ecf611ab3848ba61ea8d34559.jpg").into(binding.playerListVideo.thumbImageView);
    }
    public void initData(){

    }
    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (JCVideoPlayer.backPress()) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }
    public boolean isFmActive() {
        final AudioManager am = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            Log.d(TAG, "isFmActive: 播放？----------------------------");
         return false;
     }
        Log.d(TAG, "isFmActive: 不不不不不播放？----------------------------");
        return true;
    }


}
