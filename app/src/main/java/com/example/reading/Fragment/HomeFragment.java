package com.example.reading.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.reading.R;
import com.example.reading.databinding.HomefragmentBinding;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    HomefragmentBinding binding;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;
    private MyImageLoader mMyImageLoader;
    private int phoneHeight=-1;
    public static HomeFragment newInstance() {
        HomeFragment fragment=new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.homefragment,container,false);
            initData();
            initView();
        return binding.getRoot();
    }
    private void initView(){
        mMyImageLoader = new MyImageLoader();
        //设置样式，里面有很多种样式可以自己都看看效果
        binding.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器   CIRCLE_INDICATOR_TITLE
        binding.banner.setImageLoader(mMyImageLoader);
        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
        binding.banner.setBannerAnimation(Transformer.ZoomOutSlide);
        //轮播图片的文字   ZoomOutSlide
        binding.banner.setBannerTitles(imageTitle);
        //设置轮播间隔时间
        binding.banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是true
        binding.banner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        binding.banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置图片加载地址
        binding.banner.setImages(imagePath)
                //轮播图的监听
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Toast.makeText(getActivity(), "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();
                    }
                })
                //开始调用的方法，启动轮播图。
                .start();
    }
    private void initData(){
        phoneHeight=new DisplayMetrics().heightPixels;
        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        imagePath.add(R.drawable.english_club);
        imagePath.add(R.drawable.english_club1);
        imagePath.add(R.drawable.english_club2);
        imagePath.add(R.drawable.english_club3);
        imageTitle.add("测试图片1");
        imageTitle.add("测试图片2");
        imageTitle.add("测试图片3");
        imageTitle.add("测试图片4");
    }
    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
        }
    }
}
