package com.example.reading.util;

import com.example.reading.Bean.User;

import java.util.HashMap;
import java.util.Map;

import static com.example.reading.MainApplication.getContext;

public class UserUtil {
    private static User userData = FileCacheUtil.getUser(getContext());
    public static Map<String,String> createUserMap(){
        Map<String,String> map=new HashMap<>();
        map.put("token",userData.getToken());
        return map;
    }

    public static User getUserInfo(){
        User user=new User();
        user.setUsername(userData.getUsername());
        user.setUimg(userData.getUimg());
        return user;
    }
}
