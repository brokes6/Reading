package com.example.reading.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.example.reading.Bean.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.reading.MainApplication.getContext;


public class FileCacheUtil {
    //定义缓存文件的名字，方便外部调用
    public static final String file = "docs_cache.txt";//缓存文件
    //缓存超时时间
    public static final int CACHE_SHORT_TIMEOUT=1000 * 60 * 5; // 5 分钟

    private static Object data;

    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
 /*   private static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:

            }
        }
    };*/
    /**设置缓存
     content是要存储的内容，可以是任意格式的，不一定是字符串。
     */
    public static void setCache(String content, Context context, String cacheFileName, int mode) {
        FileOutputStream fos = null;
        try {

            //打开文件输出流，接收参数是文件名和模式
            fos = context.openFileOutput(cacheFileName,mode);
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读取缓存，返回字符串（JSON）
    public static String getCache(Context context, String cacheFileName) {
        FileInputStream fis = null;
        StringBuffer sBuf = new StringBuffer();
        try {
            fis = context.openFileInput(cacheFileName);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                sBuf.append(new String(buf,0,len));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(sBuf != null) {
            return sBuf.toString();
        }
        return null;
    }

    public static String getCachePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
    public static void setCache(Object o,Context context, String cacheFileName, int mode) {
        FileOutputStream fileOutputStream=null;
        ObjectOutputStream objectOutputStream = null;
        File file =new File(Environment.getExternalStorageDirectory().toString() + File.separator+cacheFileName);
        Log.i(TAG, "setCache: filePath"+file.toString());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream = new FileOutputStream(file.toString());
            objectOutputStream=new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(objectOutputStream!=null){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static <T extends Serializable> T getCache(Context context, String cacheFileName, int mode,Class<T> tClass){
            FileInputStream fileInputStream=null;
            ObjectInputStream objectInputStream=null;
            File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + cacheFileName);
            try {
                fileInputStream=new FileInputStream(file);
                objectInputStream=new ObjectInputStream(fileInputStream);
                T t= (T) objectInputStream.readObject();
                Log.i(TAG, "getCache: t="+t);
                data=t;
                return t;
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;

    }
    public static User getUser(Context context){
        return FileCacheUtil.getCache(context,"USERDATA.txt",0, User.class);
    }

    public static void updateUser(User user,Context context){
        setCache(user,context,"USERDATA.txt",0);
        data=null;
    }
    public static void clearData(){
        data=null;
    }

 /*   public static void setHistory(byte []b,String cacheFileName){
        FileOutputStream fileOutputStream=null;
        FileInputStream fileInputStream=null;
        File file=null;
        try {
             file=new File(Environment.getExternalStorageDirectory().toString() + File.separator+cacheFileName);
             fileOutputStream=new FileOutputStream(file);
             if(!file.exists())
                 file.createNewFile();
             fileOutputStream.write(b,0,b.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
            }
        }
    }
    public static void setHistory(int postId){

    }*/
}