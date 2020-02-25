package com.example.reading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.Bean.User;
import com.example.reading.Bean.UserBookComment;
import com.example.reading.R;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.ImageLoaderUtil;
import com.example.reading.util.RoundImageView;
import com.example.reading.util.UserUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class UserBookCommentAdapter extends RecyclerView.Adapter<UserBookCommentAdapter.ViewHolder> {
    private View mView;
    private List<UserBookComment> userBookComments=new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.loading)          // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.image_error)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.image_error)       // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
            .build();

    public UserBookCommentAdapter(List<UserBookComment> userBookComments, Context context) {
        this.userBookComments = userBookComments;
        this.context = context;
        imageLoader= ImageLoader.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView= LayoutInflater.from(context).inflate(R.layout.book_comment_item,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserBookComment comment=userBookComments.get(position);
        User user= UserUtil.getUserInfo();
        //设置图片
        imageLoader.displayImage(user.getUimg(),holder.uimg,options);
        imageLoader.displayImage(comment.getBimg(),holder.bookImg,options);
        //设置文字
        holder.username.setText(user.getUsername());
        holder.time.setText(DateTimeUtil.handlerDateTime(comment.getCcreateTime().getTime()));
        holder.content.setText(comment.getContent());
        holder.bookName.setText(comment.getBname());
        holder.description.setText(comment.getDescription());
        holder.commentNum.setText(String.valueOf(comment.getCommentNum()));
    }

    @Override
    public int getItemCount() {
        return userBookComments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RoundImageView uimg,bookImg;
        private TextView username,time,content,bookName,description,commentNum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uimg=itemView.findViewById(R.id.userimg);
            bookImg=itemView.findViewById(R.id.bookImg);
            username=itemView.findViewById(R.id.username);
            time=itemView.findViewById(R.id.time);
            content=itemView.findViewById(R.id.content);
            bookName=itemView.findViewById(R.id.bookName);
            description=itemView.findViewById(R.id.descrption);
            commentNum=itemView.findViewById(R.id.commentNum);
        }
    }
}
