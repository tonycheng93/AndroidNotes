package com.sky.androidnotes.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by tonycheng on 2017/9/30.
 */

public class BitmapUtils {

    public static Bitmap generateBitmapByColor(Bitmap bitmap, int color) {
        float red = Color.red(color) / 255f;
        float blue = Color.blue(color) / 255f;
        float green = Color.green(color) / 255f;
        Bitmap after = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(after);//将画纸固定在画布上
        Paint paint = new Paint();//实例画笔对象
        float[] colorArray = new float[]{
                red, 0, 0, 0, 0,
                0, green, 0, 0, 0,
                0, 0, blue, 0, 0,
                0, 0, 0, 1, 0
        };
        ColorMatrix colorMatrix = new ColorMatrix(colorArray);//将保存的颜色矩阵的数组作为参数传入
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);//再把该colorMatrix作为参数传入来实例化ColorMatrixColorFilter
        paint.setColorFilter(colorFilter);//并把该过滤器设置给画笔
        canvas.drawBitmap(bitmap, new Matrix(), paint);//传如baseBitmap表示按照原图样式开始绘制，将得到是复制后的图片
        return after;
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap) {
        Bitmap output;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float r = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap compressBitmap(String imgUrl, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgUrl, options);
        options.inSampleSize = calculateInSimpleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap temp = BitmapFactory.decodeFile(imgUrl, options);
        return null;
    }

    private static int calculateInSimpleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSimpleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            if (width > height) {
                inSimpleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSimpleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSimpleSize;
    }

    /**
     * transform Drawable to Bitmap
     *
     * @param drawable {@link Drawable}
     * @return {@link Bitmap}
     */
    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("drawable can not ");
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = (drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
