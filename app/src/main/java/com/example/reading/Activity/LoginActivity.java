package com.example.reading.Activity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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

import com.example.reading.Bean.User;
import com.example.reading.MainActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.databinding.LoginBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.RequestStatus;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import me.jessyan.autosize.internal.CustomAdapt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * CustomAdapt 设置
 */
public class LoginActivity extends BaseActivity implements CustomAdapt {
    String username,account ,password;
    Boolean isChoice = false;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    LoginBinding binding;
    int key,code;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    //权限
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    User user= (User) msg.obj;
                    FileCacheUtil.clearData();
                    FileCacheUtil.setCache(user,LoginActivity.this,"USERDATA.txt",0);
                    editor.putInt("key",2);
                    editor.commit();
                    Log.d(TAG, "goMainActivity: 已保存key值,key值为:"+sp.getInt("key",0)+"下次将自动登录!");
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("username",user.getUsername());
                    intent.putExtra("uimg",user.getUimg());
                    startActivity(intent);
                    break;
                case RequestStatus.FAILURE:
                    binding.loginUser.setError("账号/密码错误");
                    binding.loginPassword.setError("账号/密码错误");
                    break;
                case RequestStatus.input:
                    binding.login.setEnabled(true);
                    binding.login.setBackgroundResource(R.drawable.file_back_bule);
                    break;
                case RequestStatus.Noinput:
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
        verifyStoragePermissions(this);
        binding = DataBindingUtil.setContentView(this,R.layout.login);
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
            mes.what=RequestStatus.Noinput;
            handler.sendMessage(mes);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.input;
            handler.sendMessage(mes);
        }
        if (TextUtils.isEmpty(userPassword)){
            Message mes=new Message();
            mes.what=RequestStatus.Noinput;
            handler.sendMessage(mes);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.input;
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
                    goLogin();
                }else{
                    Toast.makeText(LoginActivity.this,"请同意用户须知",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initData(){
    }

    private void goLogin(){
        boolean flag=false;
        account = binding.loginUser.getText().toString();
        password = binding.loginPassword.getText().toString();
        if(TextUtils.isEmpty(account)){
            binding.loginUser.setError("账号不能为空");
            flag=true;
        }
        if(TextUtils.isEmpty(password)){
            binding.loginPassword.setError("密码不能为空");
            flag=true;
        }if(flag){
            return;
        }
        Login(account,password);
    }

    private void Login(final String username, final String password){
        Toast.makeText(LoginActivity.this,"请稍等，正在登录",Toast.LENGTH_SHORT).show();
        RequestBody requestBody = new FormBody.Builder()
                .add("account",username)
                .add("password",password)
                .build();
        Request request =new Request.Builder()
                .post(requestBody)
                .url("http://117.48.205.198/xiaoyoudushu/login?")
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message=new Message();
                message.what=RequestStatus.FAILURE;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Login：登录成功");
                String UserData = response.body().string();
                Log.d(TAG, "Login：用户的信息为"+UserData);
                try{
                    JSONObject jsonObject =new JSONObject(UserData);
                    code = jsonObject.getInt("code");
                    if (code==0){
                        Message message=new Message();
                        message.what=RequestStatus.FAILURE;
                        handler.sendMessage(message);
                    }
                    Gson gson =new Gson();
                    User user=gson.fromJson(jsonObject.getString("data"), User.class);
                    Message message=new Message();
                    message.obj=user;
                    message.what=RequestStatus.SUCCESS;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

}

