package com.example.reading;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.reading.Activity.BaseActivity;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.Fragment.HomeFragment;
import com.example.reading.Fragment.MyFragment;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener{
    private static final String TAG = "MainActivity";
    private BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    private MyFragment myFragment;
    private HomeFragment homeFragment;
    private CommunityFragment communityFragment;
    private long homeExitTime=0;
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
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
                .addItem(new BottomNavigationItem(R.mipmap.community, "分类"))
                .addItem(new BottomNavigationItem(R.mipmap.personal, "个人设置"))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise(); //initialise 一定要放在 所有设置的最后一项

        setDefaultFragment();//设置默认导航栏
    }
    /**
     * 设置默认导航栏
     */
    private void setDefaultFragment() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction =  manager.beginTransaction();
        homeFragment = HomeFragment.newInstance();
        transaction.replace(R.id.tb,homeFragment);
        transaction.commit();
    }
    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        num = position;
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager manager=getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = manager.beginTransaction();
        switch (position) {
            case 0:
                Log.i(TAG, "onTabSelected: 我mhome被点击了啊");
                if (homeFragment == null) {
                    Log.i(TAG, "onTabSelected:进入");
                    homeFragment = HomeFragment.newInstance();
                }

                transaction.replace(R.id.tb,homeFragment);
                if ((System .currentTimeMillis() - homeExitTime) < 2000) {
                    //弹出提示，可以有多种方式
                }
                homeExitTime=System.currentTimeMillis();
                break;
            case 1:
                if (communityFragment == null) {
                    communityFragment = CommunityFragment.newInstance("分类");
                    Log.i(TAG, "onTabSelected: 开始创建mscanfragment");
                }
                transaction.replace(R.id.tb,communityFragment);
                break;
            case 2:
                if (myFragment == null) {
                    myFragment = MyFragment.newInstance("个人");
                }
                transaction.replace(R.id.tb,myFragment);
                break;
            default:
                break;
        }
        transaction.commit();
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
}
