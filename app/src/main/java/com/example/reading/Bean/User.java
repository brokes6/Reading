package com.example.reading.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 创建于2019/9/27 15:30🐎
 */
public class User implements Serializable {
    public static final long serialVersionUID=4725L;
    private Integer uid;
    private String account;
    private String username;
    private String password;
    private String ureal_name;
    private String uprofession;
    private String email;
    private String usex;
    private Integer uhobbyid;
    private String uimg;
    private String usign;
    private String token;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUreal_name() {
        return ureal_name;
    }

    public void setUreal_name(String ureal_name) {
        this.ureal_name = ureal_name;
    }

    public String getUprofession() {
        return uprofession;
    }

    public void setUprofession(String uprofession) {
        this.uprofession = uprofession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsex() {
        return usex;
    }

    public void setUsex(String usex) {
        this.usex = usex;
    }

    public Integer getUhobbyid() {
        return uhobbyid;
    }

    public void setUhobbyid(Integer uhobbyid) {
        this.uhobbyid = uhobbyid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUsign() {
        return usign;
    }

    public void setUsign(String usign) {
        this.usign = usign;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
