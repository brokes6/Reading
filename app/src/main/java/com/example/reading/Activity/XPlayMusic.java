package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.databinding.XplayMusicBinding;
import com.example.reading.util.RequestStatus;
import com.qzs.android.fuzzybackgroundlibrary.Fuzzy_Background;


import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class XPlayMusic extends XBaseActivity {
    private static final String TAG = "XPlayMusic";
    private Timer timer;//定时器
    private MediaPlayer mediaPlayer;
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private int duration2;
    private int position;
    private boolean fristplay = false;
    private Bitmap bitmap;
    private ObjectAnimator mCircleAnimator;
    int index=0;
    String music_path ="https://sharefs.yun.kugou.com/202002131159/fe12bf1743fba8bdccd893e96d6227c5/G012/M02/18/1B/rIYBAFUPGLiAY70aAEjYKJYV34o632.mp3";
    private String[] url = {
            "https://sharefs.yun.kugou.com/202002131159/5e5776ff8e30198d5d39ff2f4233fe9a/G005/M05/03/04/pYYBAFT7EnKANNQ6ADfV3w4vyeE944.mp3",
            "https://sharefs.yun.kugou.com/202002131159/06045de85f5196330e9945a6cf29b1c4/G009/M03/01/1D/SQ0DAFT_AJKALPNBAEYW2uBd2gc565.mp3",
            "https://sharefs.yun.kugou.com/202002131159/fe12bf1743fba8bdccd893e96d6227c5/G012/M02/18/1B/rIYBAFUPGLiAY70aAEjYKJYV34o632.mp3",
            ""
    };
    XplayMusicBinding binding;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.playImg.setImageResource(R.mipmap.bt);
                    binding.MusicalBackground.setBackground(ToBlurredPicture(R.mipmap.bt));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.xplay_music);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        initView();
        initData();
        Message mes=new Message();
        mes.what=RequestStatus.SUCCESS;
        handler.sendMessage(mes);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(url[index]);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                duration2 = mediaPlayer.getDuration() / 1000;
                position = mediaPlayer.getCurrentPosition();
                binding.actPlayTimeStart.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.actPlayTimeStart.setText(calculateTime(position / 1000));
                        binding.actPlayTimeEnt.setText(calculateTime(duration2));
                    }
                });
            }
        });
        thread.start();

    }

    public void initView(){
        binding.actSuspend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        //绑定监听器，监听拖动到指定位置
        binding.actProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration2 = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int position = mediaPlayer.getCurrentPosition();//获取当前播放的位置
                binding.actPlayTimeStart.setText(calculateTime(position / 1000));//开始时间
                binding.actPlayTimeEnt.setText(calculateTime(duration2));//总时长
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = false;
                mediaPlayer.seekTo(seekBar.getProgress());//在当前位置播放
                binding.actPlayTimeStart.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });
//        binding.doubleSpeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSingleAlertDialog(getView());
//            }
//        });
        binding.actNextOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nextone();
            }
        });

    }

    public void initData(){
        mCircleAnimator = ObjectAnimator.ofFloat(binding.playImg, "rotation", 0.0f, 360.0f);
        mCircleAnimator.setDuration(60000);
        mCircleAnimator.setInterpolator(new LinearInterpolator());
        mCircleAnimator.setRepeatCount(-1);
        mCircleAnimator.setRepeatMode(ObjectAnimator.RESTART);

    }

    public void play(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            binding.actSuspend.setImageResource(R.mipmap.audio_state_pause);
            mCircleAnimator.pause();
            fristplay = true;
        }else{
            mediaPlayer.start();
            binding.actSuspend.setImageResource(R.mipmap.audio_state_play);
            if (fristplay==false){
                mCircleAnimator.start();
            }else{
                mCircleAnimator.resume();
            }
            int duration = mediaPlayer.getDuration();//获取音乐总时间
            binding.actProgress.setMax(duration);//将音乐总时间设置为Seekbar的最大值
            timer = new Timer();//时间监听器
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!isSeekbarChaning){
                        binding.actProgress.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            },0,50);
        }
    }

    public void Nextone(){
        index=index+1;
        if (index>url.length){

        }
    }


    //计算播放时间（将数值转换为时间）
    public String calculateTime(int time){
        int minute;
        int second;
        if(time > 60){
            minute = time / 60;
            second = time % 60;
            //分钟再0~9
            if(minute >= 0 && minute < 10){
                //判断秒
                if(second >= 0 && second < 10){
                    return "0"+minute+":"+"0"+second;
                }else {
                    return "0"+minute+":"+second;
                }
            }else {
                //分钟大于10再判断秒
                if(second >= 0 && second < 10){
                    return minute+":"+"0"+second;
                }else {
                    return minute+":"+second;
                }
            }
        }else if(time < 60){
            second = time;
            if(second >= 0 && second < 10){
                return "00:"+"0"+second;
            }else {
                return "00:"+ second;
            }
        }
        return null;
    }
    public Drawable ToBlurredPicture(int mipmap){
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),mipmap);
        Bitmap finalBitmap = Fuzzy_Background.with(XPlayMusic.this)
                .bitmap(bitmap) //要模糊的图片
                .radius(25)//模糊半径
                .blur();
        Drawable drawable = new BitmapDrawable(finalBitmap);
        Log.d(TAG, "ToBlurredPicture: 已返回背景");
        return drawable;
    }

    @Override
    public void onDestroy() {
        if(timer!=null)
            timer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mCircleAnimator.end();
        }
        super.onDestroy();
    }


}
