package com.example.reading.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Bean.ReplyDetailBean;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private static final String TAG = "ReplyAdapter";
    private View convertView;
    private Context mcontext;
    private LayoutInflater layoutInflater;
    private List<ReplyDetailBean> replyDetailBeans=new ArrayList<>();
    private String token;
    public ReplyAdapter(Context mcontext) {
        this.mcontext = mcontext;
        layoutInflater=LayoutInflater.from(mcontext);
        token= FileCacheUtil.getUser(mcontext).getToken();
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        convertView=layoutInflater.from(mcontext).inflate(R.layout.comment_item_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyAdapter.ViewHolder viewHolder, int i) {
        final ReplyDetailBean bean=replyDetailBeans.get(i);
        viewHolder.status=bean.getStatus();
        viewHolder.usernameView.setText(bean.getUsername());
        viewHolder.datetimeView.setText(DateTimeUtil.handlerDateTime(bean.getRcreateTime()));
        viewHolder.userImgView.setImageURL(bean.getUimg());
        viewHolder.replyContentView.setText(bean.getContent());
        viewHolder.loveNum.setText(String.valueOf(bean.getLoveCount()));
        if(viewHolder.status==1){
            viewHolder.loveImage.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        else{
            viewHolder.loveImage.setColorFilter(Color.parseColor("#aaaaaa"));
        }
        viewHolder.loveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.status==1){
                    viewHolder.status=0;
                    Toast.makeText(mcontext, "取消赞", Toast.LENGTH_SHORT).show();
                    viewHolder.loveImage.setColorFilter(Color.parseColor("#aaaaaa"));
                    viewHolder.loveNum.setText(String.valueOf(Integer.valueOf(viewHolder.loveNum.getText().toString())-1));
                }else {
                    viewHolder.status=1;
                    Toast.makeText(mcontext, "点赞", Toast.LENGTH_SHORT).show();
                    viewHolder.loveImage.setColorFilter(Color.parseColor("#FF5C5C"));
                    viewHolder.loveNum.setText(String.valueOf(Integer.valueOf(viewHolder.loveNum.getText().toString())+1));
                }
                updateReplyLove(bean.getRid());
            }

        });
    }

    @Override
    public int getItemCount() {
        return replyDetailBeans.size();
    }

    public void setReplyDetailBeans(List<ReplyDetailBean> replyDetailBeans) {
        this.replyDetailBeans.addAll(replyDetailBeans);
    }
    public void addReplyDetailsBean(ReplyDetailBean detailBean){
        if(detailBean!=null){
            replyDetailBeans.add(0,detailBean);
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("评论数据为空!");
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        MyImageView userImgView;
        TextView usernameView;
        TextView datetimeView;
        TextView replyContentView;
        TextView loveNum;
        ImageView loveImage;
        int status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImgView=itemView.findViewById(R.id.comment_item_logo);
            usernameView=itemView.findViewById(R.id.comment_item_userName);
            datetimeView=itemView.findViewById(R.id.comment_item_time);
            replyContentView=itemView.findViewById(R.id.comment_item_content);
            loveNum=itemView.findViewById(R.id.loveNum);
            loveImage=itemView.findViewById(R.id.comment_item_like);
        }
    }
    private void updateReplyLove(int replyId){
        final Request request =new Request.Builder()
                .url("http://106.54.134.17/app/updateLoveReply?token="+token+"&replyId="+replyId)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: 失败呃");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String dataStr = response.body().string();
                Log.i(TAG, "onResponse: 返回json数据"+dataStr);
                JSONObject jsonObject = null;
                try {
                    jsonObject=new JSONObject(dataStr);
                    int code=jsonObject.getInt("code");
                    if(code==0){
                        Log.i(TAG, "onResponse:点赞/取消赞 遇到未知错误。。");
                        return;
                    }
                    String msg=jsonObject.getString("msg");
                    Log.i(TAG, "onResponse: 响应信息"+msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
