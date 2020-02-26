package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.reading.Bean.User;
import com.example.reading.Fragment.IntroduceFragment;
import com.example.reading.Fragment.ProgramFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.databinding.XiaoYouSoundBinding;
import com.example.reading.util.FileCacheUtil;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import static com.example.reading.Activity.ReadActivity.NUM_ITEMS;
import static com.example.reading.MainApplication.getContext;

/**
 * 小悠之声
 */
public class XiaoYouSound extends BaseActivity {
    private String[] strings = new String[]{"音乐","介绍"};
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    XiaoYouSoundBinding binding;
    AppBarLayout appBarLayout;
    int topLayoutHeight=-1;
    private User userData;
    volatile boolean flag;
    private static final String TAG = "XiaoYouSound";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this,R.layout.xiao_you_sound);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        userData= FileCacheUtil.getUser(getContext());
        fragmentList.add(new ProgramFragment());
        fragmentList.add(new IntroduceFragment());
        initView();
        initData();

    }
    public void initView(){
        binding.userName.setText("听友 "+userData.getUsername()+" 你好");
        binding.text3.getBackground().setAlpha(200);
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
        binding.Xback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
