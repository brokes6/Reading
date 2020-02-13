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
import com.example.reading.Activity.XPlayMusic;
import com.example.reading.R;
import com.example.reading.ToolClass.BookDetails;
import com.example.reading.ToolClass.ProgramBean;

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
        programBeans.clear();
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
        Log.i(TAG, "ProgramAdapter: 开始加载");
        final ProgramBean programBean1s = programBeans.get(position);
        Log.i(TAG, "MAdapter: " + programBean1s.getMid());
        holder.id.setText(programBean1s.getAid());
        holder.text.setText(programBean1s.getDescription());
        holder.publish_time.setText(programBean1s.getData());
        //还差播放量，评论数,点击进入播放器
        holder.whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, XPlayMusic.class);
                intent.putExtra("id",programBean1s.getMid());
                Log.d(TAG, "onClick: Madapter适配器"+"传值完成,书籍id为："+programBean1s.getMid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ---------------"+programBeans.size());
        return programBeans.size();
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
