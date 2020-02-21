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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.reading.R;
import com.example.reading.Bean.BookComment;
import com.example.reading.ToolClass.Video;
import com.example.reading.databinding.AudioBinding;
import com.example.reading.util.FragmentBackHandler;
import com.example.reading.util.RequestStatus;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weavey.loading.lib.LoadingLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private boolean isSeekbarChaning;//互斥变量，防止进度条和定时器冲突。
    private int duration2;
    private int position;
    private String video;
    int current = 0;
    String date1,bid,img,token;
    private FragmentBackHandler backInterface;
    BookComment bookComment = new BookComment();
    ReadActivity activity;
    String music_path;
    String url = "http://117.48.205.198/xiaoyoudushu/findBookById?";
    String Aurl;
    public ArrayList<Map<String, Object>> list = new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.loadingLayout.setStatus(LoadingLayout.Success);
                    binding.BookName.setText(bookComment.getBname());
                    binding.author.setText(bookComment.getAuthor());
                    binding.authorBookimg.setImageURL(bookComment.getBimg());
                    binding.PlaybackOperation.setVisibility(View.GONE);
                    EventBus.getDefault().post(new Video(null,null,1));
                    Toast.makeText(getContext(),"该书籍暂无音频",Toast.LENGTH_SHORT).show();
                    break;
                case RequestStatus.FAILURE:
                    binding.loadingLayout.setStatus(LoadingLayout.Empty);
                    EventBus.getDefault().post(new Video(null,null,1));
                    Toast.makeText(getContext(),"获取数据失败，请稍后尝试",Toast.LENGTH_SHORT).show();
                    break;
                case RequestStatus.AUDIO:
                    binding.BookName.setText(bookComment.getBname());
                    binding.author.setText(bookComment.getAuthor());
                    binding.authorBookimg.setImageURL(bookComment.getBimg());
                    EventBus.getDefault().post(new Video(null,null,1));
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(music_path);
                                Log.d(TAG, "handleMessage: url为"+music_path);
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
                    break;
                case RequestStatus.VIDEO:
                    binding.loadingLayout.setStatus(LoadingLayout.Empty);
                    EventBus.getDefault().post(new Video(video,video_img,0));
                    break;
                case RequestStatus.AUDIO_AND_VIDEO:
                    binding.BookName.setText(bookComment.getBname());
                    binding.author.setText(bookComment.getAuthor());
                    binding.authorBookimg.setImageURL(bookComment.getBimg());
                    EventBus.getDefault().post(new Video(video,video_img,0));
                    Thread thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(music_path);
                                Log.d(TAG, "handleMessage: url为"+music_path);
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
        binding.detailPageDoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Hhhh(BookComment bookComment) {

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
        if (bookComment.getMusic_path()==null){

        }
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
        //设置AlertDialog长度
        alertDialog2.getWindow().setLayout(300,200);
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
                    JSONObject bookdata = new JSONObject(data);
                    type = bookdata.getInt("type");
                    Log.d(TAG, "JsonJX: 当前type为"+type);
                    switch (type){
                        case 100:
                            //100为音频
                            //获取音频
                            String BookAudioData = bookdata.getString("audio");
                            JSONObject BookAudio = new JSONObject(BookAudioData);
                            music_path=BookAudio.getString("url");
                            //获取数据
                            String bookname=bookdata.getString("bname");
                            String bookauthor=bookdata.getString("author");
                            String bookimg=bookdata.getString("bimg");
                            bookComment.setAuthor(bookauthor);
                            bookComment.setBimg(bookimg);
                            bookComment.setBname(bookname);
                            Message mes=new Message();
                            mes.what= RequestStatus.AUDIO;
                            handler.sendMessage(mes);
                            break;
                        case 200:
                            //200为视频
                            //获取视频
                            String BookVideoData = bookdata.getString("video");
                            JSONObject BookVideo = new JSONObject(BookVideoData);
                            video = BookVideo.getString("url");
                            video_img = BookVideo.getString("img");
                            bookComment.setImg(video_img);
                            bookComment.setVideo_path(video);
                            Message mes2=new Message();
                            mes2.what=RequestStatus.VIDEO;
                            handler.sendMessage(mes2);
                            break;
                        case 300:
                            //视频
                            String BookVideoData3 = bookdata.getString("video");
                            JSONObject BookVideo3 = new JSONObject(BookVideoData3);
                            video = BookVideo3.getString("url");
                            video_img = BookVideo3.getString("img");
                            bookComment.setImg(video_img);
                            bookComment.setVideo_path(video);
                            //音频
                            String BookAudioData3 = bookdata.getString("audio");
                            JSONObject BookAudio3 = new JSONObject(BookAudioData3);
                            music_path=BookAudio3.getString("url");
                            String bookname3=bookdata.getString("bname");
                            String bookauthor3=bookdata.getString("author");
                            String bookimg3=bookdata.getString("bimg");
                            bookComment.setAuthor(bookauthor3);
                            bookComment.setBimg(bookimg3);
                            bookComment.setBname(bookname3);
                            Message mes3=new Message();
                            mes3.what=RequestStatus.AUDIO_AND_VIDEO;
                            handler.sendMessage(mes3);
                            break;
                        case 0:
                            String bookname4=bookdata.getString("bname");
                            String bookauthor4=bookdata.getString("author");
                            String bookimg4=bookdata.getString("bimg");
                            bookComment.setAuthor(bookauthor4);
                            bookComment.setBimg(bookimg4);
                            bookComment.setBname(bookname4);
                            Message mes4=new Message();
                            mes4.what=RequestStatus.SUCCESS;
                            handler.sendMessage(mes4);
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


