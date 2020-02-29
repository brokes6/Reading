/*package com.example.reading.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.databinding.RecordPraiseBinding;

public class Record_praise extends BaseActivity {
    RecordPraiseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.record_praise);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
    }
    private void initView(){

    }
}*/
