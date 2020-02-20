package com.example.reading.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.reading.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.ViewHolder> {
    private View view;
    private static final String TAG = "HistorySearchAdapter";
    private Context context;
    private List<String> historys=new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;//item点击监听接口

    public HistorySearchAdapter(Context context, List<String> historys) {
        this.context = context;
        this.historys = historys;
    }

    @NonNull
    @Override
    public HistorySearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       view= LayoutInflater.from(context).inflate(R.layout.history_item,viewGroup,false);
       ViewHolder viewHolder=new ViewHolder(view);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySearchAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.name.setText(historys.get(i));
        viewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemNameTvClick(v,historys.get(i));
                }
            }
        });
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemDeleteImgClick(v,historys.get(i));
                }
            }
        });
        Log.i(TAG, "onBindViewHolder: 开始绑定"+historys.get(i));
    }

    @Override
    public int getItemCount() {
        return historys.size();
    }


    /**
     * 设置item点击监听器
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView imageView;
        private LinearLayout contentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            imageView=itemView.findViewById(R.id.search_history_item_img);
            contentLayout=itemView.findViewById(R.id.contentLayout);
        }
    }
    /**
     * item点击接口
     */
    public interface OnItemClickListener {
        void onItemNameTvClick(View v, String name);//点击历史纪录名称时
        void onItemDeleteImgClick(View v, String name);//点击删除按钮时
    }
}
