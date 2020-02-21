package com.example.reading.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.reading.Activity.History;
import com.example.reading.Activity.Set_up;
import com.example.reading.Bean.User;
import com.example.reading.R;
import com.example.reading.databinding.MyfragmentBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.RequestStatus;
import com.weavey.loading.lib.LoadingLayout;

import me.jessyan.autosize.internal.CustomAdapt;

public class MyFragment extends Fragment implements CustomAdapt {
    private View view;
    private User userData;
    MyfragmentBinding binding;
    private static final String TAG = "MyFragment";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.userName.setText(userData.getUsername());
                    if (userData.getUimg()!=null){
                        binding.userImg.setImageURL(userData.getUimg());
                        binding.myload.setStatus(LoadingLayout.Success);
                    }else{
                        binding.userImg.setImageResource(R.mipmap.userimg);
                        Log.d(TAG, "user"+"无头像");
                    }
                    break;
                case RequestStatus.FAILURE:
                    break;
            }
        }
    };
    public static MyFragment newInstance(String param1) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = DataBindingUtil.inflate(inflater,R.layout.myfragment,container,false);
            binding.myload.setStatus(LoadingLayout.Loading);
            userData= FileCacheUtil.getUser(getContext());
            initView();
            initData();
        return binding.getRoot();
    }
    private void initView(){
        binding.myhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), History.class);
                startActivity(intent);
            }
        });
        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Set_up.class);
                startActivity(intent);
            }
        });

    }
    private void initData(){
        if (userData.getUsername()==null){
            Log.d(TAG, "user无数据!");
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.SUCCESS;
            handler.sendMessage(mes);
        }
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
