package com.example.reading.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Bean.CommentDetailBean;
import com.example.reading.Bean.ReplyDetailBean;
import com.example.reading.Bean.User;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.adapter.ReplyAdapter;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.NetWorkUtil;
import com.example.reading.util.PostTemplateInterface;
import com.example.reading.util.RequestStatus;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
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

public class MoerReply extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MoerReply";
    public static final int HANDLER_DATA=1;
    public static final int HANDLER_DATA_COMMENT=3;
    public static final String REPLY_REQUEST_URL="http://106.54.134.17/app/getNewReplys";
    private ProgressBar progressBar;
    private Button loadButton;
    private TextView loadTextView;
    private BottomSheetDialog dialog;
    private MyImageView userimg;
    private TextView username;
    private TextView text;
    private TextView floor;
    private TextView Time;
    private TextView see;
    private LinearLayout back;
    private User userData;
    private TextView content;
    private int postId;
    private int commentId;
    private int userId;
    private boolean createNew;
    private TextView bt_comment;
    private RecyclerView recyclerView;
    private ReplyAdapter replyAdapter;
    private SmartRefreshLayout refreshLayout;
    private LinearLayout loadLayout;
    private RelativeLayout contentLayout;
    private AtomicInteger startPage=new AtomicInteger(1);
    private final AtomicBoolean hasMore = new AtomicBoolean(true);
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_DATA:
                    loadLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    Log.i(TAG, "handleMessage: 开始设置咯");
                    replyAdapter.setReplyDetailBeans((List<ReplyDetailBean>) msg.obj);
                    replyAdapter.notifyDataSetChanged();
                    break;
                case PostTemplateInterface.CANCEL_PROGRESS:
                    progressBar.setVisibility(View.GONE);
                    break;
                case HANDLER_DATA_COMMENT:
                    loadLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    CommentDetailBean bean= (CommentDetailBean) msg.obj;
                    username.setText(bean.getUsername());
                    userimg.setCacheImageURL(bean.getUimg());
                    text.setText(bean.getContent());
                    Time.setText(DateTimeUtil.handlerDateTime(bean.getCcreateTime()));
                    floor.setText(String.valueOf(bean.getFloor()));
                    replyAdapter.setReplyDetailBeans(bean.getReplyVoList());
                    replyAdapter.notifyDataSetChanged();
                    break;
                case RequestStatus.NO_NETWORK:
                    loadTextView.setText("加载回复失败，请检查网络后重新尝试...");
                    loadButton.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moer_reply);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        initView();
        initdata();
        click();
    }
    private void initView() {
        see = findViewById(R.id.tiezi_see);
        bt_comment = findViewById(R.id.detail_page_do_comment);
        userimg = findViewById(R.id.moer_user_img);
        username = findViewById(R.id.moer_username);
        text = findViewById(R.id.moer_Text);
        floor = findViewById(R.id.moer_floor);
        Time = findViewById(R.id.more_time);
        back = findViewById(R.id.back);
        recyclerView=findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.loading);
        loadLayout=findViewById(R.id.loadLayout);
        contentLayout=findViewById(R.id.contentLayout);
        loadButton=findViewById(R.id.loadButton);
        loadTextView=findViewById(R.id.loadTextView);
        initRefreshLayout();
    }
    private void click(){
        bt_comment.setOnClickListener(this);
        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createNew){
                    Log.i(TAG, "onClick: ccc");
                    Intent intent=new Intent(MoerReply.this,PostDetails.class);
                    intent.putExtra("postId",postId);
                    startActivity(intent);
                    finish();
                }else {
                    finish();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initIntentData();
                loadButton.setVisibility(View.GONE);
                loadTextView.setText("正在加载回复中...");
            }
        });
    }
    private void initdata(){
        initIntentData();
        replyAdapter=new ReplyAdapter(this);
        recyclerView.setAdapter(replyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void initIntentData(){
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        createNew=intent.getBooleanExtra("new",false);
        Log.i(TAG, "initIntentData: 获得值"+createNew);
        commentId = intent.getIntExtra("cid", -1);
        postId=intent.getIntExtra("postId",-1);
        if(name==null) {
            getCommentDetails();
        }else {
            String data = intent.getStringExtra("data");
            String time1 = intent.getStringExtra("time");
            String url = intent.getStringExtra("url");
            userId = intent.getIntExtra("userId", -1);
            userData = FileCacheUtil.getUser(this);
            userimg.setImageURL(url);
            username.setText(name);
            text.setText(data);
            Time.setText(time1);
            getNewsReply();
        }
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.detail_page_do_comment){
            showCommentDialog();
        }
    }
    private void initRefreshLayout(){
        refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        initRefreshFootLayout();
    }
    /**
     *2019/10/16
     * 方法：弹出评论框
     */
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
                String content = commentText.getText().toString().trim();
                //后期需要检查token的值 查看是否被更改了喔
                if(!TextUtils.isEmpty(content)){
                    progressBar.setVisibility(View.VISIBLE);
                    addReply(content,userData.getToken(),userId,text.getText().toString(),commentId,username.getText().toString());
                    dialog.dismiss();
                    ReplyDetailBean detailBean = new ReplyDetailBean(userData.getUsername(),content,"刚刚");
                    replyAdapter.addReplyDetailsBean(detailBean);
                    Toast.makeText(MoerReply.this,"评论成功",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(MoerReply.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    public void getCommentDetails(){
        Request request=new Request.Builder()
                .url("http://106.54.134.17/app/findCommentById?commentId="+commentId)
                .build();
        OkHttpClient client=new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message=new Message();
                message.what= RequestStatus.NO_NETWORK;
                handler.sendMessage(message);
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Log.i(TAG, "onResponse: "+responseData);
                try {
                    JSONObject jsonObject=new JSONObject(responseData);
                    int code=jsonObject.getInt("code");
                    if(code==0){
                        return;
                    }
                    Gson gson=new Gson();
                    CommentDetailBean bean=gson.fromJson(jsonObject.getString("data"),new TypeToken<CommentDetailBean>(){}.getType());
                    Message message=new Message();
                    message.what=HANDLER_DATA_COMMENT;
                    message.obj=bean;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getNewsReply(){
        new Thread(){
            @Override
            public void run() {
                Request request=new Request.Builder()
                        .url(REPLY_REQUEST_URL+"?startPage="+startPage+"&commentId="+commentId+"&token="+userData.getToken())
                        .build();
                OkHttpClient okHttpClient=new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message=new Message();
                        message.what= RequestStatus.NO_NETWORK;
                        handler.sendMessage(message);
                        return;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                        Log.i(TAG, "onResponse: "+responseData);
                        try {
                            JSONObject jsonObject=new JSONObject(responseData);
                            int code=jsonObject.getInt("code");
                            if(code==0){
                                hasMore.set(false);
                                return;
                            }
                            startPage.incrementAndGet();
                            Gson gson=new Gson();
                            List<ReplyDetailBean> beans=gson.fromJson(jsonObject.getString("data"),new TypeToken<List<ReplyDetailBean>>(){}.getType());
                            Message message=new Message();
                            message.what=HANDLER_DATA;
                            message.obj=beans;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }
    private void addReply(String content,String token,int commentUserId,String commentContent,int cid,String username){
        //修改回复 设置参数
        Log.i(TAG, "addReply: 被回复人的用户id为="+commentUserId);
        RequestBody requestBody = new FormBody.Builder()
                .add("content",content)
                .add("tcuid", String.valueOf(commentUserId))
                .add("token",token)
                .add("pid", String.valueOf(postId))
                .add("commentContent",commentContent)
                .add("cid",String.valueOf(cid))
                .add("username",username)
                .build();
        final Request request = new Request.Builder()
                .url("http://106.54.134.17/app/addReply")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure:失败呃");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String dataStr = response.body().string();
                System.out.println("帖子数据"+dataStr);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(dataStr);
                    int code = jsonObject.getInt("code");
                    if(code==0){
                        Log.i(TAG, "onResponse:失败咯");
                        return;
                    }
                    Log.i(TAG, "onResponse:信息"+jsonObject.getString("msg"));
                    Message message=new Message();
                    message.what=PostTemplateInterface.CANCEL_PROGRESS;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initRefreshFootLayout(){
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        final TextView tv = refreshLayout.getLayout().findViewById(ID_TEXT_TITLE);
        final ImageView iv2 = refreshLayout.getLayout().findViewById(ID_IMAGE_PROGRESS);
        final AtomicBoolean net = new AtomicBoolean(true);
        //设置多监听器，包括顶部下拉刷新、底部上滑刷新
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            /**
             * 根据上拉的状态，设置文字，并且判断条件
             * @param refreshLayout
             * @param oldState
             * @param newState
             */
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

            /**
             * 添加是否可以加载更多数据的条件
             * @param refreshLayout
             */
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (hasMore.get()) {
                    getNewsReply();
                }
                refreshLayout.finishLoadMore(1000); //这个记得设置，否则一直转圈
            }

            /**
             *  在这里根据不同的情况来修改加载完成后的提示语
             * @param footer
             * @param success
             */
            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
                super.onFooterFinish(footer, success);
                if (net.get() == false) {
                    tv.setText("请检查网络设置");
                } else if(hasMore.get()) {
                    tv.setText("加载完成");
                } else {
                    tv.setText("没有更多消息拉");
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
}
