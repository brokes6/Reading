package com.example.reading.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.reading.R;

public class HomeFragment extends Fragment {
    private View view;
    public static HomeFragment newInstance() {
        HomeFragment fragment=new HomeFragment();
        Bundle args = new Bundle();
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
            view = inflater.inflate(R.layout.homefragment, container, false);
            initView();
            initData();
        }
        return view;
    }
    private void initView(){

    }
    private void initData(){

    }
}
