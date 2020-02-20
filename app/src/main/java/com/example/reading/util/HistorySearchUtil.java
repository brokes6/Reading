package com.example.reading.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

public class HistorySearchUtil {
    public static final String CREATE_HISTORY_SEARCH = "create table searchHistory (" +
            "id integer primary key autoincrement, " +
            "name text)";
    private static final String TAG = "HistorySearchUtil";
    private static final String TABLE_NAME="searchHistory";
    public Context mContext;
    public static HistorySearchUtil mHistorySearchUtil;
    private DbHelper dbHelper;

    private HistorySearchUtil(Context context){
        dbHelper=new DbHelper(context,"record.db",null,1);
        this.mContext=context;
    }
    public static HistorySearchUtil getInstance(Context context) {//得到一个实例
        if (mHistorySearchUtil == null) {
            mHistorySearchUtil = new HistorySearchUtil(context);
        } else if ((!mHistorySearchUtil.mContext.getClass()
                .equals(context.getClass()))) {////判断两个context是否相同
            mHistorySearchUtil = new HistorySearchUtil(context);
        }
        return mHistorySearchUtil;
    }
    /**
     * 添加一条新纪录
     * @param name
     */
    public void putNewSearch(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!isExist(name)) {//判断新纪录是否存在，不存在则添加
            ContentValues values = new ContentValues();
            values.put("name", name);
            db.insert(TABLE_NAME, null, values);
        }
    }

    /**
     * 判断记录是否存在
     * @param name
     * @return
     */
    public boolean isExist(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where name = ?",
                new String[]{name});
        if (cursor.moveToFirst()) {//如果存在
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询所有历史纪录
     * @return
     */
    public List<String> queryHistorySearchList() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<String> list = new ArrayList<String>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                list.add(name);
            } while(cursor.moveToNext());
        }
        return list;
    }

    /**
     * 删除单条记录
     * @param name
     */
    public void deleteHistorySearch(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (isExist(name)) {
            db.delete(TABLE_NAME, "name = " + "'" + name + "'", null);
        }
    }

    /**
     * 删除所有记录
     */
    public void deleteAllHistorySearch() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
    
}
