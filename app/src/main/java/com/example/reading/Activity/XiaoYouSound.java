package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.reading.Fragment.IntroduceFragment;
import com.example.reading.Fragment.ProgramFragment;
import com.example.reading.Fragment.VideoFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.databinding.XiaoYouSoundBinding;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import static com.example.reading.Activity.ReadActivity.NUM_ITEMS;
/**
 * 小悠之声
 */
public class XiaoYouSound extends BaseActivity {
    private String[] strings = new String[]{"节目","介绍"};
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    XiaoYouSoundBinding binding;
    AppBarLayout appBarLayout;
    int topLayoutHeight=-1;
    volatile boolean flag;
    private static final String TAG = "XiaoYouSound";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.xiao_you_sound);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        fragmentList.add(new ProgramFragment());
        fragmentList.add(new IntroduceFragment());
        initView();
        initData();

    }
    public void initView(){
        MyAdapter fragmentAdater = new MyAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(fragmentAdater);
        binding.tablayoutReal.setupWithViewPager(binding.viewpager);

        binding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(verticalOffset == 0){
                    //完全展开状态
                }else if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
                    //折叠状态
                }else{
                    //中间状态
                }
            }
        });
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
