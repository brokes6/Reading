package com.example.reading.ToolClass;

public class Video {
    private String video_path;
    private String video_img;
    public Video(String data,String img){
        this.video_path= data;
        this.video_img= img;
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
