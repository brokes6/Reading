package com.example.reading.Bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建于2019/9/16 21:00🐎
 */
@Getter
@Setter
@ToString
public class Post {
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
}