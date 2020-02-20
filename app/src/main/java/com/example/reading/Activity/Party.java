package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.reading.Bean.VideoDetails;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.VideoAdapter;
import com.example.reading.adapter.video_item;
import com.example.reading.databinding.ActivityPartyBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.weavey.loading.lib.LoadingLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*import cn.jzvd.Jzvd;*/
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.application.AppApplication.getContext;


/**
 * 小悠之声
 */

public class Party extends BaseActivity {
    private static final String TAG = "Party";
    ActivityPartyBinding binding;
    public ArrayList<Map<String, Object>> list = new ArrayList<>();
    public video_item video;
    String data;
    private VideoAdapter video_item_test;
    ActionBar actionBar;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 100:
//                    video=new video_item(Party.this,list);
//                    binding.videoView.setAdapter(video);
//                    Log.d(TAG, "handleMessage: 适配器完成链接");
                    video_item_test.notifyDataSetChanged();
                    binding.loading.setStatus(LoadingLayout.Success);
                    break;
                case 200:
                    Log.i("TAG","-------------"+data);
                    break;
                case 300:
                    Toast.makeText(Party.this,"数据是空的！",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_party);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        binding.loading.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        Getsongs();
    }
    public void initView(){
        //设置 Header式
        binding.refreshLayout.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        //设置 Footer样式
        binding.refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        binding.refreshLayout.setDisableContentWhenRefresh(true);
        video_item_test = new VideoAdapter(this);
        LinearLayoutManager im = new LinearLayoutManager(this);
        im.setOrientation(LinearLayoutManager.VERTICAL);
        binding.videoView.setLayoutManager(im);
        binding.videoView.setAdapter(video_item_test);


    }
    public void initData(){
        binding.Pback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void Getsongs(){
        list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request=new Request.Builder()
                        .url("http://117.48.205.198/xiaoyoudushu/findTeamPrograms?currentPage=1").build();
                try{
                    Response response = okHttpClient.newCall(request).execute();
                    data = response.body().string();
                    Message mes=new Message();
                    mes.what=200;
                    handler.sendMessage(mes);
                    JsonJX(data);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void JsonJX(String Date){
        if(Date!=null){
            try{
                JSONObject video_object = new JSONObject(Date);
                String data = video_object.getString("data");
                Log.d(TAG, "党建的data为: "+data);
                JSONObject data1 = new JSONObject(data);
                String teamPrograms = data1.getString("teamPrograms");
                Log.d(TAG, "teamPrograms的数据为:"+teamPrograms);
                int code = video_object.getInt("code");
                if (code ==1){
                    JSONArray array = data1.getJSONArray("teamPrograms");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String video_titie = object.getString("title");
                        Log.d(TAG, "video_titie: "+video_titie);
                        String video_url = object.getString("rurl");
                        Log.d(TAG, "video_url: "+video_url);
                        String video_description = object.getString("description");
                        Log.d(TAG, "video_description: "+video_description);
                        Gson gson = new Gson();
                        List<VideoDetails> videoDetails = gson.fromJson(teamPrograms,new TypeToken<List<VideoDetails>>() {}.getType());
                        video_item_test.setVideoAdapter(videoDetails);
                        Message mes=new Message();
                        mes.what=100;
                        handler.sendMessage(mes);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else{
            Message mes=new Message();
            mes.what=300;
            handler.sendMessage(mes);
        }
    }
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            ActionBar actionbar = getSupportActionBar();
            if (actionbar!=null){
                actionbar.hide();
            }
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

}
