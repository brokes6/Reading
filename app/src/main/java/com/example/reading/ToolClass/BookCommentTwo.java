package com.example.reading.ToolClass;

import java.util.List;

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

    public String getMusic_path() {
        return music_path;
    }

    public void setMusic_path(String music_path) {
        this.music_path = music_path;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRurl() {
        return rurl;
    }

    public void setRurl(String rurl) {
        this.rurl = rurl;
    }

    public String getBimg() {
        return bimg;
    }

    public void setBimg(String bimg) {
        this.bimg = bimg;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getYurl() {
        return yurl;
    }

    public void setYurl(String yurl) {
        this.yurl = yurl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCbid() {
        return cbid;
    }

    public void setCbid(int cbid) {
        this.cbid = cbid;
    }

}
