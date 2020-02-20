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

import com.example.reading.Activity.XPlayMusic;
import com.example.reading.R;
import com.example.reading.Bean.ProgramBean;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramViewHolder>{
    private LayoutInflater inflater;
    private Context mContext;
    private List<ProgramBean> programBeans=new ArrayList<>();

    public ProgramAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setProgramAdapter(List<ProgramBean> programBeans1) {
//        programBeans.clear();
        this.programBeans.addAll(programBeans1);
        Log.i(TAG, "ProgramAdapter开始设置"+this.programBeans.size());
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.program_item, parent, false);
        ProgramViewHolder viewHolder = new ProgramViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {
        Log.i(TAG, "ProgramAdapter: 执行第"+position+"边");
        final ProgramBean programBean1s = programBeans.get(position);
        Log.i(TAG, "MAdapter: " + programBean1s.getMid());
        holder.id.setText(programBean1s.getAid());
        holder.text.setText(programBean1s.getDescription());
        holder.publish_time.setText(programBean1s.getDate());
        Log.d(TAG, "onBindViewHolder: -------------------"+programBean1s.getDate());
        //还差播放量，评论数,点击进入播放器
        holder.whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, XPlayMusic.class);
                intent.putExtra("url",programBean1s.getUrl());
                intent.putExtra("img",programBean1s.getImg());
                intent.putExtra("name",programBean1s.getTitle());
                Log.d(TAG, "onClick: Madapter适配器"+"传值完成,音频url为："+programBean1s.getUrl()+"名字为 :"+programBean1s.getTitle());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return programBeans.size();
    }

    //下面两个方法提供给页面刷新和加载时调用
    public void add(List<ProgramBean> programbeanlist){
        //增加数据
        int position = programBeans.size();
        programBeans.addAll(position, programbeanlist);
        notifyItemInserted(position);
    }

}



class ProgramViewHolder extends RecyclerView.ViewHolder{

    TextView id,text,publish_time;
    LinearLayout whole;

    public ProgramViewHolder(View itemView) {
        super(itemView);
        whole = itemView.findViewById(R.id.whole);
        id = itemView.findViewById(R.id.program_id);
        text = itemView.findViewById(R.id.program_text);
        publish_time = itemView.findViewById(R.id.program_Publish);

    }
}