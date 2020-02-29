package com.example.reading.Bean;

import android.content.Context;

import com.example.reading.util.FileCacheUtil;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建于2019/9/16 21:00🐎
 */
@Getter
@Setter
@ToString
public class Post implements Serializable {
    private Integer pid;
    private Integer puid;
    private String pcreateTime;
    private String content;
    private String imgurl;
    private String username;
    private String uimg;
    private int loveNum;
    private int commentNum;
    private int forwardNum;
    private int loveStatus;
    private List<String> smallImgUrls;
    private List<String> imgUrls;
    public static Post createNowPost(Context context,String content, String imgurl,int postId){

        Post post=new Post();
        post.setPid(postId);
        post.setContent(content);
        post.setImgurl(imgurl);
        User user= FileCacheUtil.getUser(context);
        post.setUsername(user.getUsername());
        post.setUimg(user.getUimg());
        post.setPcreateTime("刚刚");
        return post;
    }
}