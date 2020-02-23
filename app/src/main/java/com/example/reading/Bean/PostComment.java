package com.example.reading.Bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostComment {
    private int cid;

    private int cuid;

    private int cpid;

    private String ccreateTime;

    private String content;

    private int loveCount;

    private String username;

    private String uimg;

    private int loveStatus;
    public PostComment(String ccreateTime, String content, String username, String uimg,int cid) {
        this.ccreateTime = ccreateTime;
        this.content = content;
        this.username = username;
        this.uimg = uimg;
        this.cid=cid;
    }
}
