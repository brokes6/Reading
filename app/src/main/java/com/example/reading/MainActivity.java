package com.example.reading;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.Fragment.HomeFragment;
import com.example.reading.Fragment.MyFragment;
import com.example.reading.ToolClass.BarHigh;
import com.example.reading.util.ActivityCollector;

import java.util.Calendar;

import me.jessyan.autosize.internal.CustomAdapt;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener,CustomAdapt {
    private static final String TAG = "MainActivity";
    private BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    private MyFragment myFragment;
    private HomeFragment homeFragment;
    private CommunityFragment communityFragment;
    private long firstTime = 0;
    private long homeExitTime=0;
    int num = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        init();
        inData();
        /**
         * bottomNavigation 设置
         */

        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        /** 导航基础设置 包括按钮选中效果 导航栏背景色等 */
        bottomNavigationBar
                .setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_FIXED)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */

                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                /**
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */
                .setActiveColor("#000000") //选中颜色
                .setInActiveColor("#6D6969") //未选中颜色
                .setBarBackgroundColor("#FFFFFF");//导航栏背景色

        /** 添加导航按钮 */
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.home, "首页"))
                .addItem(new BottomNavigationItem(R.mipmap.community, "社区"))
                .addItem(new BottomNavigationItem(R.mipmap.personal, "个人设置"))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise(); //initialise 一定要放在 所有设置的最后一项

        setDefaultFragment();//设置默认导航栏
        final BarHigh high = new BarHigh();
        bottomNavigationBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                bottomNavigationBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                high.setHeight(bottomNavigationBar.getMeasuredHeight());
                high.setH(bottomNavigationBar.getMeasuredHeight());
                Log.d("测试：", bottomNavigationBar.getMeasuredHeight()+","+bottomNavigationBar.getMeasuredWidth());
            }
        });
    }
    /**
     * 设置默认导航栏
     */
    private void setDefaultFragment() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction =  manager.beginTransaction();
        homeFragment = HomeFragment.newInstance();
        transaction.replace(R.id.ll_content,homeFragment);
        transaction.commit();
    }
    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        num = position;
        FragmentManager manager=getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.ll_content,homeFragment);
                }else{
                    transaction.show(homeFragment);
                }
//                transaction.replace(R.id.ll_content,homeFragment);
//                if ((System .currentTimeMillis() - homeExitTime) < 2000) {
//                    //弹出提示，可以有多种方式
//                }
//                homeExitTime=System.currentTimeMillis();
                break;
            case 1:
                if (communityFragment == null) {
                    communityFragment = new CommunityFragment();
                    transaction.add(R.id.ll_content,communityFragment);
                }else{
                    transaction.show(communityFragment);
                }
                break;
            case 2:
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    transaction.add(R.id.ll_content,myFragment);
                }else{
                    transaction.show(myFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 隐藏当前fragment
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction){
        if (homeFragment != null){
            transaction.hide(homeFragment);
        }

        if (communityFragment != null){
            transaction.hide(communityFragment);
        }
        if (myFragment != null){
            transaction.hide(myFragment);
        }
    }


    /**
     * 设置未选中Fragment 事务
     */
    @Override
    public void onTabUnselected(int position) {

    }

    /**
     * 设置释放Fragment 事务
     */
    @Override
    public void onTabReselected(int position) {

    }
    private void init(){

    }
    private void inData(){


    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else{
            ActivityCollector.finishAll();
        }
    }

    //需要改变适配尺寸的时候，在重写这两个方法
    @Override
    public boolean isBaseOnWidth() {
        return false;
    }
    @Override
    public float getSizeInDp() {
        return 640;
    }
}
