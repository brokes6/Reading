package com.example.reading.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.ReadActivity;
import com.example.reading.Bean.BookDetails;
import com.example.reading.Bean.HistoryBean;
import com.example.reading.R;
import com.example.reading.util.RoundImageView;

import java.util.ArrayList;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{
    private static final String TAG = "HistoryAdapter";
    private LayoutInflater inflater;
    private Context mContext;
    private List<HistoryBean> historyBean=new ArrayList<>();
    public HistoryAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setHistory(List<HistoryBean> historyBeanList) {
        historyBean.clear();
        this.historyBean.addAll(historyBeanList);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.myhistory_item , parent , false);
        HistoryViewHolder viewHolder = new HistoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        final HistoryBean historyBean1 = historyBean.get(position);
        holder.bookname.setText(historyBean1.getBname());
        holder.bookauthor.setText(historyBean1.getAuthor());
        holder.bookimg.setImageURL(historyBean1.getBimg());
        holder.history_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ReadActivity.class);
                intent.putExtra("id",historyBean1.getBid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyBean.size();
    }
}
class HistoryViewHolder extends RecyclerView.ViewHolder{
    TextView bookname,bookauthor;
    RoundImageView bookimg;
    LinearLayout history_book;
    public HistoryViewHolder(View itemView) {
        super(itemView);
        bookname = itemView.findViewById(R.id.book_name);
        bookauthor = itemView.findViewById(R.id.book_author);
        bookimg = itemView.findViewById(R.id.book_img);
        history_book = itemView.findViewById(R.id.history_book);
    }
}
