package com.example.reading.Bean;


/**
 * create by czj 2019/10/23
 */
public class ReplyDetailBean {
    private int rid;
    private String username;
    private String uimg;
    private int uid;
    private String rcreateTime;
    private String content;
    private int loveCount;
    private int status;
    public ReplyDetailBean(String username, String content, String rcreateTime) {
        this.username = username;
        this.content = content;
        this.rcreateTime=rcreateTime;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getRcreateTime() {
        return rcreateTime;
    }

    public void setRcreateTime(String rcreateTime) {
        this.rcreateTime = rcreateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }
}
