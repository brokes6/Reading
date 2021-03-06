package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.reading.R;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.databinding.XplayMusicBinding;
import com.example.reading.util.FastBlurUtil;
import com.example.reading.util.RequestStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qzs.android.fuzzybackgroundlibrary.Fuzzy_Background;
import com.weavey.loading.lib.LoadingLayout;


import net.qiujuer.genius.blur.StackBlur;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import me.jessyan.autosize.internal.CustomAdapt;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.reading.MainApplication.getContext;


public class XPlayMusic extends XBaseActivity implements CustomAdapt,View.OnClickListener {
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
    //音频焦点
    private AudioManager mAudioManager;
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
                        //设置图片
                        imageLoader.displayImage(ximg,binding.playImg,options);
//                        binding.playImg.setImageURL(ximg);
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
    private ImageLoader imageLoader;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.loading)          // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.image_error)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.image_error)       // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                binding.actPlayTimeStart.setText(calculateTime(position / 1000));
                binding.actPlayTimeEnt.setText(calculateTime(duration2));
            }
        });
        thread.start();

    }

    public void initView(){
        imageLoader= ImageLoader.getInstance();
        Intent intent = getIntent();
        ximg = intent.getStringExtra("img");
        Log.d(TAG, "initView: 返回的图片为"+ximg);
        xurl = intent.getStringExtra("url");
        title = intent.getStringExtra("name");
        binding.audioTime.setVisibility(View.GONE);
        binding.actSuspend.setOnClickListener(this);
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

        binding.audioTiming.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.act_suspend:
                play();
                break;
            case R.id.audio_timing:
                //1、使用Dialog、设置style
                final Dialog dialog = new Dialog(this,R.style.DialogTheme);
                View view = View.inflate(this,R.layout.timing_dialog,null);
                dialog.setContentView(view);
                Window window = dialog.getWindow();
                //设置弹出位置
                window.setGravity(Gravity.BOTTOM);
                //设置弹出动画
                window.setWindowAnimations(R.style.main_menu_animStyle);
                //设置对话框大小
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
                dialog.findViewById(R.id.time5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDown(300000);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.time10).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDown(600000);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.time20).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDown(1200000);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.time30).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDown(1800000);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.time_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    public void play(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            binding.actSuspend.setImageResource(R.mipmap.audio_state_pause);
            mCircleAnimator.pause();
            fristplay = true;
        }else{
            mAudioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(mAudioFocusChange , AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
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
                        try{
                            binding.actProgress.setProgress(mediaPlayer.getCurrentPosition());
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                }
            },0,50);
        }
    }

    private void countDown(int num) {
        CountDownTimer timer = new CountDownTimer(num, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.audioTime.setVisibility(View.VISIBLE);
                binding.audioTime.setText("正在倒计时");
            }
            @Override
            public void onFinish() {
                Toast.makeText(XPlayMusic.this,"倒计时已到!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();
    }

    //计算播放时间
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

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChange = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange){
                case AudioManager.AUDIOFOCUS_GAIN:
                    //当其他应用申请焦点之后又释放焦点会触发此回调
                    //可重新播放音乐
                    Log.d(TAG, "AUDIOFOCUS_GAIN");
                    mediaPlayer.start();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //长时间丢失焦点,当其他应用申请的焦点为 AUDIOFOCUS_GAIN 时，
                    //会触发此回调事件，例如播放 QQ 音乐，网易云音乐等
                    //通常需要暂停音乐播放，若没有暂停播放就会出现和其他音乐同时输出声音
                    Log.d(TAG, "AUDIOFOCUS_LOSS");
                    stop();
                    //释放焦点，该方法可根据需要来决定是否调用
                    //若焦点释放掉之后，将不会再自动获得
                    mAudioManager.abandonAudioFocus(mAudioFocusChange);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //短暂性丢失焦点，当其他应用申请 AUDIOFOCUS_GAIN_TRANSIENT 或
                    //AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE 时，
                    //会触发此回调事件，例如播放短视频，拨打电话等。
                    //通常需要暂停音乐播放
                    mediaPlayer.pause();
                    binding.actSuspend.setImageResource(R.mipmap.audio_state_pause);
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //短暂性丢失焦点并作降音处理
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
            }
        }
    };
    private void stop() {
        if (mediaPlayer != null) {//mediaplayer 是MediaPlayer的 instance
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();//stop后下次重新播放要首先进入prepared状态
                mediaPlayer.seekTo(0);//须将播放时间设置到0；这样才能在下次播放是重新开始，否则会继续上次播放
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        if (mAudioManager!=null){
            mAudioManager.abandonAudioFocus(mAudioFocusChange);
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
