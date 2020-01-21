package com.example.reading.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.example.reading.ScreenAdaptation.DisplayCutoutDemo;
import com.example.reading.util.ScreenAdapterUtil;

/**
 * 创建于2019/10/30 16:35🐎
 */
public class BaseActivity extends AppCompatActivity {
    int sum= 0;
    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=28) {
            DisplayCutoutDemo displayCutoutDemo = new DisplayCutoutDemo(this);
            displayCutoutDemo.openFullScreenModel();
        }
        getStatusBarHeight(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

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
}
