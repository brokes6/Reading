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
            Log.d(TAG, "----------------------------------------"+"文件已存储");
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
            if(data!=null){
                return (T) data;
            }
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
        User user=new User();
        user.setUsername("测试名字");
        user.setUimg("http://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=%E6%B1%A4%E5%A7%86%E7%8C%AB&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&hd=undefined&latest=undefined&copyright=undefined&cs=2542804184,2514449195&os=3699182139,2249991242&simid=3311274204,199557181&pn=16&rn=1&di=71390&ln=1485&fr=&fmq=1582272919046_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=http%3A%2F%2Fimg.qqzhi.com%2Fupload%2Fimg_3_4271615870D2203553003_23.jpg&rpstart=0&rpnum=0&adpicid=0&force=undefined");
        user.setToken("OKYPyuQ2adPOIE5EFFlKQEz7nH0KG%2F6TRNMge9uu7Ew%3D");
        return user;
/*
        return FileCacheUtil.getCache(context,"USERDATA.txt",0, User.class);
*/
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