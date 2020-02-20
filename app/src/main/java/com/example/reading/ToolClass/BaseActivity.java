package com.example.reading.ToolClass;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reading.Broadcast.LoginOutBroadcastReceiver;
import com.example.reading.ScreenAdaptation.DisplayCutoutDemo;
import com.example.reading.util.ActivityCollector;
import com.example.reading.util.ScreenAdapterUtil;
import com.sdx.statusbar.statusbar.StatusBarUtil;

import me.jessyan.autosize.internal.CustomAdapt;

/**
 * 创建于2019/10/30 16:35🐎
 */
public class BaseActivity extends AppCompatActivity{
    int sum= 0;
    private static final String TAG = "BaseActivity";
    protected LoginOutBroadcastReceiver locallReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建活动时，将其加入管理器中
        ActivityCollector.addActivity(this);
        if(Build.VERSION.SDK_INT>=28) {
            DisplayCutoutDemo displayCutoutDemo = new DisplayCutoutDemo(this);
            displayCutoutDemo.openFullScreenModel();
        }
        getStatusBarHeight(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        /**
         * 设置沉浸式
         * @param activity    上下文
         * @param isPadding   是否添加状态栏
         * @param isTextColor 状态栏字体颜色切换(true为浅色,false为深色)
         * @param colorId     状态栏背景色设置(设置为1会默认白色)
         */
        StatusBarUtil.setStutatusBar(this, true, true, 1);
        StatusBarUtil.setImageStutatusBar(this, false);
    }
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height","dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.d("刘海屏的高度---","**getStatusBarHeight**" + result);
        sum=result;
        return result;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Log.i(TAG, "setContentView: "+layoutResID);
        View view = LayoutInflater.from(this).inflate(layoutResID,null);
        Log.i(TAG, "setContentView: view="+view);
        //判断屏幕
        if(ScreenAdapterUtil.hasNotchScreen(this))
        {
//            view.setPadding(0,sum,0,0);
        }
        else
        {
            Log.d("ScreenAdapterUtil","你是常规屏");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gesoft.admin.loginout");
        locallReceiver = new LoginOutBroadcastReceiver();
        registerReceiver(locallReceiver, intentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();

        // 取消注册广播接收器
        unregisterReceiver(locallReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }

}
