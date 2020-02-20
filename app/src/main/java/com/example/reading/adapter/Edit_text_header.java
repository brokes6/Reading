package com.example.reading.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.reading.R;


/**
 * 创建于2019/10/29 20:43🐎
 */
public class Edit_text_header extends LinearLayout {
    public Edit_text_header(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.edit_text_header, this);
    }
}
