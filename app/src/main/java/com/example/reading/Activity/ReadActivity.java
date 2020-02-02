package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

import com.example.reading.Fragment.AudioFrequency;
import com.example.reading.Fragment.VideoFragment;
import com.example.reading.R;
import com.example.reading.databinding.ReadbookBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读书籍页面
 */
public class ReadActivity extends AppCompatActivity {
    ReadbookBinding binding;
    static final int NUM_ITEMS = 2;
    private String[] strings = new String[]{"音频","视频"};
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.readbook);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
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
}
