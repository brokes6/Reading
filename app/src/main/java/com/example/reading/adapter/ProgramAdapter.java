package com.example.reading.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.XPlayMusic;
import com.example.reading.R;
import com.example.reading.Bean.ProgramBean;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.reading.MainApplication.getContext;

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
        holder.id.setText(programBean1s.getMid());
        holder.text.setText(programBean1s.getDescription());
        holder.publish_time.setText(programBean1s.getDate());
        holder.playNum.setText(programBean1s.getPlayNum());
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
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //创建下载任务,downloadUrl就是下载链接
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(programBean1s.getUrl()));
                        //指定下载路径和下载文件名
                        request.setDestinationInExternalPublicDir("/download/", programBean1s.getTitle());
                        //获取下载管理器
                        DownloadManager downloadManager= (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        //将下载任务加入下载队列，否则不会进行下载
                        downloadManager.enqueue(request);
                        Looper.prepare();
                        Toast.makeText(getContext(),"正在下载，请注意通知栏!",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
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
    ImageView download;
    TextView id,text,publish_time,playNum;
    LinearLayout whole;

    public ProgramViewHolder(View itemView) {
        super(itemView);
        whole = itemView.findViewById(R.id.whole);
        id = itemView.findViewById(R.id.program_id);
        text = itemView.findViewById(R.id.program_text);
        publish_time = itemView.findViewById(R.id.program_Publish);
        playNum  = itemView.findViewById(R.id.program_paly);
        download = itemView.findViewById(R.id.program_download);

    }
}
