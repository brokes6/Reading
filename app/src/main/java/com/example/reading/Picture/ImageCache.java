package com.example.reading.Picture;

import android.graphics.Bitmap;
import android.util.LruCache;


import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * 创建于2019/11/13 9:02🐎
 */
public class ImageCache extends LruCache<String,Bitmap> {

    private Map<String, SoftReference<Bitmap>> cacheMap;

    public ImageCache(Map<String, SoftReference<Bitmap>> cacheMap) {
        super((int) (Runtime.getRuntime().maxMemory() / 8));
        this.cacheMap = cacheMap;
    }

    @Override // 获取图片大小
    public int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    // 当有图片从LruCache中移除时，将其放进软引用集合中
    @Override
    public void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        if (oldValue != null) {
            SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(oldValue);
            cacheMap.put(key, softReference);
        }
    }

    public Map<String, SoftReference<Bitmap>> getCacheMap() {
        return cacheMap;
    }
}
