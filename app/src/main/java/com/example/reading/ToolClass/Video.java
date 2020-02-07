package com.example.reading.ToolClass;

public class Video {
    private String video_path;
    public Video(String data){
        this.video_path= data;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }
}
