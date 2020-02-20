package com.example.reading.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.reading.Bean.PostComment;
import com.example.reading.Bean.ReplyDetailBean;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.DateTimeUtil;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: fuxinbo
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private List<PostComment> commentBeanList=new ArrayList<>();
    private static final int REPLYNUM=3;
    private Context context;
    private int pageIndex = 1;
    public CommentExpandAdapter(Context context, List<PostComment> commentBeanList) {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    public void setCommentBeanList(List<PostComment> commentBeanList) {
        if(commentBeanList==null){
            this.commentBeanList=commentBeanList;
        }else {
            this.commentBeanList.addAll(commentBeanList);
        }
        notifyDataSetChanged();
    }
    public List<PostComment> getCommentBeanList() {
        return commentBeanList;
    }
    @Override
    public int getGroupCount() {
        Log.i(TAG, "getGroupCount: "+commentBeanList.size());
        return commentBeanList.size();
    }
    @Override
    public int getChildrenCount(int i) {
        return -1;
    }
    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }
    @Override
    public Object getChild(int i, int i1) {
        return null;
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;
        final PostComment bean=commentBeanList.get(groupPosition);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        }else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        String uimg=bean.getUimg();
        if(!(uimg==null)&&!uimg.trim().equals("")){
            groupHolder.logo.setCacheImageURL(bean.getUimg());
        }
        groupHolder.tv_name.setText(bean.getUsername());
        groupHolder.tv_time.setText(DateTimeUtil.handlerDateTime(bean.getCcreateTime()));
        groupHolder.tv_content.setText(bean.getContent());
        groupHolder.status=bean.getLoveStatus();
        groupHolder.loveNum.setText(String.valueOf(bean.getLoveCount()));
        groupHolder.position=groupPosition;
        if(groupHolder.status==1){
            groupHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        else{
            groupHolder.iv_like.setColorFilter(Color.parseColor("#aaaaaa"));
        }
        groupHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupHolder.status==1){
                    groupHolder.status=0;
                    Toast.makeText(context, "取消赞", Toast.LENGTH_SHORT).show();
                    groupHolder.iv_like.setColorFilter(Color.parseColor("#aaaaaa"));
                    groupHolder.loveNum.setText(String.valueOf(Integer.valueOf(groupHolder.loveNum.getText().toString())-1));
                }else {
                    groupHolder.status=1;
                    Toast.makeText(context, "点赞", Toast.LENGTH_SHORT).show();
                    groupHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
                    groupHolder.loveNum.setText(String.valueOf(Integer.valueOf(groupHolder.loveNum.getText().toString())+1));
                }
                handlerPostCommentNum(bean.getCid());
            }

        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder{
        private MyImageView logo;
        private TextView tv_name, tv_content, tv_time,loveNum,floorTextView;
        private ImageView iv_like;
        private int status;
        private int position;
        public GroupHolder(View view) {
            logo = view.findViewById(R.id.comment_item_logo);
            tv_content = view.findViewById(R.id.comment_item_content);
            tv_name = view.findViewById(R.id.comment_item_userName);
            tv_time = view.findViewById(R.id.comment_item_time);
            iv_like = view.findViewById(R.id.comment_item_like);
            loveNum = view.findViewById(R.id.loveNum);
            floorTextView=view.findViewById(R.id.comment_item_floor);
        }

    }


    public void addTheCommentData(PostComment postComment){
        if(postComment!=null){
            commentBeanList.add(0,postComment);
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }



    private void handlerPostCommentNum(int cid){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("cid", String.valueOf(cid));
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.HANDLER_POST_COMMENT_LOVE,new BaseCallBack<String>(){

                    @Override
                    protected void OnRequestBefore(Request request) {

                    }

                    @Override
                    protected void onFailure(Call call) {
                        Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {
                        Toast.makeText(context, "请检查网络后重试~", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void inProgress(int progress, long total, int id) {

                    }
                },params);
    }
    public void clearAll(){
        commentBeanList.clear();
        notifyDataSetChanged();
    }
}
