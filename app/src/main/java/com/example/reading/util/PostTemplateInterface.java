package com.example.reading.util;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;


public interface PostTemplateInterface {
    public static final String STANDARD_POPULAR_URL="http://106.54.134.17/app/getPopularPost";
    public static final String STANDARD_NEW_URL="http://106.54.134.17/app/getNewPost";
    public static final String TAG_POPULAR_URL="http://106.54.134.17/app/getPopularPostByTag";
    public static final String TAG_NEW_URL="http://106.54.134.17/app/getNewPostByTag";
    public static final int HANDLER_DATA=1;
    public static final int CANCEL_PROGRESS=2;
    public static final int NOTIFY=3;
    public static final int NOTIFY_NOCOMMENT=4;
    public static final int NOTIFY_COMMENT=5;
    public static final int NO_NETWORK=6;
    public static final int NOTIFY_REPLY=7;
    public static final int SHOWTOAST=8;
    public static final String REQUEST_POST_DETAILS_STR="http://106.54.134.17/app/getPostDetailsById";//mode=1
    public static final String REQUEST_COMMENT_POPULAR_STR="http://106.54.134.17/app/getPopularComments";//mode=2
    public static final String REQUEST_COMMENT_NEW_STR="http://106.54.134.17/app/getNewComments";//mode=2
    public static final String REQUEST_ADD_COMMENT_STR="http://106.54.134.17/app/addComment";//mode=3
    public static final String REQUEST_ADD_COLLECTION="http://106.54.134.17/app/addCollection";
    public static final String REQUEST_DELETE_COLLECTION="http://106.54.134.17/app/deleteCollection";
    public void getPostList(String token);
    public void clearList();
    public RecyclerView getRecycler();
    public void updateInfo(Intent intent);
}
