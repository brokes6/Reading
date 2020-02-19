package com.example.reading.ToolClass;

public class Video {
    private String video_path;
    private String video_img;
    private int type;
    public Video(String data,String img,int i){
        this.video_path= data;
        this.video_img= img;
        this.type= i;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getVideo_img() {
        return video_img;
    }

    public void setVideo_img(String video_img) {
        this.video_img = video_img;
    }
}
