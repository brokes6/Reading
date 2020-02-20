package com.example.reading.Bean;

import android.graphics.Bitmap;

import java.io.File;

public class LoadFileVo {
    private String path;
    File file;

    int pg; //图片下方的进度条

    boolean isUpload = false; //标识该文件是否上传

    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public LoadFileVo() {
    }

    public LoadFileVo(File file, int pg) {
        this.file = file;
        this.pg = pg;
    }

    public LoadFileVo(String path) {
        this.path = path;
    }

    public LoadFileVo(File file, boolean isUpload, int pg, Bitmap bitmap) {
        this.file = file;
        this.pg = pg;
        this.isUpload = isUpload;
        this.bitmap = bitmap;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getPg() {
        return pg;
    }

    public void setPg(int pg) {
        this.pg = pg;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}