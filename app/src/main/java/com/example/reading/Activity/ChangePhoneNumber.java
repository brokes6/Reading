package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.reading.Bean.User;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.databinding.ChangePhoneNumberBinding;
import com.example.reading.util.FileCacheUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.reading.MainApplication.getContext;

public class ChangePhoneNumber extends BaseActivity implements View.OnClickListener{
    ChangePhoneNumberBinding binding;
    private User userData;
    int REQUEST_CODE_ACTIVITY1 =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.change_phone_number);
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
    private void initView(){
        binding.setBack.setOnClickListener(this);
        binding.yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back:
                finish();
                break;
            case R.id.yes:
                if (isMobileNO(binding.phoneNumber.getText().toString())){
                    userData.setPhone_number(binding.phoneNumber.getText().toString());
                    FileCacheUtil.updateUser(userData, getContext());
                    Intent intent = new Intent(ChangePhoneNumber.this, Personal.class);
                    startActivityForResult(intent, REQUEST_CODE_ACTIVITY1);
                    finish();
                }else{
                    Toast.makeText(ChangePhoneNumber.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
