package com.example.reading.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.Activity.AllBooks;
import com.example.reading.Activity.Party;
import com.example.reading.Activity.SearchActivity;
import com.example.reading.Activity.UserFeedBack;
import com.example.reading.Activity.XiaoYouSound;
import com.example.reading.Bean.BookDetails;
import com.example.reading.R;
import com.example.reading.Bean.BookType;
import com.example.reading.Bean.FestivalDetails;
import com.example.reading.databinding.HomefragmentBinding;
import com.example.reading.adapter.FestivalAdapter;
import com.example.reading.adapter.MAdapter;
import com.example.reading.adapter.MAdapter_seller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.jessyan.autosize.internal.CustomAdapt;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wowo.kjt.library.onPageClickListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment implements CustomAdapt {
    HomefragmentBinding binding;
    private int phoneHeight = -1;
    String date1;
    String SolarTermsData;
    int code;
    int scode;
    private MAdapter mAdapter;
    private MAdapter_seller mAdapter_seller;
    private FestivalAdapter festivalAdapter;
    List<BookDetails> bookDetails;
    List<BookDetails> bookDetails2;
    List<FestivalDetails> festivalDetails;
    List<String> urlList;
    private String time;
    private String Title;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 200:
                    System.out.println("----");
                    mAdapter.notifyDataSetChanged();
                    mAdapter_seller.notifyDataSetChanged();
                    break;
                case 210:
                    binding.SolarTerms.setText(Title);
                    festivalAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.homefragment, container, false);
        initView();
        initData();
        analysis();
        setPullRefresher();
        return binding.getRoot();
    }

    private void initView() {
        urlList = new ArrayList<>();
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542627042541&di=3ad9deeefff266e76d1f5d57a58f63d1&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fdesign%2F00%2F69%2F99%2F66%2F9fce5755f081660431464492a9aeb003.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542627042539&di=95bd41d43c335e74863d9bb540361906&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F019a0558be22d6a801219c77d0578a.jpg%402o.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542627042539&di=cdd54bffd2aac448c70ae6b416a004d4&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01edb3555ea8100000009af0ba36f5.jpg%401280w_1l_2o_100sh.jpg");
        //向下刷新
        binding.scroll.setVerticalScrollBarEnabled(false);
        //设置 Header式
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        //取消Footer
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setDisableContentWhenRefresh(true);
        //MAdapter
        mAdapter=new MAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        binding.RecommendRecycleview.setLayoutManager(layoutManager);
        binding.RecommendRecycleview.setAdapter(mAdapter);
        //MAdapter_seller
        mAdapter_seller = new MAdapter_seller(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        binding.BestSellerRecycleview.setLayoutManager(layoutManager2);
        binding.BestSellerRecycleview.setAdapter(mAdapter_seller);
        //FestivalAdapter
        festivalAdapter = new FestivalAdapter(getActivity());
        LinearLayoutManager im3 = new LinearLayoutManager(getContext());
        im3.setOrientation(LinearLayoutManager.VERTICAL);
        binding.festivalRecycleview.setLayoutManager(im3);
        binding.festivalRecycleview.setAdapter(festivalAdapter);
        binding.searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 开始跳转");
                Intent intent=new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        binding.userfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UserFeedBack.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        binding.galleryBanner
                .setDuration(2000)
                .setOnPageClickListener(new onPageClickListener() {
                    @Override
                    public void onPageClick(int i) {
                        Toast.makeText(getActivity(),"你点击了"+i, Toast.LENGTH_SHORT).show();
                    }
                })
                .useAngleGalleryStyle()
                .setDataFromUrl((ArrayList<String>) urlList)
                .startLoop();
        binding.BestSsellerAllBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllBooks.class);
                startActivity(intent);
            }
        });
        binding.AllBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllBooks.class);
                startActivity(intent);
            }
        });
        binding.xiaoyou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),XiaoYouSound.class);
                startActivity(intent);
            }
        });
        binding.party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Party.class);
                startActivity(intent);
            }
        });
    }


    private void analysis() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                time = String.valueOf(Calendar.getInstance().getTimeInMillis());
                Log.d(TAG, "从1970至现在的毫秒数:"+time);
                /**
                 * 每月推送
                 */
                Request request = new Request.Builder()
                        .url("http://117.48.205.198/xiaoyoudushu/findBooksByDate?time="+time)
                        .build();
                Log.d(TAG, "url为"+"http://117.48.205.198/xiaoyoudushu/findBooksByDate?time="+time);
                /**
                 * 节气推送
                 */
                Request RequestSolarTerms = new Request.Builder()
                        .url("http://117.48.205.198/xiaoyoudushu/findFestivalBooks")
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    Response responseSolarTerms = okHttpClient.newCall(RequestSolarTerms).execute();
                    date1 = response.body().string();
                    SolarTermsData = responseSolarTerms.body().string();
                    Log.d(TAG, " 每月推荐数据："+date1);
                    Log.d(TAG, " 节气推荐数据："+SolarTermsData);
                    JsonJX(date1,SolarTermsData);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void JsonJX(String Data,String SolarData) {
        if(Data !=null){
            try {
                JSONObject object = new JSONObject(Data);
                String data = object.getString("data");
                JSONObject Data1 = new JSONObject(data);
                String newBooks = Data1.getString("newBooks");
                Log.d(TAG, "newBook数据为 :"+newBooks);
                String popularBooks = Data1.getString("popularBooks");
                Log.d(TAG, "popularBooks数据为 :"+popularBooks);
                code = object.getInt("code");
                if(code==1){
                    if (popularBooks!=null){
                        Gson gson = new Gson();
                        bookDetails = gson.fromJson(newBooks,new TypeToken<List<BookDetails>>() {}.getType());
                        mAdapter.setMyAdapter(bookDetails);
                        bookDetails2 = gson.fromJson(popularBooks,new TypeToken<List<BookDetails>>() {}.getType());
                        mAdapter_seller.setMAdapter_seller(bookDetails2);
                        Message message=Message.obtain();
                        message.what=200;
                        handler.sendMessage(message);
                    }else{
                        Log.d(TAG, "暂无数据！");
                        Toast.makeText(getContext(),"获取数据失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        if (SolarData !=null){
            try{
                JSONObject object = new JSONObject(SolarData);
                String data = object.getString("data");
                JSONObject array = new JSONObject(data);
                String festivaldata = array.getString("festival");
                String bookDtoList = array.getString("bookDtoList");
                JSONObject arrayfestival = new JSONObject(festivaldata);
                Title = arrayfestival.getString("name");
                Log.d(TAG, "当前节日为 "+Title);
                Log.d(TAG, "bookDtoList:"+bookDtoList);
                scode = object.getInt("code");
                if (code==1){
                    if (bookDtoList==null){
                        Log.d(TAG, "bookDtoList没数据拉");
                    }else{
                    Gson gson = new Gson();
                    festivalDetails = gson.fromJson(bookDtoList,new TypeToken<List<FestivalDetails>>() {}.getType());
                    festivalAdapter.setFestivalAdapter(festivalDetails);
                    Message message=Message.obtain();
                    message.what=210;
                    handler.sendMessage(message);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void setPullRefresher(){
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //在这里执行下拉刷新时的具体操作(网络请求、更新UI等)
//                bookDetails.clear();
//                bookDetails2.clear();
//                festivalDetails.clear();
                Toast.makeText(getContext(),"正在刷新",Toast.LENGTH_SHORT).show();
//                analysis();
                mAdapter_seller.notifyDataSetChanged();
                binding.refreshLayout.finishRefresh(2000);
            }
        });
    }

    //需要改变适配尺寸的时候，在重写这两个方法
    @Override
    public boolean isBaseOnWidth() {
        return false;
    }
    @Override
    public float getSizeInDp() {
        return 640;
    }
}