package com.example.reading.Bean;

public class BookCommentTwo {
    private int cbid;
    private int type;
    private String rurl;
    private String bimg;
    private String bname;
    private String yurl;
    private String author;
    private String description;
    private String music_path;
    private String video_path;
    public BookCommentTwo(int type,String bname,String author,String bimg,String rurl,String vurl){
        this.type = type;
        this.bname = bname;
        this.author = author;
        this.bimg = bimg;
        this.rurl = rurl;
        this.yurl = vurl;
    }

}
