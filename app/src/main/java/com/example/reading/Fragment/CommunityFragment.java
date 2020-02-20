package com.example.reading.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.addPost;
import com.example.reading.Bean.Post;
import com.example.reading.R;
import com.example.reading.adapter.NineGridAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;

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
    public static final int SEARCHVIEW=2;
    public static final int SHOWIMAGEACTIVITY=3;
    private List<Post> postList=new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private NineGridAdapter adapter;
    private int oid;
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
        recyclerView=view.findViewById(R.id.list_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new NineGridAdapter(this);
        recyclerView.setAdapter(adapter);

        final floatingactionbutton.FloatingActionButton actionA = view.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_addPost = new Intent(getActivity(), addPost.class);
                intent_addPost.putExtra("oid",oid);
                startActivity(intent_addPost);
            }
        });
    }
    private void initData(){
        findPostByPage();

    }

    private void findPostByPage(){
        Map<String,String> map= UserUtil.createUserMap();
        map.put("currentPage", String.valueOf(integer));
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
        Log.i(TAG, "onActivityResult: 饭盒了");
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
