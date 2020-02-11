package com.example.reading.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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
import com.example.reading.ToolClass.BookComment;
import com.example.reading.ToolClass.Video;
import com.example.reading.databinding.AudioBinding;
import com.example.reading.util.FragmentBackHandler;
import com.example.reading.util.RequestStatus;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
    String date1;
    String bid;
    private FragmentBackHandler backInterface;
    BookComment bookComment = new BookComment();
    ReadActivity activity;
    String music_path = "https://sharefs.yun.kugou.com/202002081817/3813c40ebddcde982ec510e16f3c57b3/G004/M08/16/03/pIYBAFS-a_aAcZRBAEGtSN5wixs886.mp3";
    public ArrayList<Map<String, Object>> list = new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.BookName.setText(bookComment.getBname());
                    binding.author.setText(bookComment.getAuthor());
                    binding.authorBookimg.setImageURL(bookComment.getBimg());
                    Log.d(TAG, "页面加载完成"+"当前页面信息为--"+"书名："+bookComment.getBname()+"--作者："+bookComment.getAuthor()+"--图片："+bookComment.getBimg());
                    break;
                case RequestStatus.FAILURE:
                    Toast.makeText(getContext(),"获取数据失败，请稍后尝试",Toast.LENGTH_SHORT).show();
                    break;
                case RequestStatus.AUDIO:
                    /**
                     * 到时候在Video多加俩个值，一个为视频图片，一个为有无视频
                     */
                    break;
                case RequestStatus.VIDEO:
                    EventBus.getDefault().post(new Video(video));//ssj,szq是我定义的两个string类型变量
                    binding.PlaybackOperation.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"该书籍暂无音频",Toast.LENGTH_SHORT).show();
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
//        binding.loadingLayout.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        Getsongs();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(music_path);
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
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url("http://117.48.205.198/xiaoyoudushu/findBookById?id="+bid)
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
                    JSONObject array = object.getJSONObject("data");
                    type = array.getInt("type");
                    if (type !=1){
                        //0为音频
                        music_path=array.getString("rurl");
                        Message mes=new Message();
                        mes.what= RequestStatus.AUDIO;
                        handler.sendMessage(mes);
                    }else{
                        //1为视频
                        video = array.getString("rurl");
                        bookComment.setVideo_path(video);
                        Message mes=new Message();
                        mes.what=RequestStatus.VIDEO;
                        handler.sendMessage(mes);
                    }
                        String bookname=array.getString("bname");
                        String bookauthor=array.getString("author");
                        String bookimg=array.getString("bimg");
                        bookComment.setAuthor(bookauthor);
                        bookComment.setBimg(bookimg);
                        bookComment.setBname(bookname);

//                        Map<String,Object> map=new HashMap<>();
//                        map.put("name",bookname);
//                        map.put("author",bookauthor);
//                        map.put("pic",bookimg);
//                        map.put("type",type);
//                        list.add(map);
                        Message mes=new Message();
                        mes.what=RequestStatus.SUCCESS;
                        handler.sendMessage(mes);

                }else{
                    Message mes=new Message();
                    mes.what=RequestStatus.FAILURE;
                    handler.sendMessage(mes);
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
        if (bid !=null){
            Log.d(TAG, "activity返回的书籍id为"+bid);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }

        }
    }


