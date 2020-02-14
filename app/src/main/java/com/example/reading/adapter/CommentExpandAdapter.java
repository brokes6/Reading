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


import com.example.reading.Bean.CommentDetailBean;
import com.example.reading.Bean.ReplyDetailBean;
import com.example.reading.Picture.MyImageView;
import com.example.reading.R;
import com.example.reading.util.DateTimeUtil;

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

/**
 * Author: fuxinbo
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private List<CommentDetailBean> commentBeanList=new ArrayList<>();
    private static final int REPLYNUM=3;
    private Context context;
    private int pageIndex = 1;
    private String token;
    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList, String token) {
        this.context = context;
        this.commentBeanList = commentBeanList;
        this.token=token;
    }

    public void setCommentBeanList(List<CommentDetailBean> commentBeanList) {
        if(commentBeanList==null){
            this.commentBeanList=commentBeanList;
        }else {
            this.commentBeanList.addAll(commentBeanList);
        }
        notifyDataSetChanged();
    }
    public List<CommentDetailBean> getCommentBeanList() {
        return commentBeanList;
    }
    @Override
    public int getGroupCount() {
        Log.i(TAG, "getGroupCount: "+commentBeanList.size());
        return commentBeanList.size();
    }
    @Override
    public int getChildrenCount(int i) {
        if(i>=commentBeanList.size()){
            Log.e(TAG, "getChildrenCount: ????");
            return 0;
        }
        List<ReplyDetailBean> detailBeans=commentBeanList.get(i).getReplyVoList();
        if(detailBeans==null||detailBeans.size()==0){
            return 0;
        }else {
            Log.e(TAG, "getChildrenCount: 长度"+detailBeans.size());
            return detailBeans.size()>2 ?REPLYNUM :detailBeans.size();
        }
    }
    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }
    @Override
    public Object getChild(int i, int i1) {
        Log.e(TAG, "getChild: 天天被调用");
        return commentBeanList.get(i).getReplyVoList().get(i1);
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
        final CommentDetailBean bean=commentBeanList.get(groupPosition);
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
        groupHolder.status=bean.getStatus();
        groupHolder.loveNum.setText(String.valueOf(bean.getLove_count()));
        groupHolder.position=groupPosition;
        groupHolder.floorTextView.setText("第"+bean.getFloor()+"层");
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
                updateCommentLove(bean.getCid());
            }

        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        CommentDetailBean commentDetailBean=commentBeanList.get(groupPosition);
        List<ReplyDetailBean> replyDetailBeans =commentDetailBean.getReplyVoList();
        Log.i(TAG, "getChildView:groupPosition="+groupPosition+"childposition="+childPosition);
        if(replyDetailBeans.size()>=3&&childPosition==2){
            Log.i(TAG, "getChildView: 最后一个啦你不操作一下?\nchildPosition="+childPosition);
            convertView= LayoutInflater.from(context).inflate(R.layout.look_more_layout,viewGroup, false);
            return convertView;
        }

            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item_layout,viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        Log.i(TAG, "getChildView: childHolder="+childHolder);
        String replyUser = replyDetailBeans.get(childPosition).getUsername();
        if(!TextUtils.isEmpty(replyUser)){
            childHolder.tv_name.setText(replyUser + ":");
        }else {
            childHolder.tv_name.setText("无名"+":");
        }
        childHolder.tv_content.setText(replyDetailBeans.get(childPosition).getContent());
        return convertView;
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
            tv_content = (TextView) view.findViewById(R.id.comment_item_content);
            tv_name = (TextView) view.findViewById(R.id.comment_item_userName);
            tv_time = (TextView) view.findViewById(R.id.comment_item_time);
            iv_like = (ImageView) view.findViewById(R.id.comment_item_like);
            loveNum=view.findViewById(R.id.loveNum);
            floorTextView=view.findViewById(R.id.comment_item_floor);
        }

    }

    private class ChildHolder{
        private TextView tv_name, tv_content;
        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (TextView) view.findViewById(R.id.reply_item_content);
        }
    }


    /**
     * by moos on 2018/04/20
     * func:评论成功后插入一条数据
     * @param commentDetailBean 新的评论数据
     */
    public void addTheCommentData(CommentDetailBean commentDetailBean){
        if(commentDetailBean!=null){
            commentBeanList.add(commentDetailBean);
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:回复成功后插入一条数据
     * @param replyDetailBean 新的回复数据
     */
    public void addTheReplyData(ReplyDetailBean replyDetailBean, int groupPosition){
        if(replyDetailBean!=null){
            Log.e(TAG, "addTheReplyData: >>>>该刷新回复列表了:"+replyDetailBean.toString() );
            if(commentBeanList.get(groupPosition).getReplyVoList() != null ){
                commentBeanList.get(groupPosition).getReplyVoList().add(replyDetailBean);
            }else {
                List<ReplyDetailBean> replyList = new ArrayList<>();
                replyList.add(replyDetailBean);
                commentBeanList.get(groupPosition).setReplyVoList(replyList);
            }
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("回复数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:添加和展示所有回复
     * @param replyBeanList 所有回复数据
     * @param groupPosition 当前的评论
     */
    private void addReplyList(List<ReplyDetailBean> replyBeanList, int groupPosition){
        if(commentBeanList.get(groupPosition).getReplyVoList() != null ){
            commentBeanList.get(groupPosition).getReplyVoList().clear();
            commentBeanList.get(groupPosition).getReplyVoList().addAll(replyBeanList);
        }else {

            commentBeanList.get(groupPosition).setReplyVoList(replyBeanList);
        }

        notifyDataSetChanged();
    }
    private void updateCommentLove(int commentId){
        final Request request =new Request.Builder()
                .url("http://106.54.134.17/app/updateLoveComment?token="+token+"&commentId="+commentId)
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
    public void clearAll(){
        commentBeanList.clear();
        Log.i(TAG, "clearAll: 数据长度"+commentBeanList.size());
        notifyDataSetChanged();
    }
}
