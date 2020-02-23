package com.example.reading.Bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookComment {
    private int cid;

    private int cuid;

    private int cbid;

    private String ccreateTime;

    private String content;

    private int loveCount;

    private String username;

    private String uimg;

    private int loveStatus;

    public BookComment(String ccreateTime, String content, String username, String uimg,int cid) {
        this.ccreateTime = ccreateTime;
        this.content = content;
        this.username = username;
        this.uimg = uimg;
        this.cid=cid;
    }
}
