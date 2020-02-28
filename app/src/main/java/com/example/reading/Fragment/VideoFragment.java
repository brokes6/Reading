package com.example.reading.Fragment;

import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.reading.MainApplication;
import com.example.reading.R;
import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.ToolClass.Video;
import com.example.reading.databinding.VideoBinding;
import com.example.reading.util.RequestStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weavey.loading.lib.LoadingLayout;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import me.jessyan.autosize.AutoSizeConfig;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * 观看视频页面
 */
public class VideoFragment extends Fragment {
    private BottomSheetDialog dialog;
    VideoBinding binding;
    SensorManager sensorManager;
    BookDetailsBean bookDetailsBean = new BookDetailsBean();
    String Vurl;
    String Vimg;
    int type = 1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.loading.setStatus(LoadingLayout.Success);
                    String proxyUrl = MainApplication.getProxy(getContext()).getProxyUrl(Vurl);
                    binding.playerListVideo.setUp(proxyUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "标题");
                    binding.playerListVideo.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(getActivity()).load(Vimg).into(binding.playerListVideo.thumbImageView);
                    break;
                case RequestStatus.FAILURE:
                    binding.playerListVideo.setVisibility(View.GONE);
                    binding.loading.setStatus(LoadingLayout.Empty);
                    break;
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AutoSizeConfig.getInstance().setCustomFragment(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.video,container,false);
        EventBus.getDefault().register(this);
        binding.loading.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        return binding.getRoot();
    }
    public void initView(){
        JCVideoPlayerStandard.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JCVideoPlayerStandard.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    }
    public void initData(){

    }
    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Video video) {
        //我这里是对的
        Vurl=video.getVideo_path();
        Vimg=video.getVideo_img();
        type=video.getType();
        Log.d(TAG, "传过来的type为"+type);
        if (Vurl !=null){
        if (type ==1){
            Message mes=new Message();
            mes.what= RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }else{
        Message mes=new Message();
        mes.what= RequestStatus.SUCCESS;
        handler.sendMessage(mes); }
        }else{
            Message mes=new Message();
            mes.what= RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }
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

    @Override
        public void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
    }


}
