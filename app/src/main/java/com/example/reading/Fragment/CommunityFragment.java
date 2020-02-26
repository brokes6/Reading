package com.example.reading.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.Party;
import com.example.reading.Activity.addPost;
import com.example.reading.Bean.Post;
import com.example.reading.R;
import com.example.reading.adapter.NineGridAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.weavey.loading.lib.LoadingLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import floatingactionbutton.FloatingActionsMenu;
import me.jessyan.autosize.AutoSizeConfig;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class CommunityFragment extends Fragment {
    private FloatingActionsMenu menu;
    private static final String TAG = "CommunityFragment";
    public static final int POSTDETAILS=1;
    public static final int ADDPOST=2;
    public static final int SHOWIMAGEACTIVITY=3;
    private List<Post> postList=new ArrayList<>();
    private View view;
    SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private NineGridAdapter adapter;
    LoadingLayout loading;
    private int oid;
    int Page = 1;
    private AtomicInteger integer=new AtomicInteger(1);
    public static CommunityFragment newInstance(String param1) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        AutoSizeConfig.getInstance().setCustomFragment(true);
        if(view==null){
        AutoSizeConfig.getInstance().setCustomFragment(true);
        view = inflater.inflate(R.layout.communityfragment, container, false);
        initView();
        initData();
        }
        return view;
    }
    //为什么呢
    private void initView(){
        loading = view.findViewById(R.id.loading);
        loading.setStatus(LoadingLayout.Loading);
        smartRefreshLayout = view.findViewById(R.id.refreshLayout);
        smartRefreshLayout.setEnableRefresh(false);
        //设置 Footer 为 经典样式
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        recyclerView=view.findViewById(R.id.list_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new NineGridAdapter(this);
        recyclerView.setAdapter(adapter);
        //上拉刷新事件监听
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++Page;
                findPostByPage(Page);
                adapter.notifyDataSetChanged();
                smartRefreshLayout.finishLoadMore(true);//加载完成
                Log.d(TAG, "onLoadMore: 添加更多完成");
            }
        });
        //悬浮按钮
        final floatingactionbutton.FloatingActionButton actionA = view.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_addPost = new Intent(getActivity(), addPost.class);
                intent_addPost.putExtra("oid",oid);
                startActivityForResult(intent_addPost,ADDPOST);
            }
        });
    }
    private void initData(){
        findPostByPage(Page);

    }

    private void findPostByPage(int page){
        Map<String,String> map= UserUtil.createUserMap();
        map.put("currentPage", String.valueOf(page));
        StandardRequestMangaer.getInstance().get(RequestUrl.FIND_POST, new BaseCallBack<List<Post>>(){

            @Override
            protected void OnRequestBefore(Request request) {
                Log.i(TAG, "OnRequestBefore:成功连接到服务器");
            }

            @Override
            protected void onFailure(Call call) {
                Toast.makeText(getContext(), "没有更多数据拉~~~", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(Call call, Response response, List<Post> posts) {
                postList.addAll(posts);
                adapter.setList(postList);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "onSuccess: 获得帖子数据成功！");
                integer.incrementAndGet();
                loading.setStatus(LoadingLayout.Success);
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
        },map);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==CommunityFragment.POSTDETAILS){
            if (data!=null){
                int position=data.getIntExtra("position",-1);
                int loveStatus=data.getIntExtra("loveStatus",-1);
                int loveNum=data.getIntExtra("loveNum",-1);
                Log.i(TAG, "onActivityResult: lovenum="+loveNum);
                int commentNum=data.getIntExtra("commentNum",-1);
                if (position!=-1){
                    Post post=postList.get(position);
                    if (loveStatus!=-1){
                        post.setLoveStatus(loveStatus);
                    }
                    if (loveNum!=-1){
                        post.setLoveNum(loveNum);
                    }
                    if (commentNum!=-1){
                        post.setCommentNum(commentNum);
                    }
                    adapter.notifyItemChanged(position);
                    Log.i(TAG, "onActivityResult: 改变");
                }
            }
        }else if(requestCode==ADDPOST){
            if (data!=null){
                String content =data.getStringExtra("content");
                String imgurl=data.getStringExtra("imgurl");
                Integer postId=data.getIntExtra("postId",0);
                Post post=Post.createNowPost(getContext(),content,imgurl,postId);
                adapter.addPost(0,post);
                Log.i(TAG, "onActivityResult:添加成功");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
