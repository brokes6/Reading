package com.example.reading.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.reading.Fragment.UserBookCommentFragment;
import com.example.reading.Fragment.UserPostCommentFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.databinding.UserCommentActivityBinding;

import java.util.ArrayList;
import java.util.List;

public class UserCommentActivity extends BaseActivity {
    private UserCommentActivityBinding binding;
    private List<Fragment> fragmentList=new ArrayList<>();
    private String[] strings=new String[]{"书评","发帖"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.user_comment_activity);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
    }

    private void initView(){
        MyAdapter adapter=new MyAdapter(getSupportFragmentManager());
        fragmentList.add(new UserBookCommentFragment());
        fragmentList.add(new UserPostCommentFragment());
        binding.viewpager.setAdapter(adapter);
        binding.tabMode.setupWithViewPager(binding.viewpager);
    }
    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
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
