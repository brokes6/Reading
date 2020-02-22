package com.example.reading.Activity;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.reading.Bean.BookDetailsBean;
import com.example.reading.Bean.HistoryBean;
import com.example.reading.Bean.User;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.adapter.FestivalAdapter;
import com.example.reading.adapter.HistoryAdapter;
import com.example.reading.databinding.HistoryBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.PostHitoryUtil;
import com.example.reading.util.RequestStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;

public class History extends BaseActivity {
    private static final String TAG = "History";
    HistoryBinding binding;
    List<HistoryBean> historyBeanList = new ArrayList<>();
    BookDetailsBean bookDetailsBean = new BookDetailsBean();
    String url = "http://117.48.205.198/xiaoyoudushu/findBookById?";
    String Aurl;
    String token;
    HistoryAdapter historyAdapter;
    private User userData;
    int code,Page = 0;
    String bookid;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    historyAdapter.notifyDataSetChanged();
                    break;
                case RequestStatus.FAILURE:
                    Toast.makeText(History.this,"服务器未响应，请稍后在尝试！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.history);
        userData= FileCacheUtil.getUser(getContext());
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
        findHistoryIdDetails();
    }
    private void initView(){
        historyAdapter = new HistoryAdapter(History.this);
        LinearLayoutManager im3 = new LinearLayoutManager(getContext());
        im3.setOrientation(LinearLayoutManager.VERTICAL);
        binding.historyView.setLayoutManager(im3);
        binding.historyView.setAdapter(historyAdapter);

    }
    private void initData(){

    }
    private void findHistoryIdDetails(){
        bookid= PostHitoryUtil.getSearchHistory(History.this);
        List<String> list= Arrays.asList(bookid.split(","));
        for (int i=0;i<list.size();i++) {
            Log.d(TAG, "返回的书籍历史id为--" + list.get(i));
            int index = i;
            token = userData.getToken();
            if (bookid.trim().equals("")) {
                Toast.makeText(this, "你还没有浏览历史喔", Toast.LENGTH_SHORT).show();
                Message message = new Message();
                message.what = RequestStatus.NO_RESOURCE;
                handler.sendMessage(message);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Aurl =url+"token="+token+"&"+"bid="+list.get(index);
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
                            String BookData = response.body().string();
                            try {
                                JSONObject object = new JSONObject(BookData);
                                code = object.getInt("code");
                                if (code == 1) {
                                    String data = object.getString("data");
                                    Gson gson = new Gson();
                                    bookDetailsBean = gson.fromJson(data, new TypeToken<BookDetailsBean>() {}.getType());
                                    HistoryBean historyBean = new HistoryBean();
                                    historyBean.setBid(bookDetailsBean.getBid());
                                    historyBean.setBname(bookDetailsBean.getBname());
                                    historyBean.setBimg(bookDetailsBean.getBimg());
                                    historyBean.setAuthor(bookDetailsBean.getAuthor());
                                    historyBeanList.add(historyBean);
                                    historyAdapter.setHistory(historyBeanList);
                                    Message message = new Message();
                                    message.what = RequestStatus.SUCCESS;
                                    handler.sendMessage(message);
                                }else{
                                    Message message = new Message();
                                    message.what = RequestStatus.FAILURE;
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        }
    }
}
