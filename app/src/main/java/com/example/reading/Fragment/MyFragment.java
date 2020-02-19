package com.example.reading.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.reading.Activity.Set_up;
import com.example.reading.R;
import com.example.reading.databinding.MyfragmentBinding;

import me.jessyan.autosize.internal.CustomAdapt;

public class MyFragment extends Fragment implements CustomAdapt {
    private View view;
    MyfragmentBinding binding;
    private static final String TAG = "MyFragment";
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
        if(view==null) {
            Log.d(TAG, "onCreateView: "+"MyFragment"+"开始构建");
            binding = DataBindingUtil.inflate(inflater,R.layout.myfragment,container,false);
            initView();
            initData();
        }
        return binding.getRoot();
    }
    private void initView(){
        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Set_up.class);
                startActivity(intent);
            }
        });

    }
    private void initData(){

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
