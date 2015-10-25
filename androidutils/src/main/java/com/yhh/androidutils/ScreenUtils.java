package com.yhh.androidutils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by yuanhh1 on 2015/9/4.
 */
public class ScreenUtils {

    /**
     * px 转换为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp 转换为 px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp 转换为 px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * 获取屏幕相关的信息
     */
    public static String getScreenInfo(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        StringBuilder sb = new StringBuilder();
        sb.append("屏幕信息:  ").append("\n");
        sb.append("density         :").append(dm.density).append("\n");
        sb.append("densityDpi      :").append(dm.densityDpi).append("\n");
        sb.append("scaledDensity   :").append(dm.scaledDensity).append("\n");
        sb.append("heightPixels    :").append(dm.heightPixels).append("\n");
        sb.append("widthPixels     :").append(dm.widthPixels).append("\n");
        sb.append("xdpi            :").append(dm.xdpi).append("\n");
        sb.append("ydpi            :").append(dm.ydpi).append("\n");

        double width = Math.pow(dm.xdpi / dm.xdpi, 2);
        double height = Math.pow(dm.ydpi / dm.ydpi, 2);
        double screenInches = Math.sqrt(width + height);
        sb.append("屏幕尺寸            :").append(screenInches).append("\n");

        return sb.toString();
    }
}
