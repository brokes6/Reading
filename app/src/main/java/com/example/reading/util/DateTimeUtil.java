package com.example.reading.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class DateTimeUtil {
    public static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("MM-dd HH:mm");
    public static String handlerDateTime(long datetime){
        long nowDateTime = System.currentTimeMillis();
        String strTime="";
        long spaceTime =nowDateTime-datetime;
        if(spaceTime<86400000){
            if(spaceTime>=3600000){
                strTime=(spaceTime/3600000)+"小时前";
            }else{
                long time=spaceTime/60000;
                if(time==0){
                    strTime="刚刚";
                }else {
                    strTime=time+"分钟前";
                }
            }
        }else{
            strTime=simpleDateFormat1.format(datetime);
        }
        return strTime;
    }
    public static String handlerDateTime(String str){
        if(str.equals("刚刚"))
            return str;
        try {
            return handlerDateTime(simpleDateFormat.parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
