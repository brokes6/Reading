package com.example.reading.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.reading.Fragment.UserBookCommentFragment;
import com.example.reading.Fragment.UserPostCommentFragment;
import com.example.reading.R;
import com.example.reading.databinding.UserCommentActivityBinding;

import java.util.ArrayList;
import java.util.List;

public class UserCommentActivity extends AppCompatActivity {
    private UserCommentActivityBinding binding;
    private List<Fragment> fragmentList=new ArrayList<>();
    private String[] strings=new String[]{"书籍读后感","帖子评论"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.user_comment_activity);
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
