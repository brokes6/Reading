package com.example.reading.ToolClass;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reading.util.ScreenAdapterUtil;
import com.example.reading.util.StatusBarUtil;

public abstract class XBaseActivity extends AppCompatActivity {
    private static final String TAG = "XBaseActivity";
    int sum= 0;
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
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

