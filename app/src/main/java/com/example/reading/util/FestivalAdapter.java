package com.example.reading.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.Activity.ReadActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.FestivalDetails;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FestivalAdapter extends RecyclerView.Adapter<FestivalViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private List<FestivalDetails> festivalDetails1=new ArrayList<>();

    public FestivalAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setFestivalAdapter(List<FestivalDetails> festivalDetails) {
        festivalDetails1.clear();
        this.festivalDetails1.addAll(festivalDetails);
        Log.i(TAG, "节气开始设置"+this.festivalDetails1.size());
    }
    @NonNull
    @Override
    public FestivalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.festival_item, parent , false);
        FestivalViewHolder festivalViewHolder = new FestivalViewHolder(view);
        return festivalViewHolder;
    }

    @Override
    public void onBindViewHolder(FestivalViewHolder holder, int position) {
        Log.d(TAG, "节气加载开始");
        final FestivalDetails festivalDetails = festivalDetails1.get(position);
        Uri uri = Uri.parse(festivalDetails.getBimg());
        holder.festival_book_name.setText(festivalDetails.getBname());
        holder.festival_introduce.setText(festivalDetails.getDescription());
        Log.d(TAG, "书籍介绍为 : "+festivalDetails.getDescription());
        Glide.with(mContext).load(uri).into(holder.festival_book_img);
        holder.festivalwhole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ReadActivity.class);
                intent.putExtra("id",festivalDetails.getBid());
                Log.d(TAG, "onClick: FestivalAdapter适配器"+"加载完成,书籍id为："+festivalDetails.getBid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return festivalDetails1.size();
    }
}

class FestivalViewHolder extends RecyclerView.ViewHolder{
    TextView festival_introduce;
    ImageView festival_book_img;
    TextView festival_book_name;
    LinearLayout festivalwhole;

    public FestivalViewHolder(View itemView) {
        super(itemView);
        festivalwhole = itemView.findViewById(R.id.festivalwhole);
        festival_book_name = itemView.findViewById(R.id.festival_book_name);
        festival_book_img = itemView.findViewById(R.id.festival_book_img);
        festival_introduce = itemView.findViewById(R.id.festival_introduce);

    }
}
