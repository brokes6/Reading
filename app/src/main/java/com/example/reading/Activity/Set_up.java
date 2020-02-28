package com.example.reading.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.DataCleanManager;
import com.example.reading.databinding.SetUpBinding;


public class Set_up extends BaseActivity implements View.OnClickListener{
        SetUpBinding binding;
        private String cacheSize;
        private static final String TAG = "Set_up";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this,R.layout.set_up);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //获取参数
        binding.setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
        setDate();
    }
    private void initView(){
            binding.clear.setOnClickListener(this);
            binding.help.setOnClickListener(this);
            binding.userAgreement.setOnClickListener(this);
            binding.privacy.setOnClickListener(this);
    }
    private void setDate(){
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear:
                DataCleanManager.clearAllCache(Set_up.this);
                Toast.makeText(Set_up.this,"已清除",Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:
                Intent intent1=new Intent(Set_up.this, UserFeedBack.class);
                startActivity(intent1);
                break;
            case  R.id.user_agreement:
                Intent intent2=new Intent(Set_up.this, UserAgreement.class);
                startActivity(intent2);
                break;
            case R.id.privacy:
                Intent intent3=new Intent(Set_up.this, PrivacyPolicy.class);
                startActivity(intent3);
                break;

        }
    }
}
