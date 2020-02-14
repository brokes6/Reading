package com.example.reading.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostHitoryUtil {
    public static final String PREFERENCE_NAME="post_history";
    public static final String SEARCH_HISTORY="postIdStr";
    private static final String TAG = "PostHitoryUtil";
    public static void saveSearchHistory(String inputText, Context context){
        Log.i(TAG, "saveSearchHistory: 输入的值"+inputText);
        SharedPreferences sharedPreferences= context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        if(TextUtils.isEmpty(inputText)){
            return;
        }
        boolean flag=true;
        int size=-1;
        int j=0;
        StringBuilder stringBuilder = new StringBuilder();
        List<String> historyList=null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String postHistoryStr=sharedPreferences.getString(SEARCH_HISTORY,"");
        if(!postHistoryStr.trim().equals("")) {
            String[] postsHistory = postHistoryStr.split(",");
            historyList = new ArrayList<>(Arrays.asList(postsHistory));
            editor = sharedPreferences.edit();
            stringBuilder = new StringBuilder();
            Log.i(TAG, "saveSearchHistory: 列表长度为" + historyList.size());
            size = historyList.size() > 20 ? 20 : historyList.size();
        }
        if(size>0){
            //之前添加过的拉
            for (int i=0;i<size;i++){
                Log.i(TAG, "saveSearchHistory:"+historyList.get(i));
                if(flag&&inputText.equals(historyList.get(i))){
                    Log.i(TAG, "saveSearchHistory: 被清除"+historyList.get(i));
                    flag=false;
                    continue;
                }
                stringBuilder.append(historyList.get(i)+",");
                Log.i(TAG, "saveSearchHistory:字符串值"+stringBuilder.toString());
            }
            stringBuilder.insert(0,inputText+",");
            editor.putString(SEARCH_HISTORY,stringBuilder.toString());
            editor.commit();
        }else {
            //之前未添加过
            Log.i(TAG, "saveSearchHistory: 我来拉");
            editor.putString(SEARCH_HISTORY, inputText + ",");
            editor.commit();
        }
    }
    public static String getSearchHistory(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(SEARCH_HISTORY,"");
    }
    public static boolean deleteAllHistory(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        return true;
    }
}
