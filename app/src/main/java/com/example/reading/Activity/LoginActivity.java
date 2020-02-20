package com.example.reading.Activity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.reading.MainActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.databinding.LoginBinding;
import com.example.reading.util.RequestStatus;

import java.util.Timer;
import java.util.TimerTask;

import me.jessyan.autosize.internal.CustomAdapt;
/**
 * CustomAdapt 设置
 */
public class LoginActivity extends BaseActivity implements CustomAdapt {
    String username,Account ,password;
    Boolean isChoice = false;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    LoginBinding binding;
    int key;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    private static final String TAG = "LoginActivity";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.login.setEnabled(true);
                    binding.login.setBackgroundResource(R.drawable.file_back_bule);
                    break;
                case RequestStatus.FAILURE:
                    binding.login.setBackgroundResource(R.drawable.file_back_bule_false);
                    binding.login.setEnabled(false);
                    break;
            }
        }
    };
    /**
     * 关于屏幕适配，需要适配的屏幕就继承CustomAdapt，然后实现两个接口就ok了
     * 不需要的则继承CancelAdapt
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this,R.layout.login);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sp.edit();
        AutomaticLogon();
        Logon();
        init();
        initData();
    }

    private void Logon(){
        String userAccount;
        String userPassword;
        userAccount = binding.loginUser.getText().toString();
        userPassword = binding.loginPassword.getText().toString();
        if (TextUtils.isEmpty(userAccount)){
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.SUCCESS;
            handler.sendMessage(mes);
        }
        if (TextUtils.isEmpty(userPassword)){
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.SUCCESS;
            handler.sendMessage(mes);
        }
    }

    private void init(){
        binding.loginUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //输入时的调用
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(timer != null)
                    timer.cancel();
                    Logon();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 1) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Logon();
                        }
                    }, DELAY);
                }
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.userInstructions.isChecked()){
                    goMainActivity();
                }else{
                    Toast.makeText(LoginActivity.this,"请同意用户须知",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initData(){
    }



    public void AutomaticLogon(){
        key = sp.getInt("key",0);
        if (key==0){

        }else{
            Log.d(TAG, "----------------AutomaticLogon:保存的key值为 "+key);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
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
    public void goMainActivity(){
        editor.putInt("key",2);
        editor.commit();
        Log.d(TAG, "goMainActivity: 已保存key值,key值为:"+sp.getInt("key",0));
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}

