package com.example.reading.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reading.Bean.BookDetails;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.ALLBookAdapter;
import com.example.reading.adapter.ProgramAdapter;
import com.example.reading.databinding.AllBooksBinding;
import com.example.reading.util.RequestStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;

public class AllBooks extends BaseActivity {
    private static final String TAG = "AllBooks";
    AllBooksBinding binding;
    String url = "http://117.48.205.198/xiaoyoudushu/findAllBooks?currentPage=" , Aurl;
    int Page = 1;
    ALLBookAdapter allBookAdapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    allBookAdapter.notifyDataSetChanged();
                    break;
                case RequestStatus.FAILURE:
                    Toast.makeText(AllBooks.this,"获取书籍失败，请稍后尝试",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.all_books);
        binding = DataBindingUtil.setContentView(this,R.layout.all_books);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        initView();
        initData();
        analysis(Page);
    }
    public void initView(){
        binding.refreshLayout.setEnableRefresh(false);
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        binding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++Page;
                analysis(Page);
                allBookAdapter.notifyDataSetChanged();
                binding.refreshLayout.finishLoadMore(true);//加载完成
                Log.d(TAG, "onLoadMore: 添加更多完成");
            }
        });

        allBookAdapter = new ALLBookAdapter(AllBooks.this);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(lm);
        binding.recyclerView.setAdapter(allBookAdapter);
    }
    public void initData(){

    }
    private void analysis(int Page){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Aurl =url+Page;
                Request request = new Request.Builder()
                        .url(Aurl)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = new Message();
                        message.what = RequestStatus.FAILURE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String BookDate = response.body().string();
                        try {
                            JSONObject object = new JSONObject(BookDate);
                            int code = object.getInt("code");
                            if (code==1){
                                String data = object.getString("data");
                                Gson gson = new Gson();
                                List<BookDetails> bookDetails = gson.fromJson(data,new TypeToken<List<BookDetails>>(){}.getType());
                                allBookAdapter.setALLBook(bookDetails);
                                Message message = new Message();
                                message.what = RequestStatus.SUCCESS;
                                handler.sendMessage(message);
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }


    public void back(View v){
        finish();
    }
}
