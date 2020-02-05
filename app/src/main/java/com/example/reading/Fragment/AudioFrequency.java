package com.example.reading.Fragment;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.reading.R;
import com.example.reading.databinding.AudioBinding;
import com.example.reading.util.BackHandlerHelper;
import com.example.reading.util.FragmentBackHandler;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * 阅读书籍页面
 */
public class AudioFrequency extends Fragment{
    private AlertDialog alertDialog2;
    private Timer timer;//定时器
    AudioBinding binding;
    private MediaPlayer mediaPlayer;
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private int duration2;
    private int position;
    int current = 0;
    private FragmentBackHandler backInterface;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.audio,container,false);
        //默认加载
//        binding.loadingLayout.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource("https://sharefs.yun.kugou.com/202002041847/7699ffa3e00895db46b1a3cb33bab4a5/G008/M0A/08/1D/SA0DAFUJqaiAPe4nAD0g7lyrfw4875.mp3");
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                duration2 = mediaPlayer.getDuration() / 1000;
                position = mediaPlayer.getCurrentPosition();
                binding.tvStart.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvStart.setText(calculateTime(position / 1000));
                        binding.tvEnd.setText(calculateTime(duration2));
                    }
                });
            }
        });
        thread.start();
        return binding.getRoot();
    }
    public void initView(){
        binding.authorBookimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        //绑定监听器，监听拖动到指定位置
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration2 = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int position = mediaPlayer.getCurrentPosition();//获取当前播放的位置
                binding.tvStart.setText(calculateTime(position / 1000));//开始时间
                binding.tvEnd.setText(calculateTime(duration2));//总时长
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = false;
                mediaPlayer.seekTo(seekBar.getProgress());//在当前位置播放
                binding.tvStart.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });
        binding.doubleSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleAlertDialog(getView());
            }
        });
    }
    public void initData(){
    }
    @Override
    public void onDestroy() {
        if(timer!=null)
            timer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
    public void play(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            Toast.makeText(getContext(),"停止播放",Toast.LENGTH_SHORT).show();
        }else{
            mediaPlayer.start();
            int duration = mediaPlayer.getDuration();//获取音乐总时间
            binding.seekBar.setMax(duration);//将音乐总时间设置为Seekbar的最大值
            timer = new Timer();//时间监听器
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!isSeekbarChaning){
                        binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            },0,50);
            Toast.makeText(getContext(),"开始播放",Toast.LENGTH_SHORT).show();
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
    public void showSingleAlertDialog(View view){
        final String[] items = {"0.75", "1.0", "1.25", "1.5"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("选择倍速");
        alertBuilder.setSingleChoiceItems(items, current, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                current = i;
                binding.doubleSpeedNum.setText(items[i]);
                setPlayerSpeed(Float.parseFloat(items[i]));
                Log.d(TAG, "onClick: -----------------------"+items[i]);
            }
        });

        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog2.dismiss();
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog2.dismiss();
            }
        });

        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }
    private void setPlayerSpeed(float speed){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
            playbackParams .setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
            play();
        }
    }
}
