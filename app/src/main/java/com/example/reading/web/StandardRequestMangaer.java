package com.example.reading.web;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

/**
 * 对okhttp的封装
 */
public class StandardRequestMangaer {
    private static final String TAG = "StandardRequestMangaer";
    private static final String GET="GET";
    private static final String POST="POST";

    private static StandardRequestMangaer requestMangaer;

    private OkHttpClient mOkHttpClient;

    private Gson mGson;

    private Handler handler;

    public StandardRequestMangaer() {
        mOkHttpClient=new OkHttpClient();
        mOkHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS).writeTimeout(10,TimeUnit.SECONDS);
        mGson=new Gson();
        handler= new Handler(Looper.getMainLooper());
    }

    public static StandardRequestMangaer getInstance(){
        if(requestMangaer==null){
            requestMangaer=new StandardRequestMangaer();
        }
        return requestMangaer;
    }

    /*********************************
     * 对外公布的方法
     ********************************/


    public void get(String url,BaseCallBack callBack,Map<String,String> params){
        Request request=buildRequest(url,params,GET);
        doRequest(request,callBack);
    }

    public void post(String url,BaseCallBack callBack,Map<String,String> params){
        Request request=buildRequest(url,params,POST);
        doRequest(request,callBack);
    }

    public void postImage(String url, BaseCallBack baseCallBack,String filekey, File file,Map<String,String>params){
        Param[]paramArr=fromMapToParams(params);
        try {
            postAsyn(url,baseCallBack,file,filekey,paramArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*********************************
     * 内部的方法
     ********************************/


    private Request buildRequest(String url, Map<String,String>params,String methodType){
        Request.Builder builder=new Request.Builder();
        if (GET.equals(methodType)){
            StringBuilder urlBuilder = new StringBuilder(url).append("?");
            if(params != null){
                Set<Map.Entry<String,String>> set=params.entrySet();
                for(Map.Entry<String,String> entry: set){
                    //将请求参数遍历添加到我们的请求构件类中
                    urlBuilder.append(entry.getKey()).append("=").
                            append(entry.getValue()).append("&");
                }
            }
            builder.url(urlBuilder.toString());
            builder.get();
        }else if (POST.equals(methodType)){
            RequestBody requestBody=buildFormData(params);
            builder.url(url);
            builder.post(requestBody);
        }
       return builder.build();
    }
    private RequestBody buildFormData(Map<String,String> params){
        FormBody.Builder builder=new FormBody.Builder();
        if(params != null){
            Set<Map.Entry<String,String>> set=params.entrySet();
            for(Map.Entry<String,String> entry: set){
                if (entry.getValue()==null)
                builder.add(entry.getKey(),entry.getValue());
            }
        }
        return builder.build();
    }

    private void doRequest(Request request, final BaseCallBack callBack){
        callBack.OnRequestBefore(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onResponse(response);
                String result=response.body().string();
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        Log.i(TAG, "onResponse: JSON数据为:"+jsonObject);
                        int code=jsonObject.getInt("code");
                        if (code==1){
                            if (callBack.getMType()==String.class){
                                try {
                                    callBackSuccess(callBack,call,response,jsonObject.getString("data"));
                                }catch (JSONException e){
                                    callBackSuccess(callBack,call,response,jsonObject.getString("msg"));
                                }
                                ;
                            }else {
                                Object object = mGson.fromJson(jsonObject.getString("data"), callBack.getMType());
                                callBackSuccess(callBack, call, response, object);
                            }
                        }else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onFailure(call);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onEror(call,response.code());
                            }
                        });
                    }
                }else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onEror(call,response.code());
                        }
                    });
                }
            }
        });
    }
    private void callBackSuccess(final BaseCallBack callBack, final Call call, final Response response, final Object object){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(call,response,object);
            }
        });
    }


    private Param[] fromMapToParams(Map<String, String> params) {
        if (params == null)
            return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }


    //单个文件上传请求 带参数
    private void postAsyn(String url, BaseCallBack callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        doRequest(request, callback);
    }

    //构造上传图片 Request
    private Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, Param[] params) {
        params = validateParam(params);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(MediaType.parse("image/png"), param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else
            return params;
    }
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }
}
