package com.example.reading.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reading.Bean.BookComment;
import com.example.reading.Bean.BookCommentVo;
import com.example.reading.Bean.PostComment;
import com.example.reading.Bean.User;
import com.example.reading.Fragment.AudioFrequency;
import com.example.reading.Fragment.VideoFragment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.adapter.BookCommentAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.databinding.ReadbookBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
public class ReadActivity extends BaseActivity {
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
    private Handler handler=new Handler(Looper.getMainLooper());
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
        binding.detailPageDoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });

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

    public void handlerComments(BookDetailsBean bookDetailsBean){
        handler.post(new Runnable() {
            @Override
            public void run() {
                handlerBookInfo(bookDetailsBean);
                List<BookComment>comments=bookDetailsBean.getCommentVo().getComments();
                if (comments==null||comments.size()==0){
                    //1
                    Log.i(TAG, "handlerComments:暂时没有更多评论");
                    return;
                }
                bookComments.addAll(comments);
                bookCommentAdapter.notifyDataSetChanged();
            }
        });
    }
    public void handlerBookInfo(BookDetailsBean bookDetailsBean){
        binding.loveNumStr.setText(String.valueOf(bookDetailsBean.getLoveNum()));
        binding.commentStr.setText(String.valueOf(bookDetailsBean.getCommentNum()));
        bid=bookDetailsBean.getBid();
        loveStatus=bookDetailsBean.getLoveStatus();
        if(loveStatus==1){
            binding.loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_wanc));
        }else{
            binding.loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_black));
        }
        binding.loveNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loveStatus==1){
                    binding.loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_black));
                    loveStatus=0;
                    binding.loveNumStr.setText(String.valueOf(Integer.valueOf(binding.loveNumStr.getText().toString())-1));
                }else{
                    binding.loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_wanc));
                    loveStatus=1;
                    binding.loveNumStr.setText(String.valueOf(Integer.valueOf(binding.loveNumStr.getText().toString())+1));
                }
                handlerLove(bookDetailsBean.getBid());
            }
        });
    }

    private void handlerLove(int bid){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("bid", String.valueOf(bid));
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.HANDLER_BOOK_LOVE,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {

                    }

                    @Override
                    protected void onFailure(Call call) {

                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        Toast.makeText(ReadActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {
                        Toast.makeText(ReadActivity.this, "请检查网络后重试", Toast.LENGTH_SHORT).show();

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
        Map<String,String> params=UserUtil.createUserMap();
        params.put("cbid", String.valueOf(bid));
        params.put("content",content);
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.ADD_BOOK_COMMENT,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {
                    }

                    @Override
                    protected void onFailure(Call call) {
                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        Toast.makeText(ReadActivity.this, s, Toast.LENGTH_SHORT).show();
                        bookCommentAdapter.addTheCommentData(new BookComment("刚刚",content,"测试","http://image.biaobaiju.com/uploads/20180803/23/1533308847-sJINRfclxg.jpeg",Integer.valueOf(s)));
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
}
