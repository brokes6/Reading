package com.example.reading.util;

import com.example.reading.Bean.User;

import java.util.HashMap;
import java.util.Map;

public class UserUtil {
    public static Map<String,String> createUserMap(){
        Map<String,String> map=new HashMap<>();
        map.put("token","OKYPyuQ2adPOIE5EFFlKQEz7nH0KG%2F6TRNMge9uu7Ew%3D");
        return map;
    }

    public static User getUserInfo(){
        User user=new User();
        user.setUsername("汤姆猫");
        user.setUimg("http://117.48.205.198/xiaoyoudushu_resource/post_img/19eb36788041414490a8e8bf75fa33db.jpg");
        return user;
    }
}
