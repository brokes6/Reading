package com.example.reading.ToolClass;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import android.view.WindowManager;

import androidx.annotation.Nullable;

/**
 * 作用：状态栏透明化
 */

public class Transparent extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucent(this);
    }

    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
