package com.yhh.androidutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by yuanhh1 on 2015/9/4.
 */
public class ImageUtils {

    private ImageUtils(){}

    /**
     * 将bitmap 转换为 byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte[]  bitmapToByte(Bitmap bitmap){
        if(bitmap ==null){
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 将byte数组 转换为 bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap byteToBitmap(byte[] b){
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * 将drawable 转换为 Bitmap
     *
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d){
        return d == null ? null : ((BitmapDrawable)d).getBitmap();
    }

    /**
     * 将 Bitmap 转换为 drawable
     *
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * 将 drawable 转换为 byte数组
     *
     * @param d
     * @return
     */
    public static byte[] drawableToByte(Drawable d){
        return bitmapToByte(drawableToBitmap(d));
    }

    /**
     * 将颜色 转换为 drawable
     *
     * @param color
     * @return
     */
    public static Drawable colorToDrawable(int color){
        return new ColorDrawable(color);
    }

    /**
     * 将byte数组 转换为 drawable
     *
     * @param b
     * @return
     */
    public static Drawable byteToDrawable(byte[] b){
        return bitmapToDrawable(byteToBitmap(b));
    }

    /**
     * 按指定的大小， 缩放图片
     *
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }


    /**
     * 按一定的横纵比，缩放图片
     *
     * @param org
     * @param scaleWidth sacle of width
     * @param scaleHeight scale of height
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }

    /**
     * 将bitmap 内容保持到文件中
     *
     * @param bitmap
     * @param filePath
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmap, String filePath) {
        return saveBitmap(bitmap, new File(filePath));
    }

    /**
     * 将bitmap 内容保持到文件中
     *
     * @param bitmap
     * @param file
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return false;
    }

    /**
     * 获取图片的大小
     *
     * @param bitmap
     * @return
     */
    public static int getImageSize(Bitmap bitmap){
        if(bitmap == null) return 0;

        int bitmapSize = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bitmapSize = bitmap.getByteCount();
        } else {
            bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
        }

        return  bitmapSize;
    }



}
