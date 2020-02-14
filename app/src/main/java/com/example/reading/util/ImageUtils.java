package com.example.reading.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class ImageUtils {
    public static final int NO=-1;
    public static final int JPEG=1;
    public static final int GIF=2;
    public static final int PNG=3;
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
    public static int handlerImagePath(String path){
        String suffix=path.substring(path.lastIndexOf(".")+1);
        if(suffix.equalsIgnoreCase("JPEG")||suffix.equalsIgnoreCase("JPG")){
            return JPEG;
        }else if(suffix.equalsIgnoreCase("gif")){
            return GIF;
        }else if(suffix.equalsIgnoreCase("png")){
            return PNG;
        }
            else {
            return NO;
        }
    }
}
