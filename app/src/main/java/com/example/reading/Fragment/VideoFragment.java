package com.example.reading.Fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.reading.Bean.BookResource;
import com.example.reading.R;
import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.ToolClass.Video;
import com.example.reading.databinding.VideoBinding;
import com.example.reading.util.RequestStatus;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weavey.loading.lib.LoadingLayout;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

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
                    binding.playerListVideo.setUp(Vurl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "标题");
                    binding.playerListVideo.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(getActivity()).load(Vimg).into(binding.playerListVideo.thumbImageView);
                    break;
                case RequestStatus.FAILURE:
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
    public void onEventMainThread(BookResource video) {
        Vurl=video.getUrl();
        Vimg=video.getImg();
        type=video.getType();
        Log.d(TAG, "当前传过来的type为"+type);
        if (Vurl !=null){
        if (type ==1){
            Message mes=new Message();
            mes.what= RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }else{
        /**
         * 参数1：视频路径
         * 参数2：播放器类型
         * 参数3：视频标题  可为空
         */
        Message mes=new Message();
        mes.what= RequestStatus.SUCCESS;
        handler.sendMessage(mes);
            }
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


    /**
     *2019/10/16
     * 方法：弹出评论框
     */
    private void showCommentDialog(){
        dialog = new BottomSheetDialog(getContext(),R.style.BottomSheetEdit);
        final View commentView = LayoutInflater.from(getContext()).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                //后期需要检查token的值 查看是否被更改了喔
                if(!TextUtils.isEmpty(commentContent)){
                    //
                    dialog.dismiss();
                }else {
                    Toast.makeText(getActivity(),"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }
    @Override
        public void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
    }


}
