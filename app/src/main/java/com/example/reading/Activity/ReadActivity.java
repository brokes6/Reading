package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reading.Bean.BookComment;
import com.example.reading.Bean.BookCommentVo;
import com.example.reading.Bean.User;
import com.example.reading.Fragment.AudioFrequency;
import com.example.reading.Fragment.VideoFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.adapter.BookCommentAdapter;
import com.example.reading.databinding.ReadbookBinding;
import com.example.reading.util.FileCacheUtil;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static com.example.reading.MainApplication.getContext;


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
    private List<BookComment> bookComments=new ArrayList<>();
    private User userData;
    private BookCommentAdapter bookCommentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userData= FileCacheUtil.getUser(getContext());
        Log.i(TAG, "onCreate:"+userData.getAccount());
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        bookCommentAdapter=new BookCommentAdapter(ReadActivity.this,bookComments);
        binding.detailPageLvComment.setAdapter(bookCommentAdapter);
    }

    public String getDataId(){
        Intent intent = getIntent();
        Log.d(TAG, "getDataId: 当前书籍id为:"+intent.getStringExtra("id"));
        return intent.getStringExtra("id");
    }
    public String gettoken(){
        String token = userData.getToken();
        Log.d(TAG, "gettoken: 当前用户的token为"+token);
        return token;
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

    public void handlerComments(BookCommentVo commentVo){
        List<BookComment>comments=commentVo.getComments();
        if (comments==null||comments.size()==0){
            //1
            Log.i(TAG, "handlerComments:暂时没有更多评论");
            return;
        }
        bookComments.addAll(comments);
        bookCommentAdapter.notifyDataSetChanged();
    }
}
