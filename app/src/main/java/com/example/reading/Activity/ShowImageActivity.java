/*
package com.example.reading.Activity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.autofill.UserData;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.reading.Bean.Post;
import com.example.reading.R;
import com.example.reading.adapter.ShowImageAdapter;
import com.example.reading.util.ImageUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowImageActivity extends AppCompatActivity {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    private static final String TAG = "ShowImageActivity";
    private ViewPager viewPager;
    private TextView picture_text;
    private TextView picture_num;
    private LinearLayout lin;
    private List<View>  listViews =new ArrayList<>();
    private ImageView back,button_images;
    private ShowImageAdapter imageAdapter;
    private List<Post> mList=new ArrayList<>();
    private ArrayList<String> urls =null;
    private ArrayList<Boolean> booleans;
    private int position,total;
    private LinearLayout bottom,bottom_text;
    private LinearLayout Open_and_Retract;
    private LinearLayout loveLayout,collectionLayout;
    private ImageView loveImageView,collectionImageView;
    private TextView Picture_text,button_text,loveStr,talkStr,collectionStr,loadTextView;
    private ScrollView Picture_text_main;
    private Boolean Picture_key;
    private RelativeLayout.LayoutParams linearParams;
    private LayoutTransition transition;
    //子线程不能操作UI，通过Handler设置图片
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
//                case GET_DATA_SUCCESS:
//                    Bitmap bitmap= (Bitmap) msg.obj;
//                    break;
                case NETWORK_ERROR:
                    Toast.makeText(ShowImageActivity.this,"网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(ShowImageActivity.this,"服务器发生错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public void setList(List<Post> list) {
        mList.addAll(list);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_image_layout_text);
        initView();
        initData();
        back = findViewById(R.id.title_back);
        lin = findViewById(R.id.lin_go);
        viewp = findViewById(R.id.show_view_pager);
        //设置加载动画
        //
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onBackPressed();
            }
        });
        mMemoryCacheUtils = MemoryCacheUtils.getInstance();
        mLocalCacheUtils = LocalCacheUtils.getInstance();
    }
    private void initView(){
        Picture_key=false;
        //容器内的子view的layout发生变化时也播放动画
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        transition = new LayoutTransition();
        container.setLayoutTransition(transition);
        transition = container.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        Picture_text_main = findViewById(R.id.Picture_text_main);
        button_images = findViewById(R.id.button_images);
        button_text = findViewById(R.id.button_text);
        bottom = findViewById(R.id.bottom);
        Picture_text = findViewById(R.id.Picture_text);
        Open_and_Retract = findViewById(R.id.Open_and_Retract);
        viewPager =findViewById(R.id.show_view_pager);
        //将设置好的动画指定给它
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        picture_num=findViewById(R.id.picture_num);
        //先写死把
        loveLayout=findViewById(R.id.loveLayout);
        loadTextView=findViewById(R.id.loadTextView);
        loveImageView=findViewById(R.id.loveNum);
        loveStr=findViewById(R.id.loveNumStr);
        collectionLayout=findViewById(R.id.collectionLayout);
        collectionImageView=findViewById(R.id.collection);
        collectionStr=findViewById(R.id.collectionNum);
        talkStr=findViewById(R.id.talkNum);
        Picture_text_main.getBackground().mutate().setAlpha(100);
        Picture_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Picture_key==false){
                    button_text.setText("展开");
                    button_images.setImageResource(R.drawable.open_gray);
                    linearParams =(RelativeLayout.LayoutParams) Picture_text_main.getLayoutParams();
                    linearParams.height=0;
                    Picture_text_main.setLayoutParams(linearParams);
                    Picture_key=true;
                }
            }
        });
        Open_and_Retract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Picture_key==true){
                    button_text.setText("收起");
                    button_images.setImageResource(R.drawable.retract_gray);
                    linearParams =(RelativeLayout.LayoutParams) Picture_text_main.getLayoutParams();
                    linearParams.height=420;
                    Picture_text_main.setLayoutParams(linearParams);
                    Picture_key=false;
                }else{
                    button_text.setText("展开");
                    button_images.setImageResource(R.drawable.open_gray);
                    linearParams =(RelativeLayout.LayoutParams) Picture_text_main.getLayoutParams();
                    linearParams.height=0;
                    Picture_text_main.setLayoutParams(linearParams);
                    Picture_key=true;
                }

            }
        });
    }
    private void initData(){
        Bundle bundle=getIntent().getExtras();
        total=bundle.getInt("total",0);
        position=bundle.getInt("id",0);
        picture_num.setText(position+1+"/"+total);
    }
    private void inint() {
        if (urls != null && urls.size() > 0){
            for (int i = 0; i < urls.size(); i++) {  //for循环将试图添加到list中
                View view = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.view_pager_item, null);   //绑定viewpager的item布局文件
//                ImageView iv = (ImageView) view.findViewById(R.id.view_image);   //绑定布局中的id
//                iv.setImageBitmap(urls.get(i));   //设置当前点击的图片
                listViews.add(view);
                String path=urls.get(i);
                switch (ImageUtils.handlerImagePath(path)){
                    case ImageUtils.PNG:
                    case ImageUtils.JPEG:
                        setImageURL(path,i);
                        break;
                    case ImageUtils.GIF:
                        setGifUrl(path,i);
                        break;
                }
            }
            imageAdapter = new ShowImageAdapter(listViews);
            viewPager.setAdapter(imageAdapter);
            viewPager.setOnPageChangeListener(new PageChangeListener()); //设置viewpager的改变监听
            //截取intent获取传递的值
            viewPager.setCurrentItem(position);    //viewpager显示指定的位置
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventMainThread(Object o) {
        urls = (ArrayList<String>)o;
        booleans=new ArrayList<>(urls.size());
        inint();   //初始化
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.i(TAG, "onPageScrollStateChanged: 滚动啊");
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.i(TAG, "onPageSelected: 冲啊");
            picture_num.setText(position+1+"/"+total);
//            boolean b = booleans.get(position);
//            if(!b){
//                ProgressBar progressBar=new ProgressBar();
//            }
            index = position;
        }

    }
    //设置网络图片（从网络中获取图片）
    public void setImageURL(final String path, final int index) {
        Log.i(TAG, "setImageURL: path="+path);
        View view=listViews.get(index);
        final SubsamplingScaleImageView imageView =view.findViewById(R.id.view_image);//绑定布局中的id/
        final LinearLayout loadLayout = view.findViewById(R.id.loadLayout);
        final Button loadButton = view.findViewById(R.id.loadButton);
        final TextView loadTextView=view.findViewById(R.id.loadTextView);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTextView.setText("努力加载图片中...");
                loadButton.setVisibility(View.GONE);
                setImageURL(path,index);
            }
        });
        Glide.with(ShowImageActivity.this).load(path).into(new CustomViewTarget<SubsamplingScaleImageView,Drawable>(imageView) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                loadButton.setVisibility(View.VISIBLE);
                loadTextView.setText("加载图片失败请重试");
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Log.i(TAG, "onResourceReady: 加载成功！");
                loadLayout.setVisibility(View.GONE);
                imageView.setImage(ImageSource.bitmap(ImageUtils.drawableToBitmap(resource)));
            }
        });
    }
    private void setGifUrl(final String path, final int index){
        Log.i(TAG, "setGifURL: path="+path);
        View view=listViews.get(index);
        final ImageView imageView =view.findViewById(R.id.gifView);//绑定布局中的id/
        final LinearLayout loadLayout = view.findViewById(R.id.loadLayout);
        final Button loadButton = view.findViewById(R.id.loadButton);
        final TextView loadTextView=view.findViewById(R.id.loadTextView);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTextView.setText("努力加载图片中...");
                loadButton.setVisibility(View.GONE);
                setImageURL(path,index);
            }
        });
        Glide.with(ShowImageActivity.this).asGif().load(path).addListener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                loadButton.setVisibility(View.VISIBLE);
                loadTextView.setText("加载图片失败请重试");
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                loadLayout.setVisibility(View.GONE);
                return  false;
            }
        }).into(imageView);
    }
*/
/*    private void initStatus(){
        Log.i(TAG, "initStatus: loveNum="+info.getContent());
        loveStr.setText(info.getLoveNum());
        talkStr.setText(info.getTalkNum());
        loveStatus=info.getLoveStatus();
        collectionStatus=info.getCollectionStatus();
        content=info.getContent();
        if(loveStatus==1){
            loveImageView.setImageDrawable(getResources().getDrawable(R.drawable.thumbs_up_complete));
        }else{
            loveImageView.setImageDrawable(getResources().getDrawable(R.drawable.thumbs_up_white));
        }
        loveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loveStatus==1){
                    loveImageView.setImageDrawable(getResources().getDrawable(R.drawable.thumbs_up_white));
                    loveStatus=0;
                    //加加
                    loveStr.setText(String.valueOf(Integer.valueOf(loveStr.getText().toString())-1));

                }else{
                    loveImageView.setImageDrawable(getResources().getDrawable(R.drawable.thumbs_up_complete));
                    loveStatus=1;
                    //减减
                    loveStr.setText(String.valueOf(Integer.valueOf(loveStr.getText().toString())+1));
                }
                netWorkUtil.updatePostLove(info.getPostId(),userData.getToken());
            }
        });
        if(collectionStatus==1){
            collectionImageView.setImageDrawable(getResources().getDrawable(R.drawable.shocangwanc));
            collectionStr.setText("已收藏");
        }else {
            collectionImageView.setImageDrawable(getResources().getDrawable(R.drawable.shocang_text));
            collectionStr.setText("未收藏");
        }
        collectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerCollection();
                if (collectionStatus==1){
                    collectionImageView.setImageDrawable(getResources().getDrawable(R.drawable.shocang_text));
                    collectionStatus=0;
                    collectionStr.setText("未收藏");

                }else{
                    collectionImageView.setImageDrawable(getResources().getDrawable(R.drawable.shocangwanc));
                    collectionStatus=1;
                    collectionStr.setText("已收藏");
                }
            }
        });
    }*//*

*/
/*    private void handlerCollection(){
        RequestBody requestBody=new FormBody.Builder()
                .add("postId", String.valueOf(info.getPostId()))
                .add("token",userData.getToken())
                .build();
        Request request=new Request.Builder()
                .post(requestBody)
                .url(getRequestUrl())
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
    }*//*

*/
/*    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: 放回了啊");
        Intent intent=new Intent();
        intent.putExtra("loveNum",loveStr.getText().toString());
        intent.putExtra("talkNum",talkStr.getText().toString());
        intent.putExtra("status",loveStatus);
        intent.putExtra("collectionStatus",collectionStatus);
        setResult(HomeFragment.SHOWIMAGEACTIVITY,intent);
        finish();
    }*//*

}
*/
