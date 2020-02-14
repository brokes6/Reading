package com.example.reading.Bean;

import java.util.List;

/**
 * Created by moos on 2018/4/20.
 */

public class CommentDetailBean {
    private int uid;
    private int cid;
    private String username;
    private String uimg;
    private String content;
    private int love_count;
    private int replyTotal;//不错 暂定 后期可以添加回复总数
    private String ccreateTime;
    private int status;
    private int floor;
    private List<ReplyDetailBean> replyVoList;

    public CommentDetailBean(String username, String content, String ccreateTime, String uimg) {
        this.username = username;
        this.content = content;
        this.ccreateTime = ccreateTime;
        this.uimg=uimg;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLove_count() {
        return love_count;
    }

    public void setLove_count(int love_count) {
        this.love_count = love_count;
    }

    public int getReplyTotal() {
        return replyTotal;
    }

    public void setReplyTotal(int replyTotal) {
        this.replyTotal = replyTotal;
    }

    public String getCcreateTime() {
        return ccreateTime;
    }

    public void setCcreateTime(String ccreateTime) {
        this.ccreateTime = ccreateTime;
    }

    public List<ReplyDetailBean> getReplyVoList() {
        return replyVoList;
    }

    public void setReplyVoList(List<ReplyDetailBean> replyVoList) {
        this.replyVoList = replyVoList;
    }
}
