package com.example.reading.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.PostDetails;
import com.example.reading.Bean.Post;
import com.example.reading.Fragment.CommunityFragment;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.NetWorkUtil;
import com.example.reading.util.PostTemplateInterface;
import com.example.reading.view.NineGridTestLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class NineGridTest2Adapter extends RecyclerView.Adapter<NineGridTest2Adapter.ViewHolder> {
    private static final String TAG = "NineGridTest2Adapter";
    private MyImageView tieze_user_img;
    private Context mContext;
    private View convertView;
    private List<Post> mList=new ArrayList<>();
    protected LayoutInflater inflater;
    private NetWorkUtil netWorkUtil;
    private String token;
    private ViewHolder nowViewHolder;
    private int nowPosition;
    public NineGridTest2Adapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        netWorkUtil=new NetWorkUtil(context);
        token= FileCacheUtil.getUser(context).getToken();
    }

    public void setList(List<Post> list) {
        mList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //获取模板 item_bbs_nine_grid
        convertView = inflater.inflate(R.layout.comment_item, parent, false);
        //将获取到的模板传入ViewHolder
        ViewHolder viewHolder = new ViewHolder(convertView);
        Log.i(TAG, "onCreateViewHolder:111");
        return viewHolder;
    }

    @Override
    //将从服务器获取的值设置上去
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Post post=mList.get(position);
        String content=Html.fromHtml(post.getContent()).toString();
        String imgUrls=post.getImgUrl();
        Log.i(TAG, "onBindViewHolder: imgUrls="+imgUrls);
        holder.content.setText(content);
        holder.datetime.setText(DateTimeUtil.handlerDateTime(post.getPcreateTime()));
        holder.uimg.setImageURL(post.getUimg());
        holder.username.setText(post.getUsername());
        holder.postId=post.getPid();
        holder.loveStatus=post.getStatus();
        holder.collectionStatus=post.getCollection();
        holder.talkNumStr.setText(String.valueOf(post.getCommentCount()));
        holder.loveNumStr.setText(String.valueOf(post.getLoveCount()));
        if (imgUrls==null||imgUrls.trim().equals("")){
            holder.layout.setVisibility(View.GONE);
        }else {
            holder.layout.setUrlList(Arrays.asList(imgUrls.split(",")));
            holder.layout.setIsShowAll(mList.get(position).isShowAll());
        }
//        holder.layout.setContent(content);
        holder.loveLayout.setOnClickListener(new View.OnClickListener(){
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

                post.setStatus(holder.loveStatus);
                post.setLoveCount(Integer.valueOf(holder.loveNumStr.getText().toString()));
                netWorkUtil.updatePostLove(holder.postId,token);
            }
        });
        holder.collectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerCollection(post.getPid(),token,holder.collectionStatus);
                if(holder.collectionStatus==1){
                    holder.collection.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.shocang));
                    holder.collectionStatus=0;
                    holder.collectionStr.setText("未收藏");
                    Toast.makeText(mContext, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                }else {
                    holder.collection.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.shocangwanc));
                    holder.collectionStatus=1;
                    holder.collectionStr.setText("已收藏");
                    Toast.makeText(mContext, "收藏成功！", Toast.LENGTH_SHORT).show();

                }
                post.setCollection(holder.collectionStatus);
                Log.i(TAG, "onClick: 收藏的值未:"+post.getCollection());
            }
        });
        if(holder.loveStatus==1){
            holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.thumbs_up_black));
        }else{
            holder.loveNum.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.thumbs_up_wanc));
        }
        if(holder.collectionStatus==1){
            holder.collection.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.shocang));
            holder.collectionStr.setText("已收藏");
        }else {
            holder.collection.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.shocangwanc));
            holder.collectionStr.setText("未收藏");
        }
        //这里还没搞收藏的点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            //跳转的时候的动画
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 随便响应"+mList.get(position).getPid());
                Intent intent = new Intent(mContext, PostDetails.class);
                //
                intent.putExtra("postId",mList.get(position).getPid());
                //获取两个页面的共同值
                androidx.core.util.Pair<View, String> uimg = new androidx.core.util.Pair(holder.uimg, "userphoto");
                androidx.core.util.Pair<View, String> username = new androidx.core.util.Pair(holder.username, "username");
                androidx.core.util.Pair<View, String> image = new androidx.core.util.Pair(holder.layout, "image");
                androidx.core.util.Pair<View, String> text = new androidx.core.util.Pair(holder.datetime, "time");
                androidx.core.util.Pair<View, String> longtext = new androidx.core.util.Pair(holder.content, "longtext");
                ActivityOptionsCompat optionsCompat =
                        //将获取到的共同值传入，随后进行跳转
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, image,text,longtext,uimg,username);
                nowViewHolder=holder;
                ((Activity) mContext).startActivityForResult(intent, CommunityFragment.POSTDETAILS,optionsCompat.toBundle());
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowViewHolder=holder;
                nowPosition=position;
            }
        });
        holder.layout.setInfo(post.getContent(),String.valueOf(post.getLoveCount()),String.valueOf(post.getCommentCount()),post.getStatus(),post.getCollection(),post.getPid(),null);
    }

    @Override
    public int getItemCount() {
        //获取长度
        return getListSize(mList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        NineGridTestLayout layout;
        LinearLayout loveLayout,collectionLayout;
        MyImageView uimg;
        TextView datetime,content,username,loveNumStr,collectionStr,talkNumStr;
        ImageView loveNum,collection;
        int postId,loveStatus,collectionStatus;
        public ViewHolder(View itemView) {
            //获取各种组件
            super(itemView);
            this.view=itemView;
            layout = (NineGridTestLayout) itemView.findViewById(R.id.layout_nine_grid);
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
    public void clear(){
        mList=null;
        mList=new ArrayList<>();
    }
    public void updateInfo(Intent intent){
        Log.i(TAG, "updateInfo: intent="+intent);
        Log.i(TAG, "updateInfo: nowHolder="+nowViewHolder.content.getText().toString());
        String loveNum=intent.getStringExtra("loveNum");
        String talkNum=intent.getStringExtra("talkNum");
        int status=intent.getIntExtra("status",0);
        int collectionStatus=intent.getIntExtra("collectionStatus",0);
        Log.i(TAG, "updateInfo: loveNum="+loveNum);
        Post post=mList.get(nowPosition);
        post.setLoveCount(Integer.valueOf(loveNum));
        post.setCommentCount(Integer.parseInt(talkNum));
        post.setStatus(status);
        Log.i(TAG, "updateInfo: 收藏的值未"+collectionStatus);
        post.setCollection(collectionStatus);
        notifyDataSetChanged();
    }

    public void setNowViewHolder(ViewHolder nowViewHolder) {
        this.nowViewHolder = nowViewHolder;
    }
    public void handlerCollection(int postId,String token,int status){
        RequestBody requestBody=new FormBody.Builder()
                .add("postId", String.valueOf(postId))
                .add("token",token)
                .build();
        Request request=new Request.Builder()
                .post(requestBody)
                .url(getRequestUrl(status))
                .build();
        OkHttpClient client=new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData =response.body().string();
                Log.i(TAG, "onResponse: "+responseData);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(responseData);
                    int code =jsonObject.getInt("code");
                    String msg=jsonObject.getString("msg");
                    if(code==0){
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private String getRequestUrl(int status){
        return (status==1? PostTemplateInterface.REQUEST_DELETE_COLLECTION:PostTemplateInterface.REQUEST_ADD_COLLECTION);
    }
}
