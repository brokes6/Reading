package com.example.reading.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.reading.Activity.ReadActivity;
import com.example.reading.Bean.PostComment;
import com.example.reading.R;
import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.ToolClass.Video;
import com.example.reading.adapter.CommentExpandAdapter;
import com.example.reading.databinding.AudioBinding;
import com.example.reading.util.FragmentBackHandler;
import com.example.reading.util.PostHitoryUtil;
import com.example.reading.util.RequestStatus;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weavey.loading.lib.LoadingLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * 阅读书籍页面
 */
public class AudioFrequency extends Fragment{
    int  type;
    String video_img;
    int code;
    private ProgressBar progressBar;
    private BottomSheetDialog dialog;
    private AlertDialog alertDialog2;
    private Timer timer;//定时器
    AudioBinding binding;
    private MediaPlayer mediaPlayer;
    private CommentExpandAdapter commentExpandAdapter;
    private List<PostComment> comments=new ArrayList<>();
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private int duration2;
    private int position;
    int current = 0;
    String date1,bid,token;
    private FragmentBackHandler backInterface;
    BookDetailsBean bookDetailsBean = new BookDetailsBean();
    ReadActivity activity;
    String url = "http://117.48.205.198/xiaoyoudushu/findBookById?";
    String Aurl;
    public ArrayList<Map<String, Object>> list = new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.loadingLayout.setStatus(LoadingLayout.Success);
                    binding.BookName.setText(bookDetailsBean.getBname());
                    binding.author.setText(bookDetailsBean.getAuthor());
                    binding.authorBookimg.setImageURL(bookDetailsBean.getBimg());
                    binding.PlaybackOperation.setVisibility(View.GONE);
                    EventBus.getDefault().post(new Video(null,null,1));
                    Toast.makeText(getContext(),"该书籍暂无音频,视频",Toast.LENGTH_SHORT).show();
                    binding.authorBookimg.setEnabled(false);
                    PostHitoryUtil.saveSearchHistory(String.valueOf(bid),getActivity());
                    break;
                case RequestStatus.FAILURE:
                    binding.loadingLayout.setStatus(LoadingLayout.Empty);
                    EventBus.getDefault().post(new Video(null,null,1));
                    Toast.makeText(getContext(),"获取数据失败，请稍后尝试",Toast.LENGTH_SHORT).show();
                    break;
                case RequestStatus.AUDIO:
                    binding.BookName.setText(bookDetailsBean.getBname());
                    binding.author.setText(bookDetailsBean.getAuthor());
                    binding.authorBookimg.setImageURL(bookDetailsBean.getBimg());
                    String musicPath=bookDetailsBean.getAudio().getUrl();
                    EventBus.getDefault().post(new Video(null,null,1));
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(musicPath);
                                Log.d(TAG, "handleMessage: url为"+musicPath);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        binding.loadingLayout.setStatus(LoadingLayout.Success);
                                    }
                                });
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
                    PostHitoryUtil.saveSearchHistory(String.valueOf(bid),getActivity());
                    break;
                case RequestStatus.VIDEO:
                    binding.loadingLayout.setStatus(LoadingLayout.Empty);
                    EventBus.getDefault().post(bookDetailsBean.getVideo());
                    PostHitoryUtil.saveSearchHistory(String.valueOf(bid),getActivity());
                    break;
                case RequestStatus.AUDIO_AND_VIDEO:
                    String musicPath1=bookDetailsBean.getAudio().getUrl();
                    binding.BookName.setText(bookDetailsBean.getBname());
                    binding.author.setText(bookDetailsBean.getAuthor());
                    binding.authorBookimg.setImageURL(bookDetailsBean.getBimg());
                    EventBus.getDefault().post(bookDetailsBean.getVideo());
                    Thread thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(musicPath1);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        binding.loadingLayout.setStatus(LoadingLayout.Success);
                                    }
                                });
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
                    thread1.start();
                    PostHitoryUtil.saveSearchHistory(String.valueOf(bid),getActivity());
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
        binding = DataBindingUtil.inflate(inflater, R.layout.audio,container,false);
        //默认加载
        binding.loadingLayout.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        Getsongs();
        return binding.getRoot();
    }
    public void initView(){
        binding.xpaly.setOnClickListener(new View.OnClickListener() {
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
        /*commentExpandAdapter=new CommentExpandAdapter(getContext(),)*//*
        binding.detailPageLvComment.setAdapter();*/
    }
    public void initData(){
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Hhhh(BookDetailsBean bookDetailsBean) {

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
        if (bookDetailsBean.getAudio().getUrl()==null){

        }
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            binding.xpaly.setImageResource(R.mipmap.xpause);
            binding.xplayText.setText("播放");
            Toast.makeText(getContext(),"停止播放",Toast.LENGTH_SHORT).show();
        }else{
            mediaPlayer.start();
            binding.xpaly.setImageResource(R.mipmap.xxplay);
            binding.xplayText.setText("暂停");
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
                binding.doubleSpeedNum.setText(items[i]+"倍速");
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
        //设置AlertDialog长度
        alertDialog2.getWindow().setLayout(950,900);

    }
    private void setPlayerSpeed(float speed){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
            playbackParams .setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
            play();
        }
    }
    private void Getsongs(){
        list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Aurl = url+"token="+token+"&"+"bid="+bid;
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(Aurl)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    date1 = response.body().string();
                    Log.d(TAG, " 服务器返回的数据为："+date1);
                    JsonJX(date1);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void JsonJX(String Data) {
        if(Data !=null){
            try{
                JSONObject object = new JSONObject(Data);
                code = object.getInt("code");
                if (code==1) {
                    String data = object.getString("data");
                    Gson gson=new Gson();
                    bookDetailsBean=gson.fromJson(data,new TypeToken<BookDetailsBean>(){}.getType());
                    ((ReadActivity)getContext()).handlerComments(bookDetailsBean);
                    int type=bookDetailsBean.getType();
                    Log.d(TAG, "JsonJX: 当前type为"+type);
                    Message mes=new Message();
                    switch (type){
                        case 100:
                            //获取音频
                            mes.what= RequestStatus.AUDIO;
                            handler.sendMessage(mes);
                            break;
                        case 200:
                            //获取视频
                            mes.what=RequestStatus.VIDEO;
                            handler.sendMessage(mes);
                            break;
                        case 300:
                            //视频和音频
                            mes.what=RequestStatus.AUDIO_AND_VIDEO;
                            handler.sendMessage(mes);
                            break;
                        case 0:
                            mes.what=RequestStatus.SUCCESS;
                            handler.sendMessage(mes);
                    }
                }else{
                    Message mes=new Message();
                    mes.what=RequestStatus.FAILURE;
                    handler.sendMessage(mes);
                    return;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }

    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bid = (((ReadActivity) activity).getDataId());
        token = (((ReadActivity) activity).gettoken());
        if (bid !=null){
            Log.d(TAG, "activity返回的书籍id为"+bid);

        }else{
            Toast.makeText(getContext(),"服务器出错",Toast.LENGTH_SHORT).show();
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }

        }
    }


