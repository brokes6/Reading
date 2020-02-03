package com.example.reading.Activity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.reading.MainActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.databinding.LoginBinding;

import me.jessyan.autosize.internal.CustomAdapt;
/**
 * CustomAdapt 设置
 */
public class LoginActivity extends BaseActivity implements CustomAdapt {
    LoginBinding binding;
    /**
     * 关于屏幕适配，需要适配的屏幕就继承CustomAdapt，然后实现两个接口就ok了
     * 不需要的则继承CancelAdapt
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.login);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        init();
        initData();
    }
    private void init(){
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
    public void goMainActivity(View v){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}

