package com.example.reading.Bean;

/**
 * 创建于2019/9/16 21:00🐎
 */
public class Post {
    private Integer pid;
    private Integer uid;
    private String pcreateTime;
    //现在觉得不合适 后期修改 应该改为string
    private Integer ptag;
    private String content;
    private Integer loveCount;
    private Integer pviewNum;
    private String imgUrl;
    private String uimg;
    private String username;
    private boolean isShowAll = false;
    private int status;
    private int commentCount;
    private int collection;

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getPcreateTime() {
        return pcreateTime;
    }

    public void setPcreateTime(String pcreateTime) {
        this.pcreateTime = pcreateTime;
    }

    public Integer getPtag() {
        return ptag;
    }

    public void setPtag(Integer ptag) {
        this.ptag = ptag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(Integer loveCount) {
        this.loveCount = loveCount;
    }

    public Integer getPviewNum() {
        return pviewNum;
    }

    public void setPviewNum(Integer pviewNum) {
        this.pviewNum = pviewNum;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
    }
}