package com.example.reading.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.example.reading.Bean.PostComment;
import com.example.reading.Bean.Post;
import com.example.reading.Bean.PostCommentVo;
import com.example.reading.Bean.PostDetailsBean;
import com.example.reading.Bean.ReplyDetailBean;
import com.example.reading.Bean.User;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.CommentExpandAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.NetWorkUtil;
import com.example.reading.util.PostHitoryUtil;
import com.example.reading.util.PostTemplateInterface;
import com.example.reading.util.RequestStatus;
import com.example.reading.util.UserUtil;
import com.example.reading.view.CommentExpandableListView;
import com.example.reading.view.StandardNineGridLayout;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.scwang.smartrefresh.layout.internal.InternalClassics.ID_IMAGE_PROGRESS;
import static com.scwang.smartrefresh.layout.internal.InternalClassics.ID_TEXT_TITLE;

public class PostDetails extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "PostDetails";
    private TextView bt_comment;
    private CommentExpandableListView expandableListView;
    private CommentExpandAdapter adapter;
    private int postId;
    private int commentPage=1;
    private int loveStatus=0,collectionStatus=0;
    private List<PostComment> commentsList=new ArrayList<>();
    private BottomSheetDialog dialog;
    private TextView username,dateTime,content,message,loveNumStr,commentStr,loadTextView,collectionStr;
    private LinearLayout back;
    private MyImageView userImg;
    private ImageView loveNum,collection;
    private LinearLayout messageLayout;
    private LinearLayout commentLayout;
    private StandardNineGridLayout standardNineGridLayout;
    private LinearLayout loveLayout;
    private LinearLayout collectionLayout;
    private LinearLayout loadLayout;
    private RelativeLayout contentLayout;
    private ProgressBar progressBar;
    private SmartRefreshLayout refreshLayout;
    private NetWorkUtil netWorkUtil;
    private NiceSpinner niceSpinner;
    private boolean commentFlag;
    private Button loadButton;
    private int postUserId;
    private int position;
    private int currentPage=1;
    private int total=-1;
    private int mode=RequestUrl.NEW;
    private List<String> spinnerData = new LinkedList<>(Arrays.asList("时间排序", "点赞排序"));
    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PostTemplateInterface.HANDLER_DATA:
                    final Post post = (Post) msg.obj;
                    String str=Html.fromHtml(post.getContent()).toString();
                    postUserId=post.getPuid();
                    Log.i(TAG, "handleMessage:  postUserId="+postUserId);
                    String imgUrls=post.getImgurl();
/*                    loveStatus=post.getStatus();
                    collectionStatus=post.getCollection();*/
                    username.setText(post.getUsername());
                    userImg.setImageURL(post.getUimg());
                    dateTime.setText(DateTimeUtil.handlerDateTime(post.getPcreateTime()));
                    content.setText(str);
                    commentStr.setText(String.valueOf(post.getCommentNum()));
                    if(imgUrls==null||imgUrls.trim().equals("")){
                        standardNineGridLayout.setVisibility(View.GONE);
                    }else {
                        standardNineGridLayout.setUrlList(Arrays.asList(imgUrls.split(",")));
                    }
                    loveNumStr.setText(String.valueOf(post.getLoveNum()));
                    collectionLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*handlerCollection();*/
                            if (collectionStatus==1){
                                collection.setImageDrawable(getResources().getDrawable(R.mipmap.shocang));
                                collectionStatus=0;
                                collectionStr.setText("未收藏");
                                Toast.makeText(PostDetails.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                            }else{
                                collection.setImageDrawable(getResources().getDrawable(R.mipmap.shocangwanc));
                                collectionStatus=1;
                                collectionStr.setText("已收藏");
                                Toast.makeText(PostDetails.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    if(loveStatus==1){
                        loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_wanc));
                    }else{
                        loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_black));
                    }
                    if(collectionStatus==1){
                        collection.setImageDrawable(getResources().getDrawable(R.mipmap.shocangwanc));
                        collectionStr.setText("已收藏");
                    }else{
                        collection.setImageDrawable(getResources().getDrawable(R.mipmap.shocang));
                        collectionStr.setText("未收藏");
                    }
                    contentLayout.setVisibility(View.VISIBLE);
                    loadLayout.setVisibility(View.GONE);
                    standardNineGridLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
