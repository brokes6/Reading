package com.example.reading.Bean;

import com.google.gson.annotations.JsonAdapter;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserBookComment {
    private int cid;

    private int bid;

    private String content;

    private String description;

    private String bname;

    private String bimg;

    @JsonAdapter(DateTypeAdapter.class)
    private Date ccreateTime;

    private int commentNum;
}
