package com.yhh.androidutils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * String Utils
 *
 * @author by yuanhuihui   2015-09-01
 */
public class StringUtils {

    public static final String EMPTY = "";

    private StringUtils() {
        throw new RuntimeException("init");
    }

    /**
     * 字符串为NULL 或者 长度为0
     *
     * @param str
     *
     * @return
     */
    public static boolean isNull(String str) {
        return (str == null || str.length() == 0);
    }


    /**
     * 字符串为NULL 或者 是一串空格组成
     *
     * @param str
     *
     * @return
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * 比较两个字符串，判断是否相等
     *
     * @param actual
     * @param expected
     * @return
     */
    public static boolean isEquals(String actual, String expected){
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }

    /**
     * 获取字符序列的长度
     *
     * @param charSequence
     *
     * @return
     */
    public static int length(CharSequence charSequence){
        return charSequence == null ? 0 : charSequence.length();
    }

    /**
     * UTF-8格式编码
     *
     * @param str
     * @return
     */
    public static String encodeToUtf8(String str){
        if(isNull(str) || str.length() == str.getBytes().length){
            return str;
        }

        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    /**
     * UTF-8格式编码，发生异常时返回默认值
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static String encodeToUtf8(String str, String defaultValue){
        if(isNull(str) || str.length() == str.getBytes().length){
            return str;
        }

        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return defaultValue;
        }
    }

    public static boolean match(char value, char keyword) {
        return value == keyword || value - keyword == 32;
    }
}
