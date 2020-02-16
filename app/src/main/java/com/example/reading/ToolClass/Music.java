package com.example.reading.ToolClass;

public class Music {
        private String music_path;
        private String music_img;
        public Music(String data,String img){
            this.music_path= data;
            this.music_img= img;
        }

    public String getMusic_path() {
        return music_path;
    }

    public void setMusic_path(String music_path) {
        this.music_path = music_path;
    }

    public String getMusic_img() {
        return music_img;
    }

    public void setMusic_img(String music_img) {
        this.music_img = music_img;
    }
}


