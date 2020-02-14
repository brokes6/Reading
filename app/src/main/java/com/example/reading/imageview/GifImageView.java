package com.example.reading.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.reading.R;
import com.example.reading.application.AppApplication;


public class GifImageView extends ImageView {
    private boolean isGif = false;
    private static Bitmap gifbmp = null;
    private static Paint paint;

    static {
        gifbmp = BitmapFactory.decodeResource(AppApplication.getContext().getResources(), R.drawable.gif_gaitubao_27x27);
        paint = new Paint();
        paint.setColor(Color.parseColor("#469de6"));
        paint.setStyle(Paint.Style.FILL);
    }
    public GifImageView(Context context) {
        super(context);
    }

    public GifImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GifImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GifImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(canvas.getWidth() - gifbmp.getWidth() - 18, canvas.getHeight() - gifbmp.getHeight(), canvas.getWidth(), canvas.getHeight(), paint);
        canvas.drawBitmap(gifbmp, canvas.getWidth() - gifbmp.getWidth() - 9, canvas.getHeight() - gifbmp.getHeight(), null);
    }
}
