package com.example.reading.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.reading.Bean.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.reading.MainApplication.getContext;

public class UserUtil {
    private static final String TAG = "UserUtil";
    private static User userData = FileCacheUtil.getUser(getContext());
    public static Map<String,String> createUserMap(){
        Map<String,String> map=new HashMap<>();
        map.put("token",userData.getToken());
        return map;
    }

    public static List<String> handlerSmallPostImg(String img){
        if (TextUtils.isEmpty(img)||"null".equals(img)) {
            return null;
        }
        List<String> source= Arrays.asList(img.split(","));
        List<String> target= new ArrayList<>();
        String temp="";
        int index=-1;
        for (String s:source){
            Log.e(TAG, "handlerPostImg: s="+s);
            index=s.lastIndexOf(".");
            Log.e(TAG, "handlerPostImg: index="+index);
            temp=s.substring(0,index)+"small"+s.substring(index);
            Log.e(TAG, "handlerPostImg: temp"+temp);
            target.add(temp);
        }
        return target;
    }
    public static List<String> handlerStandardPostImg(String img){
        if (TextUtils.isEmpty(img)||"null".equals(img)) {
            return null;
        }
        return Arrays.asList(img.split(","));
    }
}
