package com.example.reading.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetWorkUtil {
    private static final String TAG = "NetWorkUtil";
    private Context context;
    private HandlerUtil handlerUtil;
    public NetWorkUtil(Context context){
        this.context=context;
        handlerUtil=new HandlerUtil(context);
    }
    public  void updatePostLove(int postId,String token){
        final Request request = new Request.Builder()
                .url("http://106.54.134.17/app/updateLovePost?token="+token+"&postId="+postId)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody=response.body().string();
                Log.i(TAG, "onResponse: 返回消息"+responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    int code=jsonObject.getInt("code");
                    if(code!=1){
                        handlerUtil.sendToast("请求失败!请稍后再试");
                        return;
                    }
                    String msg = jsonObject.getString("msg");
                    handlerUtil.sendToast(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
