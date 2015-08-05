/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeUtils {
    public static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
    
    public static String getCurrentTime2(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
    
    
    public static String seconds2Hhmmss(float seconds){
        StringBuilder hhmmss = new StringBuilder();
        int ss = (int)seconds;
        hhmmss.append(seconds2Hhmmss(ss));
        int ms = (int) ((seconds - ss)*1000);
        if(ms>0){
            hhmmss.append(ms+"ms");
        }
        return hhmmss.toString();
    }
    
    private static int[] secends2hms(int seconds){
        int[] t = new int[3];
        t[0] = seconds/3600;
        seconds = seconds%3600;
        t[1] = seconds/60;
        t[2] = seconds%60;
        return t;
    }
    
    public static String seconds2Hhmmss(int seconds){
        StringBuilder hhmmss = new StringBuilder();
        int hh,mm,ss;
        hh = seconds/3600;
        seconds = seconds%3600;
        mm = seconds/60;
        ss = seconds%60;
        if(hh>0){
            hhmmss.append(hh+"h");
        }
        
        if(mm>0){
            hhmmss.append(mm+"m");
        }
        
        if(ss>0){
            hhmmss.append(ss+"s");
        }
        
        return hhmmss.toString();
    }
}
