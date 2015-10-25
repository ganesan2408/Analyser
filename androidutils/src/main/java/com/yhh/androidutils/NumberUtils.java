package com.yhh.androidutils;

import java.text.DecimalFormat;

/**
 * String Utils
 *
 * @author by yuanhuihui   2015-09-01
 */
public class NumberUtils {

    private NumberUtils() {
        throw new RuntimeException("init");
    }


    /**
     * 将字符串解析为int类型
     *
     * @param str
     * @return
     */
    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("NumberFormatException", e);
        }
    }

    /**
     * 将字符串解析为int类型，当出现异常时返回默认值
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串解析为long类型
     *
     * @param str
     * @return
     */
    public static long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("NumberFormatException", e);
        }
    }

    /**
     * 将字符串解析为long类型，当出现异常时返回默认值
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static long parseLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串解析为double类型
     *
     * @param str
     * @return
     */
    public static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("NumberFormatException", e);
        }
    }

    /**
     * 将字符串解析为长整型，当出现异常时返回默认值
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static double parseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 将数字截止小数点后若干位
     *
     * @param number 待截取的数
     * @param digits 需要保留小数点后的位数
     * @return 返回截取后的数值
     */
    public static <T> String format(T number, int digits) {

        return format(number, digits, false);
    }

    /**
     * 将数字截止小数点后若干位
     *
     * @param number
     * @param digits
     * @param isPercent
     * @param <T>
     * @return
     */
    public static <T> String format(T number, int digits, boolean isPercent) {

        StringBuffer a = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                a.append(".");
            a.append("0");
        }

        if (isPercent)
            a.append("%");

        DecimalFormat nf = new DecimalFormat("###,###,###,##0" + a.toString());
        String formatted = nf.format(number);

        return formatted;
    }
}
