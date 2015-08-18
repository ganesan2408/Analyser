/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

public class StringUtils {
    
    public static boolean isPositive(String text) {
        Double num;
        try {
            num = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return false;
        }
        return num >= 0;
    }
    
    public static boolean match(char value, char keyword) {
        if(value == keyword || value - keyword == 32){
            return true;
        }
        return false;
    }

    /**
     * 字符串为NULL 或者 长度为0
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 字符串为NULL 或者 是一串空格组成
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

}
