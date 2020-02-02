package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.reading.R;
import com.example.reading.databinding.ReadbookBinding;
/**
 * 阅读书籍页面
 */
public class ReadActivity extends AppCompatActivity {
    ReadbookBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.readbook);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        initView();
        initData();
    }
    public void initView(){

    }
    public void initData(){

    }
}
