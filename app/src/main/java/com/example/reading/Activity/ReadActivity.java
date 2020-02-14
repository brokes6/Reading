package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.reading.Fragment.AudioFrequency;
import com.example.reading.Fragment.VideoFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.Bean.BookComment;
import com.example.reading.databinding.ReadbookBinding;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;


/**
 * 阅读书籍页面
 */
public class ReadActivity extends BaseActivity {
    private static final String TAG = "ReadActivity";
    Fragment fragment1;
    AudioFrequency audioFrequency =new AudioFrequency();
    ReadbookBinding binding;
    static final int NUM_ITEMS = 2;
    private String[] strings = new String[]{"音 频","视 频"};
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    BookComment bookComment = new BookComment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.readbook);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        fragmentList.add(new AudioFrequency());
        fragmentList.add(new VideoFragment());
        initView();
        initData();

    }

    public void initView(){
        MyAdapter fragmentAdater = new  MyAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(fragmentAdater);
        binding.tabMode.setupWithViewPager(binding.viewpager);
    }
    public void initData(){
    }

    public String getDataId(){
        Intent intent = getIntent();
        Log.d(TAG, "getDataId: 当前书籍id为:"+intent.getStringExtra("id"));
        return intent.getStringExtra("id");
    }



    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }
    }
//    @Override
//    public void onBackPressed() {
//        if (!BackHandlerHelper.handleBackPress(this)) {
//            super.onBackPressed();
//        }
//    }
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        super.onConfigurationChanged(newConfig);
    }

}
