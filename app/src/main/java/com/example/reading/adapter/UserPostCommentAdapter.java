package com.example.reading.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.PostDetails;
import com.example.reading.Bean.User;
import com.example.reading.Bean.UserPostComment;
import com.example.reading.R;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.RoundImageView;
import com.example.reading.util.UserUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.example.reading.MainApplication.getContext;

public class UserPostCommentAdapter extends RecyclerView.Adapter<UserPostCommentAdapter.ViewHolder> {
    private View mView;
    private List<UserPostComment> userPostComments=new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    private User userData;
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.loading)          // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.image_error)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.image_error)       // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
            .build();

    public UserPostCommentAdapter(List<UserPostComment> userPostComments, Context context) {
        this.userPostComments = userPostComments;
        this.context = context;
        imageLoader= ImageLoader.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView= LayoutInflater.from(context).inflate(R.layout.post_comment_item,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPostComment comment=userPostComments.get(position);
        userData= FileCacheUtil.getUser(getContext());
        //设置图片
        imageLoader.displayImage(userData.getUimg(),holder.uimg,options);
        //设置文字
        holder.username.setText(userData.getUsername());
        holder.time.setText(DateTimeUtil.handlerDateTime(comment.getCcreateTime().getTime()));
        holder.content.setText(comment.getContent());
        holder.postContent.setText("文章标题："+comment.getPostContent());
        holder.postContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PostDetails.class);
                intent.putExtra("postId",comment.getPid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userPostComments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RoundImageView uimg;
        private TextView username,time,content,postContent,commentNum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uimg=itemView.findViewById(R.id.userimg);
            username=itemView.findViewById(R.id.username);
            time=itemView.findViewById(R.id.time);
            content=itemView.findViewById(R.id.content);
            postContent=itemView.findViewById(R.id.postContent);
        }
    }
}