/*
                            standardNineGridLayout.setInfo(post.getContent(),String.valueOf(post.getLoveCount()),String.valueOf(post.getCommentCount()),post.getStatus(),post.getCollection(),post.getPid(),null);
*/
                        }
                    });
                    PostHitoryUtil.saveSearchHistory(String.valueOf(postId),PostDetails.this);break;
                case PostTemplateInterface.CANCEL_PROGRESS:
                    progressBar.setVisibility(View.GONE);
                    break;
                case PostTemplateInterface.NOTIFY:
                    int index=adapter.getCommentBeanList().size();
                    adapter.setCommentBeanList(commentsList);
                    Log.e(TAG, "handleMessage: 长度"+index);
                    for(int i =index; i<commentsList.size()+index; i++){
                        expandableListView.expandGroup(i);
                    }
                    if(index<6){
                        refreshLayout.setEnableLoadMore(false);
                        Log.i(TAG, "handleMessage: 不能够加载更多");
                    }

                    messageLayout.setVisibility(View.VISIBLE);
                    message.setText("暂无更多");
                    message.setTextSize(14);
                    break;
                case PostTemplateInterface.NOTIFY_NOCOMMENT:
                    Log.i(TAG, "handleMessage: 喔有运行？");
                    commentLayout.setMinimumHeight(500);
                    refreshLayout.setEnableLoadMore(false);
                    messageLayout.setVisibility(View.VISIBLE);
                    break;
                case PostTemplateInterface.NOTIFY_COMMENT:
                    Toast.makeText(PostDetails.this,"评论成功",Toast.LENGTH_SHORT).show();
                    adapter.addTheCommentData((PostComment) msg.obj);
                    Log.i(TAG, "handleMessage: 评论处理");
                    int index1=adapter.getCommentBeanList().size();
                    commentStr.setText(String.valueOf(Integer.valueOf(commentStr.getText().toString())+1));
                    progressBar.setVisibility(View.GONE);
