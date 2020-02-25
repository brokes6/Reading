package com.example.reading.Bean;

import com.google.gson.annotations.JsonAdapter;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPostComment {
    private int cid;

    private int pid;

    private String content;

    @JsonAdapter(DateTypeAdapter.class)
    private Date ccreateTime;

    private String postContent;

    private int commentNum;
}
