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

import com.example.reading.Bean.CommentDetailBean;
import com.example.reading.Bean.Post;
import com.example.reading.Bean.ReplyDetailBean;
import com.example.reading.Bean.User;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.CommentExpandAdapter;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.NetWorkUtil;
import com.example.reading.util.PostHitoryUtil;
import com.example.reading.util.PostTemplateInterface;
import com.example.reading.util.RequestStatus;
import com.example.reading.view.CommentExpandableListView;
import com.example.reading.view.NineGridTestLayout;
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
import java.util.LinkedList;
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

public class PostDetails extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "PostDetails";
    private TextView bt_comment;
    private CommentExpandableListView expandableListView;
    private CommentExpandAdapter adapter;
    private int postId;
    private int commentPage=1;
    private int loveStatus=0,collectionStatus=0;
    private List<CommentDetailBean> commentsList=new ArrayList<>();
    private BottomSheetDialog dialog;
    private TextView username,dateTime,content,message,loveNumStr,commentStr,loadTextView,collectionStr;
    private LinearLayout back;
    private MyImageView userImg;
    private ImageView loveNum,collection;
    private LinearLayout messageLayout;
    private LinearLayout commentLayout;
    private NineGridTestLayout nineGridTestLayout;
    private LinearLayout loveLayout;
    private LinearLayout collectionLayout;
    private LinearLayout loadLayout;
    private RelativeLayout contentLayout;
    private ProgressBar progressBar;
    private SmartRefreshLayout refreshLayout;
    private NetWorkUtil netWorkUtil;
    private User userData;
    private NiceSpinner niceSpinner;
    private boolean commentFlag;
    private Button loadButton;
    private int postUserId;
    private List<String> spinnerData = new LinkedList<>(Arrays.asList("时间排序", "点赞排序"));
    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PostTemplateInterface.HANDLER_DATA:
                    final Post post = (Post) msg.obj;
                    String str=Html.fromHtml(post.getContent()).toString();
                    postUserId=post.getUid();
                    Log.i(TAG, "handleMessage:  postUserId="+postUserId);
                    String imgUrls=post.getImgUrl();
                    loveStatus=post.getStatus();
                    collectionStatus=post.getCollection();
                    username.setText(post.getUsername());
                    userImg.setImageURL(post.getUimg());
                    dateTime.setText(DateTimeUtil.handlerDateTime(post.getPcreateTime()));
                    content.setText(str);
                    commentStr.setText(String.valueOf(post.getCommentCount()));
                    if(imgUrls==null||imgUrls.trim().equals("")){
                        nineGridTestLayout.setVisibility(View.GONE);
                    }else {
                        nineGridTestLayout.setUrlList(Arrays.asList(imgUrls.split(",")));
                        nineGridTestLayout.setIsShowAll(post.isShowAll());
                    }
                    loveNumStr.setText(String.valueOf(post.getLoveCount()));
                    loveLayout.setOnClickListener(new View.OnClickListener() {
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
                            netWorkUtil.updatePostLove(postId,userData.getToken());
                        }
                    });
                    collectionLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handlerCollection();
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
                    nineGridTestLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nineGridTestLayout.setInfo(post.getContent(),String.valueOf(post.getLoveCount()),String.valueOf(post.getCommentCount()),post.getStatus(),post.getCollection(),post.getPid(),null);
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
/*                    if(index<6){
                        refreshLayout.setEnableLoadMore(false);
                        Log.i(TAG, "handleMessage: 不能够加载更多");
                    }*/
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
                    adapter.addTheCommentData((CommentDetailBean) msg.obj);
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
                    adapter.addTheReplyData(detailBean, position);
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
        userData= FileCacheUtil.getUser(this);
        postId = getPostId();
        initView();
        click();
        initDetailsLayout();
        initRefreshLayout();
        initLoadLayout();
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
        adapter = new CommentExpandAdapter(this,commentsList,userData.getToken());
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复
        expandableListView.setAdapter(adapter);
        initExpandableListView();
        getComments();
        progressBar=findViewById(R.id.progress);
        Log.i(TAG, "initView: userData="+userData);
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
                        commentFlag=false;
                        commentPage=1;
                        getComments();
                        break;
                    case 1:
                        adapter.clearAll();
                        commentFlag=true;
                        commentPage=1;
                        getComments();
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
                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
                Log.e(TAG, "onGroupClick: 当前的评论id>>>"+adapter.getCommentBeanList().get(groupPosition).getCid());
                showReplyDialog(groupPosition,-1);
                return true;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                if(childPosition==2) {
                    Log.i(TAG, "onChildClick: 点击查看更多");
                    CommentDetailBean bean=adapter.getCommentBeanList().get(groupPosition);
                    String name = bean.getUsername();
                    String data = bean.getContent();
                    String time = DateTimeUtil.handlerDateTime(bean.getCcreateTime());
                    String url = bean.getUimg();
                    int userId = bean.getUid();
                    Intent intent = new Intent(PostDetails.this, MoerReply.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("data",data);
                    intent.putExtra("url",url);
                    intent.putExtra("time",time);
                    intent.putExtra("name",name);
                    intent.putExtra("cid",adapter.getCommentBeanList().get(groupPosition).getCid());
                    Log.d(TAG, "名字为----------------"+name);
                    Log.d(TAG, "时间为----------------"+time);
                    Log.d(TAG, "内容为----------------"+data);
                    Log.d(TAG, "url为----------------"+url);
                    startActivity(intent);
                    return false;
                }
                Log.e(TAG, "onGroupClick: -----当前的评论id>>>" + adapter.getCommentBeanList().get(groupPosition).getCid());
                showReplyDialog(groupPosition,childPosition);
                return false;
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
                String commentContent = commentText.getText().toString().trim();
                //后期需要检查token的值 查看是否被更改了喔
                if(!TextUtils.isEmpty(commentContent)){
                    progressBar.setVisibility(View.VISIBLE);
                    addComment(commentContent,userData.getUsername(),content.getText().toString(),postUserId);
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
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }
        /**
         * by   czj 2019/10/23
         * 方法:弹出回复框
         */
    private void showReplyDialog(final int groupPosition,final int childPosition){
        String commentContent=null;
        String username=null;
        int commentUserId=0;
        int cid=0;
        if(childPosition==-1){
            CommentDetailBean detailBean=adapter.getCommentBeanList().get(groupPosition);
            commentContent=detailBean.getContent();
            commentUserId=detailBean.getUid();
            username=detailBean.getUsername();
            cid=detailBean.getCid();
        }else {
            CommentDetailBean commentDetailBean=adapter.getCommentBeanList().get(groupPosition);
            ReplyDetailBean detailBean=commentDetailBean.getReplyVoList().get(childPosition);
            commentContent=detailBean.getContent();
            commentUserId=detailBean.getUid();
            username=detailBean.getUsername();
            cid=commentDetailBean.getCid();
        }
        dialog = new BottomSheetDialog(this,R.style.BottomSheetEdit);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        Log.i(TAG, "showReplyDialog: view="+commentView);
        final EditText commentText = commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment =commentView.findViewById(R.id.dialog_comment_bt);
        commentText.setHint("回复 " + username+ " 的评论:");
        dialog.setContentView(commentView);
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());
        final int finalCommentUserId = commentUserId;
        final String finalCommentContent = commentContent;
        final int finalCid = cid;
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent =commentText.getText().toString();
                Log.i(TAG, "onClick:"+replyContent);
                if(!TextUtils.isEmpty(replyContent)){
                    dialog.dismiss();
                    addReply(replyContent,userData.getToken(), finalCommentUserId, finalCommentContent, finalCid,userData.getUsername(),groupPosition);
                }else {
                    Toast.makeText(PostDetails.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
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
                    }else {
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
        final Request request = new Request.Builder()
                .url(getRequestStr(1))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure:失败呃");
                Message message=new Message();
                message.what=PostTemplateInterface.NO_NETWORK;
                handler.sendMessage(message);
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
                    Gson gson = new Gson();
                    Post post = gson.fromJson(jsonObject.getString("data"),Post.class);
                    if(post==null){
                        Log.i(TAG, "onResponse: 解析json数据失败");
                        return;
                    }
                    handlerData(post);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initDetailsLayout(){
        netWorkUtil=new NetWorkUtil(this);
        username=findViewById(R.id.tiezi_username);
        dateTime=findViewById(R.id.tiezi_time);
        content=findViewById(R.id.tieze_Text);
        nineGridTestLayout=findViewById(R.id.layout_nine_grid);
        Log.i(TAG, "initDetailsLayout: ---------id=="+postId);
        loveNum=findViewById(R.id.loveNum);
        loveNumStr=findViewById(R.id.loveNumStr);
        loveLayout=findViewById(R.id.loveLayout);
        commentStr=findViewById(R.id.commentStr);
        getPostById();
    }
    private int getPostId(){
        Intent intent = getIntent();
        return intent.getIntExtra("postId",-1);
    }
    private void handlerData(Post post){
        Message message=new Message();
        message.what=PostTemplateInterface.HANDLER_DATA;
        message.obj=post;
        handler.sendMessage(message);
    }
    private void addComment(final String content, String username, String postContent, int puid){
        Log.i(TAG, "addComment: 文章长度"+postContent);
        if(postContent.length()>30) {
            postContent = postContent.substring(0, 30);
        }
        Log.i(TAG, "addComment: 长度="+postContent);
        RequestBody requestBody = new FormBody.Builder()
                .add("cpid", String.valueOf(postId))
                .add("content",content)
                .add("token",userData.getToken())
                .add("username",username)
                .add("postContent",postContent)
                .add("puid",String.valueOf(puid))
                .build();
        final Request request = new Request.Builder()
                .url(PostTemplateInterface.REQUEST_ADD_COMMENT_STR)
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
                Log.i(TAG, "onResponse:"+dataStr);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(dataStr);
                    int code = jsonObject.getInt("code");
                    if(code==0){
                        Log.i(TAG, "onResponse:失败咯");
                        return;
                    }
                    int floor =jsonObject.getInt("data");
                    CommentDetailBean detailBean = new CommentDetailBean(userData.getUsername(), content,"刚刚",userData.getUimg());
                    detailBean.setFloor(floor);
                    Message message=new Message();
                    message.what=PostTemplateInterface.NOTIFY_COMMENT;
                    message.obj=detailBean;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void addReply(final String content, String token, int commentUserId, final String commentContent, int cid, String username, final int position){
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
                Message message=new Message();
                message.what= RequestStatus.NO_NETWORK;
                handler.sendMessage(message);
                return;
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
                    ReplyDetailBean detailBean = new ReplyDetailBean(userData.getUsername(),content,"刚刚");
                    Message message=new Message();
                    message.what=PostTemplateInterface.NOTIFY_REPLY;
                    message.obj=detailBean;
                    message.arg1=position;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getComments(){
        final Request request =new Request.Builder()
                .url(getRequestStr(2))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    return;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.i(TAG, "onResponse: 正文"+responseStr);
                try {
                    JSONObject jsonObject = new JSONObject(responseStr);
                    int code=jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if(code==0){
                        Log.i(TAG, "onResponse: 返回评论失败:"+msg);
                        Message message = new Message();
                        commentsList=new ArrayList<>();
                        message.what=PostTemplateInterface.NOTIFY_NOCOMMENT;
                        handler.sendMessage(message);
                        return;
                    }
                    Gson gson = new Gson();
                    commentsList=gson.fromJson(jsonObject.getString("data"),new TypeToken<List<CommentDetailBean>>(){}.getType());
                    Message message = new Message();
                    message.what=PostTemplateInterface.NOTIFY;
                    handler.sendMessage(message);
                    commentPage++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void handlerCollection(){
        RequestBody requestBody=new FormBody.Builder()
                .add("postId", String.valueOf(postId))
                .add("token",userData.getToken())
                .build();
        Request request=new Request.Builder()
                .post(requestBody)
                .url(getRequestStr(3))
                .build();
        OkHttpClient client=new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData =response.body().string();
                Log.i(TAG, "onResponse: "+responseData);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(responseData);
                    int code =jsonObject.getInt("code");
                    String msg=jsonObject.getString("msg");
                    if(code==0){
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private String getRequestStr(int mode){
        String urlStr=null;
        String token=null;
        switch (mode){
            case 1:urlStr=PostTemplateInterface.REQUEST_POST_DETAILS_STR+"?postId="+postId;
                            break;
            case 2:urlStr=(commentFlag?PostTemplateInterface.REQUEST_COMMENT_POPULAR_STR:PostTemplateInterface.REQUEST_COMMENT_NEW_STR)+"?startPage="+commentPage+"&postId="+postId;
                            Log.i(TAG, "getRequestStr: postId="+postId);
                            break;
            case 3:urlStr=(collectionStatus==1?PostTemplateInterface.REQUEST_DELETE_COLLECTION:PostTemplateInterface.REQUEST_ADD_COLLECTION);
                            return urlStr;
            default:break;
        }
        token=userData.getToken();
        if(token==null) {
            return urlStr;
        }
        Log.i(TAG,urlStr);
        return urlStr+"&token="+token;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("loveNum",loveNumStr.getText().toString());
        intent.putExtra("talkNum",commentStr.getText().toString());
        intent.putExtra("status",loveStatus);
        intent.putExtra("collectionStatus",collectionStatus);
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
                if (mostTimes.get() < 3) {
                    getComments();
                    mostTimes.getAndIncrement();
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
                } else if (mostTimes.get() >= 3) {
                    tv.setText("没有更多消息拉");
                } else {
                    tv.setText("加载完成");
                    if (mostTimes.get() == 2) {
                        mostTimes.getAndIncrement();
                    }
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
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPostById();
                getComments();
                loadButton.setClickable(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CommunityFragment.SHOWIMAGEACTIVITY:
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
}
