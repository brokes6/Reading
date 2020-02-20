package com.example.reading.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.PostDetails;
import com.example.reading.Activity.ReadActivity;
import com.example.reading.Bean.SearchResult;
import com.example.reading.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private View convertView;
    private Context mcontext;
    private LayoutInflater inflater;
    private List<SearchResult> searchResultList=new ArrayList<SearchResult>();
    public SearchResultAdapter(Context context) {
        mcontext=context;
        inflater=LayoutInflater.from(context);
    }

    public void setSearchResultList(List<SearchResult> searchResultList) {
        this.searchResultList = searchResultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        convertView=inflater.inflate(R.layout.result_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchResult result=searchResultList.get(position);
            if (result.getType().equals("BOOK")){
                holder.resultType.setText("书籍");
            }else if(result.getType().equals("POST")){
                holder.resultType.setText("帖子");
            }else {
                holder.resultType.setText("用户");
            }
            holder.result.setText(result.getContent());
            holder.totalLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=null;
                    if (result.getType().equals("BOOK")){
                        intent=new Intent(mcontext, ReadActivity.class);
                        mcontext.startActivity(intent);
                    }else if(result.getType().equals("POST")){
                        holder.resultType.setText("帖子");
                        intent=new Intent(mcontext, PostDetails.class);
                        mcontext.startActivity(intent);
                    }else {
                        holder.resultType.setText("用户");
                        Toast.makeText(mcontext, "暂无跳转页面", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView result,resultType;
        LinearLayout totalLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            result=itemView.findViewById(R.id.result);
            resultType=itemView.findViewById(R.id.resultType);
            totalLayout=itemView.findViewById(R.id.totalLayout);
        }
    }
}
