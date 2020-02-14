package com.example.reading.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class HandlerUtil {
    public static final int SEND_TOAST=1;
    private Context context;
    public HandlerUtil(Context context){
        this.context=context;
    }
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_TOAST:
                    Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public void sendToast(String msg){
        Message message = new Message();
        message.what=SEND_TOAST;
        message.obj=msg;
        handler.sendMessage(message);
    }
}
