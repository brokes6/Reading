package com.example.reading.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.reading.R;
import com.example.reading.Bean.ProgramBean;
import com.example.reading.databinding.ProgramBinding;
import com.example.reading.adapter.ProgramAdapter;
import com.example.reading.util.RequestStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProgramFragment extends Fragment {
    private static final String TAG = "ProgramFragment";
    String Data;
    ProgramBinding binding;
    int code;
    ProgramAdapter programAdapter;
    private List<ProgramBean> programBeanArrayList;
    int Page = 1;
    List<ProgramBean> programBindings;
    String AUrl = "http://117.48.205.198/xiaoyoudushu/findMusicProgram?currentPage=";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    programAdapter.notifyDataSetChanged();
                    break;
                case RequestStatus.FAILURE:
                    break;
//                case RequestStatus.INCREASE:
//                    programAdapter.notifyDataSetChanged();
//                    break;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.program,container,false);
        initView();
        initData();
        analysis(AUrl+Page);
        return binding.getRoot();
    }
    public void initView(){
        binding.refreshLayout.setEnableRefresh(false);
        //设置 Footer 为 经典样式
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        programAdapter = new ProgramAdapter(getActivity());
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.RecommendRecycleview.setLayoutManager(lm);
        binding.RecommendRecycleview.setAdapter(programAdapter);
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++Page;
                analysis(AUrl+Page);
                //加了这一句反而不行了。。。。吐了
//                programAdapter.add(programBindings);
                programAdapter.notifyDataSetChanged();
                binding.refreshLayout.finishLoadMore(true);//加载完成
                Log.d(TAG, "onLoadMore: 添加更多完成");
            }
        });
    }
    public void initData(){

    }
    private void analysis(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                /**
                 * 小悠之声
                 */
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try{
                    Response response = okHttpClient.newCall(request).execute();
                    Data = response.body().string();
                    Log.d(TAG, "小悠之声数据为: "+Data);
                    JsonJX(Data);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void JsonJX(String Data){
        if(Data!=null){
            try{
                JSONObject object = new JSONObject(Data);
                String data = object.getString("data");
                code = object.getInt("code");
                JSONObject object1data = new JSONObject(data);
                String musicPrograms = object1data.getString("musicPrograms");
                Log.d(TAG, "musicPrograms的数据为 :"+musicPrograms);
                if (code==1){
                    if(musicPrograms==null){
                        Message message = Message.obtain();
                        message.what = RequestStatus.FAILURE;
                        handler.sendMessage(message);
                    }else{
                        Gson gson = new Gson();
                        if (Page>1){
                            programBindings = gson.fromJson(musicPrograms,new TypeToken<List<ProgramBean>>(){}.getType());
                            Log.d(TAG, "小悠之声开始添加数据--"+musicPrograms);
                            Message message=Message.obtain();
                            message.what=RequestStatus.INCREASE;
                            handler.sendMessage(message);
                        }
                        List<ProgramBean> programBinding1s = gson.fromJson(musicPrograms,new TypeToken<List<ProgramBean>>(){}.getType());
                        programAdapter.setProgramAdapter(programBinding1s);
                        Message message=Message.obtain();
                        message.what=RequestStatus.SUCCESS;
                        handler.sendMessage(message);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
