package com.example.reading.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.reading.Activity.PostDetails;
import com.example.reading.Activity.ShowImageActivity;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.adapter.NineGridAdapter;
import com.example.reading.util.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：图片布局
 * 作者：czj
 * 时间：2020-02-15
 */
public class StandardNineGridLayout extends NineGridLayout {
    private static final String TAG = "NineGridTestLayout";
    protected static final int MAX_W_H_RATIO = 3;
    private List<String> detailsImgUrls;
    private ShowImageInfo info;
    private NineGridAdapter.ViewHolder viewHolder;
    private OnClickListener listener;
    public StandardNineGridLayout(Context context) {
        super(context);
    }

    public StandardNineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final ImageView imageView, String url, final int parentWidth) {

        ImageLoaderUtil.displayImage(mContext, imageView, url, ImageLoaderUtil.getPhotoImageOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                Log.i(TAG, "onLoadingComplete: imageUri="+imageUri);
                Log.i(TAG, "onLoadingComplete: bitmap="+bitmap);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int newW;
                int newH;
                if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
                    newW = parentWidth / 2;
                    newH = newW * 5 / 3;
                } else if (h < w) {//h:w = 2:3
                    newW = parentWidth * 2 / 3;
                    newH = newW * 2 / 3;
                } else {//newH:h = newW :w
                    newW = parentWidth / 2;
                    newH = h * newW / w;
                }
                setOneImageLayoutParams(imageView, newW, newH);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return false;
    }

    @Override
    protected void displayImage(ImageView imageView, String url) {
        Glide.with(mContext).load(url).into(imageView);
    }

    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {
        if(detailsImgUrls==null){
            detailsImgUrls=new ArrayList<>();
            for(String str:urlList){
                int index=str.lastIndexOf(".");
                str=str.substring(0,index-5)+str.substring(index);
                Log.i(TAG, "onClickImage: str="+str);
                detailsImgUrls.add(str);
            }
        }
        Log.i(TAG, "onClickImage: info"+info);
        EventBus.getDefault().postSticky(urlList);
        //帖子详情-----
        Intent intent = new Intent(getContext(), ShowImageActivity.class);
        intent.putExtra("id",i);   //将当前点击的位置传递过去
        intent.putExtra("info",info);
        intent.putExtra("total",urlList.size());
        //-------
        ((AppCompatActivity)getContext()).startActivityForResult(intent, CommunityFragment.SHOWIMAGEACTIVITY); //启动Activity

    }

    public void setInfo(String content, String loveNum, String talkNum, int loveStatus, int collectionStatus, int postId, NineGridAdapter.ViewHolder viewHolder) {
        info=new ShowImageInfo(content,loveNum,talkNum,loveStatus,collectionStatus,postId);
        this.viewHolder=viewHolder;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null) {
            listener.onClick(v);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        this.listener=l;
    }

    public static class ShowImageInfo implements Serializable{
        private String content,loveNum,talkNum;
        private int loveStatus,collectionStatus,postId;

        private ShowImageInfo(String content, String loveNum, String talkNum, int loveStatus, int collectionStatus,int postId) {
            this.content = content;
            this.loveNum = loveNum;
            this.talkNum = talkNum;
            this.loveStatus = loveStatus;
            this.collectionStatus = collectionStatus;
            this.postId=postId;
        }

        public String getContent() {
            return content;
        }

        public String getLoveNum() {
            return loveNum;
        }

        public String getTalkNum() {
            return talkNum;
        }

        public int getLoveStatus() {
            return loveStatus;
        }

        public int getCollectionStatus() {
            return collectionStatus;
        }

        public int getPostId() {
            return postId;
        }
    }
}
