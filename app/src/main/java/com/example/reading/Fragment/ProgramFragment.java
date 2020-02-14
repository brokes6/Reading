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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    programAdapter.notifyDataSetChanged();
                    break;
                case RequestStatus.FAILURE:
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
        binding = DataBindingUtil.inflate(inflater, R.layout.program,container,false);
        initView();
        initData();
        analysis();
        return binding.getRoot();
    }
    public void initView(){
        programAdapter = new ProgramAdapter(getActivity());
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.RecommendRecycleview.setLayoutManager(lm);
        binding.RecommendRecycleview.setAdapter(programAdapter);
    }
    public void initData(){

    }
    private void analysis(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                /**
                 * 小悠之声
                 */
                Request request = new Request.Builder()
                        .url("http://117.48.205.198/xiaoyoudushu/findMusicProgram?token=wRAUt%252FEoacAdpCmj2VsoJg%253D%253D&currentPage=1")
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
                        List<ProgramBean> programBindings = gson.fromJson(musicPrograms,new TypeToken<List<ProgramBean>>(){}.getType());
                        programAdapter.setProgramAdapter(programBindings);
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
