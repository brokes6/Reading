package com.example.reading.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.reading.R;

public class CommunityFragment extends Fragment {
    private View view;
    public static CommunityFragment newInstance(String param1) {
        CommunityFragment fragment = new CommunityFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(view==null){
        view = inflater.inflate(R.layout.communityfragment, container, false);
        initData();
        initView();
        }
        return view;
    }
    private void initView(){

    }
    private void initData(){

    }
}
