package com.example.reading.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.PostDetails;
import com.example.reading.Bean.User;
import com.example.reading.Bean.UserPostComment;
import com.example.reading.R;
import com.example.reading.util.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class Record_PraiseAdapter extends RecyclerView.Adapter<Record_PraiseAdapter.ViewHolder>{
    private View mView;
    private List<UserPostComment> userComments=new ArrayList<>();
    private Context context;
    private static final String TAG = "Record_PraiseAdapter";
    public Record_PraiseAdapter(List<UserPostComment> userComments, Context context) {
        this.userComments = userComments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView= LayoutInflater.from(context).inflate(R.layout.record_item,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPostComment comment=userComments.get(position);
        Spanned spanned = Html.fromHtml(comment.getContent());
        holder.comment.setText(spanned+"...");
        holder.comment_big.setOnClickListener(new View.OnClickListener() {
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
        return userComments.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView comment;
        LinearLayout comment_big;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            comment_big = itemView.findViewById(R.id.comment_big);
        }
    }
}
