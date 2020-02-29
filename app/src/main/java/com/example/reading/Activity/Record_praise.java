package com.example.reading.Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.reading.Bean.UserPostComment;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.Record_PraiseAdapter;
import com.example.reading.adapter.UserPostCommentAdapter;
import com.example.reading.databinding.RecordPraiseBinding;
import com.example.reading.util.RequestStatus;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.weavey.loading.lib.LoadingLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;
import static com.example.reading.constant.RequestUrl.FIND_USER_COMMENT;
import static com.example.reading.constant.RequestUrl.FIND_USER_POST_COMMENT;

public class Record_praise extends BaseActivity {
    RecordPraiseBinding binding;
    Record_PraiseAdapter record_praiseAdapter;
    private List<UserPostComment> userComments=new ArrayList<>();
    private int currentPage = 1;
    private static final String TAG = "Record_praise";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.FAILURE:
                    binding.loading.setStatus(LoadingLayout.Success);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.record_praise);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
        initData();
    }
    private void initView(){
        binding.loading.setStatus(LoadingLayout.Loading);
        binding.refreshLayout.setEnableRefresh(false);
        //设置 Footer 为 经典样式
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        record_praiseAdapter=new Record_PraiseAdapter(userComments,Record_praise.this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(record_praiseAdapter);
        findUserComment();
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++currentPage;
                findUserComment();
                record_praiseAdapter.notifyDataSetChanged();
                refreshLayout.finishLoadMore(2000);//加载完成
                Log.d(TAG, "onLoadMore: 添加更多完成");
            }
        });
    }
    private void initData(){
        binding.setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void findUserComment(){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("currentPage", String.valueOf(currentPage));
        StandardRequestMangaer.getInstance().get(FIND_USER_COMMENT, new BaseCallBack<List<UserPostComment>>() {

            @Override
            protected void OnRequestBefore(Request request) {

            }

            @Override
            protected void onFailure(Call call) {

            }

            @Override
            protected void onSuccess(Call call, Response response, List<UserPostComment> list) {
                Toast.makeText(getContext(), "获得点赞记录成功！", Toast.LENGTH_SHORT).show();
                binding.loading.setStatus(LoadingLayout.Success);
                userComments.addAll(list);
                record_praiseAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onResponse(Response response) {
                Message mes=new Message();
                mes.what= RequestStatus.FAILURE;
                handler.sendMessage(mes);
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
