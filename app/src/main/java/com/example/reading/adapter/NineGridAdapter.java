package com.example.reading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.Activity.PostDetails;
import com.example.reading.Bean.Post;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.NetWorkUtil;
import com.example.reading.util.PostTemplateInterface;
import com.example.reading.util.RoundImageView;
import com.example.reading.util.UserUtil;
import com.example.reading.view.StandardNineGridLayout;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by HMY on 2016/8/6
 */
public class NineGridAdapter extends RecyclerView.Adapter<NineGridAdapter.ViewHolder> {
    private static final String TAG = "NineGridAdapter";
    private PostCallBack postCallBack;
    private Context mContext;
    private Fragment fragment;
    private View convertView;
    private List<Post> mList=new ArrayList<>();
    protected LayoutInflater inflater;
    public NineGridAdapter(Fragment fragment) {
        this.fragment=fragment;
        this.mContext = fragment.getContext();
        inflater = LayoutInflater.from(this.mContext);
    }
    public void setList(List<Post> list) {
        mList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        convertView = inflater.inflate(R.layout.comment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    //将从服务器获取的值设置上去
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Post post=mList.get(position);
        Log.i(TAG, "onBindViewHolder: post="+post);
        String content=Html.fromHtml(post.getContent()).toString();
        String imgUrls=post.getImgurl();
        holder.content.setText(content);//设置内容
        holder.datetime.setText(DateTimeUtil.handlerDateTime(post.getPcreateTime()));//设置帖子时间
        Glide.with(mContext).load(post.getUimg()).into(holder.uimg);//设置头像
        holder.username.setText(post.getUsername());//设置用户名
        holder.postId=post.getPid();//
        holder.loveStatus=post.getLoveStatus();
        holder.talkNumStr.setText(String.valueOf(post.getCommentNum()));
        holder.loveNumStr.setText(String.valueOf(post.getLoveNum()));
        if (TextUtils.isEmpty(imgUrls)){
            holder.layout.setVisibility(View.GONE);
        }else {

            holder.layout.setUrlList(Arrays.asList(imgUrls.split(",")));
        }
        holder.loveNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.loveStatus==1){
                    holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.thumbs_up_black));
                    holder.loveStatus=0;
                    //加加
                    holder.loveNumStr.setText(String.valueOf(Integer.valueOf(holder.loveNumStr.getText().toString())-1));
                }else{
                    holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.thumbs_up_wanc));
                    holder.loveStatus=1;
                    //减减
                    holder.loveNumStr.setText(String.valueOf(Integer.valueOf(holder.loveNumStr.getText().toString())+1));
                }
                handlerLove(post.getPid());
            }
        });

        if(holder.loveStatus==0){
            holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.thumbs_up_black));
        }else{
            holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.thumbs_up_wanc));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            //跳转的时候的动画
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetails.class);
                intent.putExtra("postId",mList.get(position).getPid());
                intent.putExtra("position",position);
                fragment.startActivityForResult(intent, CommunityFragment.POSTDETAILS);
            }
        });
    }

    @Override
    public int getItemCount() {
        //获取长度
        return getListSize(mList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        StandardNineGridLayout layout;
        RoundImageView uimg;
        TextView datetime,content,username,loveNumStr,collectionStr,talkNumStr;
        ImageView loveNum,collection;
        int postId,loveStatus,collectionStatus;
        public ViewHolder(View itemView) {
            //获取各种组件
            super(itemView);
            this.view=itemView;
            layout = (StandardNineGridLayout) itemView.findViewById(R.id.layout_nine_grid);
            username=itemView.findViewById(R.id.com_username);
            uimg=itemView.findViewById(R.id.com_userimg);
            datetime=itemView.findViewById(R.id.com_time);
            content=itemView.findViewById(R.id.com_text);
            loveNum=itemView.findViewById(R.id.loveNum);
//            collection=itemView.findViewById(R.id.collection);
            loveNumStr=itemView.findViewById(R.id.loveNumStr);
//            collectionStr=itemView.findViewById(R.id.collectionStr);
            talkNumStr=itemView.findViewById(R.id.talkNumStr);
            collection=itemView.findViewById(R.id.collection);
            collectionStr=itemView.findViewById(R.id.collectionStr);
        }

    }

    private int getListSize(List<Post> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    private void handlerLove(int pid){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("pid", String.valueOf(pid));
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.HANDLER_POST_LOVE,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {

                    }

                    @Override
                    protected void onFailure(Call call) {

                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        Toast.makeText(mContext, "操作成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {
                        Toast.makeText(mContext, "请检查网络后重试", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    protected void inProgress(int progress, long total, int id) {

                    }
                },params);
    }
    public interface PostCallBack{
        void handlerLove();
    }
}
