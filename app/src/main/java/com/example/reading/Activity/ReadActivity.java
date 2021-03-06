package com.example.reading.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.reading.Bean.BookComment;
import com.example.reading.Bean.BookCommentVo;
import com.example.reading.Bean.BookDetails;
import com.example.reading.Bean.BookDetailsComment;
import com.example.reading.Bean.PostComment;
import com.example.reading.Bean.PostCommentVo;
import com.example.reading.Bean.User;
import com.example.reading.Fragment.AudioFrequency;
import com.example.reading.Fragment.VideoFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.ToolClass.XBaseActivity;
import com.example.reading.adapter.BookCommentAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.databinding.ReadbookBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.RequestStatus;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.weavey.loading.lib.LoadingLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;


/**
 * 阅读书籍页面
 */
public class ReadActivity extends XBaseActivity {
    protected static final float FLIP_DISTANCE = 50;
    private static final String TAG = "ReadActivity";
    Fragment fragment1;
    AudioFrequency audioFrequency =new AudioFrequency();
    ReadbookBinding binding;
    private int loveStatus;
    static final int NUM_ITEMS = 2;
    private int bid;
    private BroadcastReceiver broadcastReceiver;
    private String[] strings = new String[]{"音 频","视 频"};
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<BookComment> bookComments=new ArrayList<>();
    private User userData;
    private BookCommentAdapter bookCommentAdapter;
    private BottomSheetDialog dialog;
    private GestureDetector detector;
    private Handler handler=new Handler(Looper.getMainLooper());
    private int Page = 1;
    String commentNum;
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 100:
                    binding.commentStr.setText(commentNum);
                    break;
            }
        }
    };

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
        binding.loading.setStatus(LoadingLayout.Loading);
        fragmentList.add(new AudioFrequency());
        fragmentList.add(new VideoFragment());
        initView();
        initData();
        initExpandableListView();
        handlerComments(Page);
    }

    public void initView(){
        binding.refreshLayout.setEnableRefresh(false);
        //设置 Footer 为 经典样式
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Page++;
                handlerComments(Page);
                bookCommentAdapter.notifyDataSetChanged();
                binding.refreshLayout.finishLoadMore(1000);//加载完成
                Log.d(TAG, "onLoadMore: 添加更多完成");
            }
        });

        MyAdapter fragmentAdater = new  MyAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(fragmentAdater);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled: position="+position);
                Log.i(TAG, "onPageScrolled: position="+positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.tabMode.setupWithViewPager(binding.viewpager);
        binding.detailPageDoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
        detector=new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float minMove = 120; // 最小滑动距离
                float minVelocity = 0; // 最小滑动速度
                float beginX = e1.getX();
                float endX = e2.getX();
                float beginY = e1.getY();
                float endY = e2.getY();

                if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) { // 左滑
                    int index=binding.viewpager.getCurrentItem();
                    if (index==0){
                        /*binding.viewpager.setCurrentItem(0);*/
                    }
                    return true;
                } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) { // 右滑
                    int index=binding.viewpager.getCurrentItem();
                    if (index==1){
                        /*binding.viewpager.setCurrentItem(1);*/
                    }
                    return true;
                }
             return false;
            }
        });
    }
    public void initData(){
        bookCommentAdapter=new BookCommentAdapter(ReadActivity.this,bookComments);
        binding.detailPageLvComment.setAdapter(bookCommentAdapter);
    }

    private void initExpandableListView(){
        binding.detailPageLvComment.setGroupIndicator(null);
        binding.detailPageLvComment.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return true;
            }
        });
        binding.detailPageLvComment.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //toast("展开第"+groupPosition+"个分组");

            }
        });
        binding.detailPageLvComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        binding.detailPageLvComment.setLongClickable(true);
    }
    public void setCommentNum(BookDetailsBean bookDetailsBean){
        commentNum = String.valueOf(bookDetailsBean.getCommentNum());
        Message mes=new Message();
        mes.what= 100;
        handler1.sendMessage(mes);
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

    public void handlerComments(int page){
        Map<String,String> params=UserUtil.createUserMap();
        params.put("bid", getDataId());
        params.put("currentPage",String.valueOf(page));
        StandardRequestMangaer.getInstance().get(RequestUrl.FIND_BOOK_NEW_COMMENT,new BaseCallBack<BookDetailsComment>(){
            @Override
            protected void OnRequestBefore(Request request) {

            }

            @Override
            protected void onFailure(Call call) {
                Toast.makeText(ReadActivity.this,"暂无评论",Toast.LENGTH_SHORT).show();
                binding.loading.setStatus(LoadingLayout.Success);
            }

            @Override
            protected void onSuccess(Call call, Response response, BookDetailsComment bookDetailsComment) {
                List<BookComment>comments = bookDetailsComment.getComments();
                if (comments==null||comments.size()==0){
                    binding.loading.setStatus(LoadingLayout.Success);
                    Log.i(TAG, "handlerComments:暂时没有更多评论");
                    return;
                }else{
                    bid = Integer.parseInt(getDataId());
                    bookComments.addAll(comments);
                    bookCommentAdapter.notifyDataSetChanged();
                    binding.loading.setStatus(LoadingLayout.Success);
                }
            }


            @Override
            protected void onResponse(Response response) {
            }

            @Override
            protected void onEror(Call call, int statusCode) {

            }

            @Override
            protected void inProgress(int progress, long total, int id) {

            }
        },params);
    }

    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this,R.style.BottomSheetEdit);
        final View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                //后期需要检查token的值 查看是否被更改了喔
                if(!TextUtils.isEmpty(commentContent)){
                    addComment(commentContent);
                    dialog.dismiss();
                }else {
                    Toast.makeText(ReadActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                    bt_comment.setEnabled(true);
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                    bt_comment.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void addComment(final String content){
        binding.loading.setStatus(LoadingLayout.Loading);
        Map<String,String> params=UserUtil.createUserMap();
        params.put("cbid", String.valueOf(getDataId()));
        params.put("content",content);
        Log.d(TAG, "addComment: ---------------"+params);
        StandardRequestMangaer.getInstance().post(RequestUrl.ADD_BOOK_COMMENT,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {
                    }

                    @Override
                    protected void onFailure(Call call) {
                        binding.loading.setStatus(LoadingLayout.Error);
                        Toast.makeText(ReadActivity.this, "网络异常，无法连接至服务器", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        binding.loading.setStatus(LoadingLayout.Success);
                        Toast.makeText(ReadActivity.this,"评论成功", Toast.LENGTH_SHORT).show();
                        bookCommentAdapter.addTheCommentData(new BookComment("刚刚",content,userData.getUsername(),userData.getUimg(),Integer.valueOf(s)));
                        binding.commentStr.setText(String.valueOf(Integer.valueOf(binding.commentStr.getText().toString())+1));
                    }

                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {

                    }

                    @Override
                    protected void inProgress(int progress, long total, int id) {

                    }
                },params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }
}
