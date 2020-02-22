package com.example.reading.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.Bean.LoadFileVo;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;

import java.util.List;

public class LoadPicAdapter extends RecyclerView.Adapter<LoadPicAdapter.MyViewHolder> {
    private static final String TAG = "LoadPicAdapter";
    Context context;
    private List<String> imagePath;
    View view;
    int index=0;
    public LoadPicAdapter(Context context, List<String> imagePath) {
        this.context = context;
        this.imagePath = imagePath;
    }

    public interface OnItemClickListener {
        void click(View view, int positon);
        void del(View view);
    }

    OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.load_item_pic, parent, false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //设置删除事件
        holder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePath.remove(position);
                if (imagePath.size()==8&&!imagePath.contains(null)){
                    imagePath.add(null);
                }
                listener.del(v);
                notifyItemRemoved(position);
                Log.i(TAG, "onClick: 当前总数为"+getItemCount());
                notifyItemRangeChanged(position,getItemCount());
                Log.i(TAG, "onClick:删除下标为"+position+"的元素");
            }
        });
        String path=imagePath.get(position);
        Log.i(TAG, "onBindViewHolder: postion="+position+"图片为"+path);
        if (TextUtils.isEmpty(path)){
            holder.ivPic.setImageResource(R.drawable.jiahao);
            holder.ivDel.setVisibility(View.GONE);
            holder.ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.click(v,position);
                }
            });
        }else {
            holder.ivDel.setVisibility(View.VISIBLE);
            Glide.with(context).load(path).into(holder.ivPic);
        }
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "MyViewHolder";
        public MyImageView ivPic;
        public MyImageView ivDel;
        View view;
        MyViewHolder(View view) {
            super(view);
            this.view = view;
            Log.i(TAG, "MyViewHolder: 被创建拉");
            ivPic = view.findViewById(R.id.ivPic);
            ivDel = view.findViewById(R.id.ivDel);
        }
    }
}
