package com.example.reading.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Bean.SearchResult;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.HistorySearchAdapter;
import com.example.reading.adapter.SearchResultAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.HistorySearchUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.internal.CustomAdapt;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;


public class SearchActivity extends BaseActivity implements CustomAdapt {
    private static final String TAG = "SearchActivity";
    private SearchView searchView;
    private LinearLayout historyLayout;
    private RecyclerView recyclerHisotryView;
    private LinearLayout resultLayout;
    private RecyclerView recyclerResultView;
    private List<String> historyList=new ArrayList<>();
    private SearchResultAdapter searchResultAdapter;
    private HistorySearchAdapter adapter;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        initView();
        initHistoryRecycler();
        getHistoryList();//得到历史记录数组
        setSearchTvListener();//设置搜索按钮监听器
/*        setHistoryEmptyTvListener();//设置清空记录按钮监听器*/

    }
    private void initView(){
        searchView=findViewById(R.id.searchView);
        searchView.findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
        searchView.findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        recyclerHisotryView=findViewById(R.id.recyclerHistoryView);
        historyLayout=findViewById(R.id.historyLayout);
        resultLayout=findViewById(R.id.resultLayout);
        recyclerResultView=findViewById(R.id.recyclerResultView);
        searchView.setQueryHint("请输入搜索内容");
        searchView.onActionViewExpanded();
        back = findViewById(R.id.search_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchResultAdapter=new SearchResultAdapter(this);
        recyclerResultView.setLayoutManager(new LinearLayoutManager(this));
        recyclerResultView.setAdapter(searchResultAdapter);
    }

    private void setSearchTvListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchWord(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length()>=2){
                    Log.i(TAG, "onQueryTextChange: 开始搜索");
                    searchWord(newText);
                }else if(newText.length()<=0){
                    historyLayout.setVisibility(View.VISIBLE);
                    resultLayout.setVisibility(View.GONE);
                    getHistoryList();
                }
                return true;
            }
        });

    }
    private void showViews() {
        if (historyList.size() > 0) {
            historyLayout.setVisibility(View.VISIBLE);
        } else {
            historyLayout.setVisibility(View.GONE);
        }
    }
    private void initHistoryRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerHisotryView.setLayoutManager(layoutManager);
        recyclerHisotryView.setNestedScrollingEnabled(false);//解决滑动冲突
        adapter=new HistorySearchAdapter(this,historyList);
        recyclerHisotryView.setAdapter(adapter);
        adapter.setOnItemClickListener(new HistorySearchAdapter.OnItemClickListener() {
            @Override
            public void onItemNameTvClick(View v, String name) {
                HistorySearchUtil.getInstance(SearchActivity.this).putNewSearch(name);//保存记录到数据库
                searchWord(name);
            }
            @Override
            public void onItemDeleteImgClick(View v, String name) {
                Log.i(TAG, "onItemDeleteImgClick: "+name);
                historyList.remove(name);
                HistorySearchUtil.getInstance(SearchActivity.this).deleteHistorySearch(name);
                adapter.notifyDataSetChanged();
                showViews();
            }
    });
}
    private void getHistoryList(){
        historyList.clear();
        historyList.addAll(HistorySearchUtil.getInstance(this).queryHistorySearchList());
        System.out.println("历史长度为"+historyList.size());
        adapter.notifyDataSetChanged();
        showViews();
    }

    private void searchWord(String queryWord){
        Map<String,String> params=new HashMap<>();
        params.put("queryWord",queryWord);
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.SEARCH, new BaseCallBack<List<SearchResult>>(){
                    @Override
                    protected void OnRequestBefore(Request request) {

                    }

                    @Override
                    protected void onFailure(Call call) {
                        Toast.makeText(SearchActivity.this, "没有结果", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onSuccess(Call call, Response response, List<SearchResult> searchResults) {
                        Log.d(TAG, "onSuccess: -----------------------"+searchResults);
                        searchResultAdapter.setSearchResultList(searchResults);
                        searchResultAdapter.notifyDataSetChanged();
                        Toast.makeText(SearchActivity.this, "搜索成功", Toast.LENGTH_SHORT).show();
                        HistorySearchUtil.getInstance(SearchActivity.this).putNewSearch(queryWord);
                        resultLayout.setVisibility(View.VISIBLE);
                        historyLayout.setVisibility(View.GONE);
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
    public boolean isBaseOnWidth() {
        return false;
    }
    @Override
    public float getSizeInDp() {
        return 640;
    }
}