//                    if(index1>=6)
//                        messageLayout.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                case PostTemplateInterface.NO_NETWORK:
                        loadTextView.setText("网络不稳定，请重新刷新试试");
                        loadButton.setVisibility(View.VISIBLE);
                        loadButton.setClickable(true);
                        break;
                case PostTemplateInterface.NOTIFY_REPLY:
                    Toast.makeText(PostDetails.this,"回复成功",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    ReplyDetailBean detailBean= (ReplyDetailBean) msg.obj;
                    int position=msg.arg1;
                    expandableListView.expandGroup(position);
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.post_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        postId = getPostId();
        initView();
        click();
        initDetailsLayout();
        initRefreshLayout();
        initLoadLayout();
        getPostById();
    }
    //初始化
    private void initView() {
        //获取实列
        back = findViewById(R.id.back);
        userImg=findViewById(R.id.tieze_user_img);
        message = findViewById(R.id.message);
        messageLayout=findViewById(R.id.messageLayout);
        contentLayout=findViewById(R.id.contentLayout);
        commentLayout=findViewById(R.id.detail_page_comment_container);
        collectionLayout=findViewById(R.id.collectionLayout);
        collectionStr=findViewById(R.id.collectionStr);
        collection=findViewById(R.id.collection);
        expandableListView =findViewById(R.id.detail_page_lv_comment);
        bt_comment =findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener(this);
        adapter = new CommentExpandAdapter(this,commentsList);
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复
        expandableListView.setAdapter(adapter);
        initExpandableListView();
        progressBar=findViewById(R.id.progress);
        niceSpinner = findViewById(R.id.nice_spinner);
        niceSpinner.attachDataSource(spinnerData);
        niceSpinner.setBackgroundResource(R.drawable.textview_round_border);
        niceSpinner.setTextColor(Color.BLACK);
        niceSpinner.setTextSize(13);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        adapter.clearAll();
                        mode=RequestUrl.NEW;
                        currentPage=1;
                        getComments(mode);
                        break;
                    case 1:
                        adapter.clearAll();
                        mode=RequestUrl.POPULAR;
                        currentPage=1;
                        getComments(mode);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void click(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("loveNum",loveNumStr.getText().toString());
                intent.putExtra("talkNum",commentStr.getText().toString());
                intent.putExtra("status",loveStatus);
                setResult(CommunityFragment.POSTDETAILS,intent);
                finish();
            }
        });
    }
    private void initRefreshLayout(){
        refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        initRefreshFootLayout();
    }
    private void initExpandableListView(){
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return true;
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //toast("展开第"+groupPosition+"个分组");

            }
        });
    }
    //onOptionsItemSelected方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //重写onClick方法
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.detail_page_do_comment){
            showCommentDialog();
        }
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
                    Toast.makeText(PostDetails.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
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
                    bt_comment.setEnabled(false);
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        dialog.show();
    }
    private void getPostById(){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("pid", String.valueOf(postId));
        StandardRequestMangaer.getInstance().get(RequestUrl.FIND_POST_DEATILS, new BaseCallBack<PostDetailsBean>() {

            @Override
            protected void OnRequestBefore(Request request) {
                loadButton.setVisibility(View.GONE);
                Log.i(TAG, "OnRequestBefore: 正在加载");
                loadTextView.setText("网络波动...加载中...");
            }

            @Override
            protected void onFailure(Call call) {
                loadTextView.setText("该帖子已被删除！");
            }

            @Override
            protected void onSuccess(Call call, Response response, PostDetailsBean postDetailsBean) {
                contentLayout.setVisibility(View.VISIBLE);
                loadLayout.setVisibility(View.GONE);
                Log.i(TAG, "onSuccess: 获得帖子详细信息成功！"+postDetailsBean);
                Glide.with(PostDetails.this).load(postDetailsBean.getUimg()).into(userImg);
                username.setText(postDetailsBean.getUsername());
                content.setText(postDetailsBean.getContent());
                loveNumStr.setText(String.valueOf(postDetailsBean.getLoveNum()));
                commentStr.setText(String.valueOf(postDetailsBean.getCommentNum()));
                dateTime.setText(DateTimeUtil.handlerDateTime(postDetailsBean.getPcreateTime()));
                String imgUrls=postDetailsBean.getImgurl();
                if (!TextUtils.isEmpty(imgUrls)){
                    standardNineGridLayout.setUrlList(Arrays.asList(imgUrls.split(",")));
                    standardNineGridLayout.setVisibility(View.VISIBLE);
                }else {
                    standardNineGridLayout.setVisibility(View.GONE);
                }
                loveStatus=postDetailsBean.getLoveStatus();
                if (loveStatus==0){
                    loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_black));
                }else{
                    loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_wanc));
                }
                loveNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loveStatus==1){
                            loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_black));
                            loveStatus=0;
                            loveNumStr.setText(String.valueOf(Integer.valueOf(loveNumStr.getText().toString())-1));
                        }else{
                            loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_wanc));
                            loveStatus=1;
                            loveNumStr.setText(String.valueOf(Integer.valueOf(loveNumStr.getText().toString())+1));
                        }
                        handlerLove(postDetailsBean.getPid());
                    }
                });
                currentPage++;
                commentsList.addAll(postDetailsBean.getPostCommentVo().getComments());
                total=postDetailsBean.getPostCommentVo().getCount();
                adapter.notifyDataSetChanged();
            }

            @Override
            protected void onResponse(Response response) {

            }

            @Override
            protected void onEror(Call call, int statusCode) {
                Log.i(TAG, "OnRequestBefore: 正在加载");
                loadTextView.setText("网络好像不太好喔，请检查网络后重试");
                loadButton.setVisibility(View.VISIBLE);
            }

            @Override
            protected void inProgress(int progress, long total, int id) {

            }
        },params);
    }
    private void initDetailsLayout(){
        netWorkUtil=new NetWorkUtil(this);
        username=findViewById(R.id.tiezi_username);
        dateTime=findViewById(R.id.tiezi_time);
        content=findViewById(R.id.tieze_Text);
        standardNineGridLayout =findViewById(R.id.layout_nine_grid);
        Log.i(TAG, "initDetailsLayout: ---------id=="+postId);
        loveNum=findViewById(R.id.loveNum);
        loveNumStr=findViewById(R.id.loveNumStr);
        loveLayout=findViewById(R.id.loveLayout);
        commentStr=findViewById(R.id.commentStr);
    }
    private int getPostId(){
        Intent intent = getIntent();
        position=intent.getIntExtra("position",-1);
        return intent.getIntExtra("postId",-1);
    }
    private void handlerData(Post post){
        Message message=new Message();
        message.what=PostTemplateInterface.HANDLER_DATA;
        message.obj=post;
        handler.sendMessage(message);
    }
    private void addComment(final String content){
        Map<String,String> params=UserUtil.createUserMap();
        params.put("cpid", String.valueOf(postId));
        params.put("content",content);
        StandardRequestMangaer.getInstance().post(RequestUrl.ADD_POST_COMMENT,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {
                        loadLayout.setVisibility(View.VISIBLE);
                        loadButton.setVisibility(View.GONE);
                        Log.i(TAG, "OnRequestBefore: 正在加载");
                        loadTextView.setText("---正在上传评论中---");
                    }

                    @Override
                    protected void onFailure(Call call) {
                        loadTextView.setText("遇到未知原因,上传评论失败,请稍后重试");
                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        loadLayout.setVisibility(View.GONE);
                        Toast.makeText(PostDetails.this, s, Toast.LENGTH_SHORT).show();
                        adapter.addTheCommentData(new PostComment("刚刚",content,"测试","http://image.biaobaiju.com/uploads/20180803/23/1533308847-sJINRfclxg.jpeg",Integer.valueOf(s)));
                        commentStr.setText(String.valueOf(Integer.valueOf(commentStr.getText().toString())+1));
                    }

                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {
                        loadTextView.setText("网络好像不太好喔，请检查网络后重试");
                    }

                    @Override
                    protected void inProgress(int progress, long total, int id) {

                    }
                },params);
    }
    private void getComments(int mode){
        Map<String,String> params=UserUtil.createUserMap();
        params.put("pid", String.valueOf(postId));
        params.put("currentPage", String.valueOf(currentPage));
       StandardRequestMangaer.getInstance().get(mode==RequestUrl.NEW?RequestUrl.FIND_POST_NEW_COMMENT:RequestUrl.FIND_POST_POPULAR_COMMENT,new BaseCallBack<PostCommentVo>(){

                   @Override
                   protected void OnRequestBefore(Request request) {

                   }

                   @Override
                   protected void onFailure(Call call) {
                       Toast.makeText(PostDetails.this, "没有更多评论拉！", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   protected void onSuccess(Call call, Response response, PostCommentVo postCommentVo) {
                            commentsList.addAll(postCommentVo.getComments());
                            total=postCommentVo.getCount();
                            Toast.makeText(PostDetails.this, "获得评论成功！", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            currentPage++;
                   }

                   @Override
                   protected void onResponse(Response response) {

                   }

                   @Override
                   protected void onEror(Call call, int statusCode) {
                       Toast.makeText(PostDetails.this, "网络请求出错", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   protected void inProgress(int progress, long total, int id) {

                   }
               },params);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("position",position);
        intent.putExtra("loveNum",Integer.valueOf(loveNumStr.getText().toString()));
        intent.putExtra("commentNum",Integer.valueOf(commentStr.getText().toString()));
        intent.putExtra("loveStatus",loveStatus);
        setResult(CommunityFragment.POSTDETAILS,intent);
        finish();
    }
    private void initRefreshFootLayout(){
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        final TextView tv = refreshLayout.getLayout().findViewById(ID_TEXT_TITLE);
        final ImageView iv2 = refreshLayout.getLayout().findViewById(ID_IMAGE_PROGRESS);
        final AtomicBoolean net = new AtomicBoolean(true);
        final AtomicInteger mostTimes = new AtomicInteger(0);//假设只有三屏数据
        //设置多监听器，包括顶部下拉刷新、底部上滑刷新
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                switch (newState) {
                    case None:
                    case PullUpToLoad:
                        break;
                    case Loading:
                    case LoadReleased:
                        tv.setText("正在加载..."); //在这里修改文字
                        if (!NetWorkUtil.isNetworkConnected(getApplicationContext())) {
                            net.set(false);
                        } else {
                            net.set(true);
                        }
                        break;
                    case ReleaseToLoad:
                        tv.setText("release");
                        break;
                    case Refreshing:
                        tv.setText("refreshing");
                        break;
                }
            }


            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (currentPage*10 <total) {
                    Log.i(TAG, "onLoadMore: total="+total);
                    Log.i(TAG, "onLoadMore: currentpage*10="+currentPage*10);
                    getComments(mode);
                }
                refreshLayout.finishLoadMore(1000); //这个记得设置，否则一直转圈
            }


            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
                super.onFooterFinish(footer, success);
                if (net.get() == false) {
                    tv.setText("请检查网络设置");
                } else if (currentPage*10 >= total) {
                    tv.setText("没有更多消息拉");
                } else {
                    tv.setText("加载完成");
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Log.i(TAG, "onLoadMore: 下拉加载");
                refreshLayout.autoLoadMore();
            }
        });
    }
    private void initLoadLayout(){
        loadLayout=findViewById(R.id.loadLayout);
        loadTextView=findViewById(R.id.loadTextView);
        loadButton=findViewById(R.id.loadButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CommunityFragment.SHOWIMAGEACTIVITY:
                if(data==null){
                    return;
                }
                String loveData=data.getStringExtra("loveNum");
                String talkNumStr=data.getStringExtra("talkNum");
                int status=data.getIntExtra("status",0);
                int collectionStatus=data.getIntExtra("collectionStatus",0);
                loveNumStr.setText(loveData);
                commentStr.setText(talkNumStr);
                loveStatus=status;
                this.collectionStatus=collectionStatus;
                if(loveStatus==1){
                    loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_wanc));
                }else{
                    loveNum.setImageDrawable(getResources().getDrawable(R.mipmap.thumbs_up_black));
                }
                if(collectionStatus==1){
                    collection.setImageDrawable(getResources().getDrawable(R.mipmap.shocangwanc));
                    collectionStr.setText("已收藏");
                }else{
                    collection.setImageDrawable(getResources().getDrawable(R.mipmap.shocang));
                    collectionStr.setText("未收藏");
                }
        }
    }
    private void handlerLove(int pid){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("pid", String.valueOf(pid));
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.HANDLER_POST_LOVE,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {

                    }

                    @Override
                    protected void onFailure(Call call) {

                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        Toast.makeText(PostDetails.this, "操作成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {
                        Toast.makeText(PostDetails.this, "请检查网络后重试", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    protected void inProgress(int progress, long total, int id) {

                    }
                },params);
    }

}
