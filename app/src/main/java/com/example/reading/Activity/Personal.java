package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.reading.Bean.User;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.databinding.ActivityPersonalBinding;
import com.example.reading.util.FileCacheUtil;
import com.weavey.loading.lib.LoadingLayout;

import static com.example.reading.MainApplication.getContext;

public class Personal extends BaseActivity {
    private User userData;
    ActivityPersonalBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_personal);
        binding.loading.setStatus(LoadingLayout.Loading);
        userData= FileCacheUtil.getUser(getContext());
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
    public void  initView(){
        binding.puserimg.setImageURL(userData.getUimg());
        binding.pusername.setText(userData.getUsername());
        binding.loading.setStatus(LoadingLayout.Success);
        binding.setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
