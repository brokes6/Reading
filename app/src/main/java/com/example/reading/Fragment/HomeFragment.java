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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.Activity.AllBooks;
import com.example.reading.Activity.History;
import com.example.reading.Activity.Party;
import com.example.reading.Activity.SearchActivity;
import com.example.reading.Activity.UserFeedBack;
import com.example.reading.Activity.XiaoYouSound;
import com.example.reading.Activity.addPost;
import com.example.reading.Bean.BookDetails;
import com.example.reading.Bean.RotationBean;
import com.example.reading.R;
import com.example.reading.Bean.BookType;
import com.example.reading.Bean.FestivalDetails;
import com.example.reading.constant.RequestUrl;
import com.example.reading.databinding.HomefragmentBinding;
import com.example.reading.adapter.FestivalAdapter;
import com.example.reading.adapter.MAdapter;
import com.example.reading.adapter.MAdapter_seller;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.internal.CustomAdapt;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wowo.kjt.library.onPageClickListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment implements CustomAdapt,View.OnClickListener {
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
                    binding.loading.setStatus(LoadingLayout.Success);
                    System.out.println("----");
                    mAdapter.notifyDataSetChanged();
                    mAdapter_seller.notifyDataSetChanged();
                    break;
                case 210:
                    binding.loading.setStatus(LoadingLayout.Success);
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
        binding.loading.setStatus(LoadingLayout.Loading);
        initView();
        initData();
        analysis();
        setPullRefresher();
        return binding.getRoot();
    }

    private void initView() {
        urlList = new ArrayList<>();
        //向下刷新
        binding.scroll.setVerticalScrollBarEnabled(false);
        //设置 Header式
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        //取消Footer
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setDisableContentWhenRefresh(true);
        //MAdapter
        mAdapter=new MAdapter(getActivity());
//        LinearLayoutManager im = new LinearLayoutManager(getContext());
//        im.setOrientation(LinearLayoutManager.HORIZONTAL);
        GridLayoutManager gridLayoutManage = new GridLayoutManager(getContext(),3);
        binding.RecommendRecycleview.setLayoutManager(gridLayoutManage);
        binding.RecommendRecycleview.setAdapter(mAdapter);
        //MAdapter_seller
        mAdapter_seller = new MAdapter_seller(getActivity());
//        LinearLayoutManager im2 = new LinearLayoutManager(getContext());
//        im2.setOrientation(LinearLayoutManager.HORIZONTAL);
        GridLayoutManager gridLayoutManage2 = new GridLayoutManager(getContext(),3);
        binding.BestSellerRecycleview.setLayoutManager(gridLayoutManage2);
        binding.BestSellerRecycleview.setAdapter(mAdapter_seller);
        //FestivalAdapter
        festivalAdapter = new FestivalAdapter(getActivity());
        LinearLayoutManager im3 = new LinearLayoutManager(getContext());
        im3.setOrientation(LinearLayoutManager.VERTICAL);
        binding.festivalRecycleview.setLayoutManager(im3);
        binding.festivalRecycleview.setAdapter(festivalAdapter);
    }

    private void initData() {
        initRotationChart();
        binding.searchBox.setOnClickListener(this);
        binding.userfeedback.setOnClickListener(this);
        binding.BestSsellerAllBook.setOnClickListener(this);
        binding.AllBook.setOnClickListener(this);
        binding.xiaoyou.setOnClickListener(this);
        binding.party.setOnClickListener(this);
        binding.history.setOnClickListener(this);
        binding.addBox.setOnClickListener(this);
    }
    public void initBanner(){
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_box:
                Log.i(TAG, "onClick: 开始跳转");
                Intent intent=new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.userfeedback:
                Intent intent1=new Intent(getContext(), UserFeedBack.class);
                startActivity(intent1);
                break;
            case R.id.BestSseller_AllBook:
            case R.id.AllBook:
                Intent intent2 = new Intent(getContext(), AllBooks.class);
                startActivity(intent2);
                break;
            case R.id.xiaoyou:
                Intent intent4 = new Intent(getContext(),XiaoYouSound.class);
                startActivity(intent4);
                break;
            case R.id.party:
                Intent intent5 = new Intent(getContext(), Party.class);
                startActivity(intent5);
                break;
            case R.id.history:
                Intent intent6 = new Intent(getContext(), History.class);
                startActivity(intent6);
                break;
            case R.id.add_box:
                Intent intent7 = new Intent(getContext(), addPost.class);
                startActivity(intent7);
        }
    }
    private void initRotationChart(){
        Map<String,String> map= UserUtil.createUserMap();
        map.clear();
        map.put("type","100");
        StandardRequestMangaer.getInstance().get(RequestUrl.FIND_ROTATION, new BaseCallBack<List<RotationBean>>(){

            @Override
            protected void OnRequestBefore(Request request) {
                Log.d(TAG, "OnRequestBefore: 1");
            }

            @Override
            protected void onFailure(Call call) {
                Toast.makeText(getContext(),"轮播图获取失败，请稍后尝试!",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onSuccess(Call call, Response response, List<RotationBean> rotationBeans) {
                urlList.clear();
                for (int i=0;i<rotationBeans.size();i++){
                    urlList.add(rotationBeans.get(i).getUrl());
                }
                initBanner();
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
                String popularBooks = Data1.getString("popularBooks");
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
                String bookDtoList = array.getString("bookVos");
                JSONObject arrayfestival = new JSONObject(festivaldata);
                Title = arrayfestival.getString("name");
                Log.d(TAG, "当前节日为 "+Title);
                Log.d(TAG, "bookDtoList:"+bookDtoList);
                scode = object.getInt("code");
                if (code==1){
                    if (bookDtoList==null){

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
                Toast.makeText(getContext(),"正在刷新",Toast.LENGTH_SHORT).show();
                mAdapter.ClearMyAdapter();
                mAdapter_seller.ClearMAdapter_seller();
                festivalAdapter.ClearFestivalAdapter();
                analysis();
                Message message=Message.obtain();
                message.what=200;
                handler.sendMessage(message);
                Message message1=Message.obtain();
                message.what=210;
                handler.sendMessage(message1);
                binding.refreshLayout.finishRefresh(true);
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