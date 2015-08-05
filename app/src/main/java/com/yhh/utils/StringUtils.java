/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.utils;

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
}
