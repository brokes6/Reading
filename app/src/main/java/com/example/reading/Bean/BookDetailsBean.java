package com.example.reading.Bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDetailsBean {
    private int bid;
    private int type;
    private String rurl;
    private String bimg;
    private String bname;
    private String author;
    private String description;
    private int loveNum;
    private int forwardNum;
    private int commentNum;
    private BookResource video;
    private BookResource audio;
}
