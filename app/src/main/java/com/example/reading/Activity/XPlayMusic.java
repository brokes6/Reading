package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.databinding.XplayMusicBinding;
import com.example.reading.util.FastBlurUtil;
import com.example.reading.util.RequestStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qzs.android.fuzzybackgroundlibrary.Fuzzy_Background;
import com.weavey.loading.lib.LoadingLayout;


import net.qiujuer.genius.blur.StackBlur;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.jessyan.autosize.internal.CustomAdapt;


public class XPlayMusic extends XBaseActivity implements CustomAdapt {
    private static final String TAG = "XPlayMusic";
    private Timer timer;//定时器
    private MediaPlayer mediaPlayer;
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private int duration2;
    private int position;
    private boolean fristplay = false;
    private Bitmap bitmap;
    private ObjectAnimator mCircleAnimator;
    FastBlurUtil fastBlurUtil = new FastBlurUtil();
    int index=0;
    String ximg;
    String xurl;
    Bitmap Img;
    String title;
    Bitmap bitmap2;
    String music_path ="https://sharefs.yun.kugou.com/202002131159/fe12bf1743fba8bdccd893e96d6227c5/G012/M02/18/1B/rIYBAFUPGLiAY70aAEjYKJYV34o632.mp3";
    XplayMusicBinding binding;
    private Drawable drawable;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    if (ximg==null){
                        Log.d(TAG, "当前音乐无背景···");
                        binding.actBookDetailTitleId.setText(title);
                        binding.playImg.setImageResource(R.mipmap.bt);
                        binding.MusicalBackground.setBackground(ToBlurredPicture(R.mipmap.bt));
                    }else{
                        binding.actBookDetailTitleId.setText(title);
                        binding.playImg.setImageURL(ximg);
                        binding.playImg.setStrokeWidth(5);
                    }
                    break;
                case RequestStatus.AUDIO:
                    Bitmap bitmaps = (Bitmap) msg.obj;
                    Bitmap newBitmap = StackBlur.blurNativelyPixels(bitmaps, 25, false);
                    binding.MusicalBackground.setBackground(ToBitmapPicture(newBitmap));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.xplay_music);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        binding.loadButton.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        Getsongs();
        Message mes=new Message();
        mes.what=RequestStatus.SUCCESS;
        handler.sendMessage(mes);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                Bitmap bitmap =fastBlurUtil.getBitMBitmap(ximg);
                Message msg=new Message();
                msg.what = RequestStatus.AUDIO;
                msg.obj = bitmap;
                handler.sendMessage(msg);
                try {
                    mediaPlayer.setDataSource(xurl);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            binding.loadButton.setStatus(LoadingLayout.Success);
                        }
                    });
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
        Intent intent = getIntent();
        ximg = intent.getStringExtra("img");
        Log.d(TAG, "initView: 返回的图片为"+ximg);
        xurl = intent.getStringExtra("url");
        title = intent.getStringExtra("name");

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
    public Drawable ToBitmapPicture(Bitmap bitmapX){
        Drawable drawable = new BitmapDrawable(bitmapX);
        return drawable;
    }

    private void Getsongs(){
    }


    @Override
    public void onDestroy() {
        if(timer!=null)
            timer.cancel();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mCircleAnimator.end();
        }
        super.onDestroy();
    }
    //需要改变适配尺寸的时候，在重写这两个方法
    @Override
    public boolean isBaseOnWidth() {
        return false;
    }
    @Override
    public float getSizeInDp() {
        return 640;
    }

}
