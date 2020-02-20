package com.example.reading;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.jpush.android.api.JPushInterface;


/**
 * 描述：
 * 作者：HMY
 * 时间：2016/5/13
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
/*        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);*/
        Log.i(TAG, "onCreate: 被调用");
        initImageLoader();
    }

    public static Context getContext(){
        return context;
    }
    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }
}
