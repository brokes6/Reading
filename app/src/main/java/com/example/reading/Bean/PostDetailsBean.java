package com.example.reading.Bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDetailsBean {
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
    private PostCommentVo postCommentVo;
}
