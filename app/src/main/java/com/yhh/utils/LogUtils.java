/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.utils;

import java.io.File;
import java.util.regex.Pattern;

public class LogUtils {
    public static final String LOG_ALARM = "ALARM";
    public static final String LOG_BATTERY = "battery";
    public static final String LOG_BLUETOOTH = "bluetooth"; // filter out
    public static final String LOG_DMESGlog = "dmesglog";
    public static final String LOG_INTERRUPTES = "interrupts.txt";
    public static final String LOG_LOGCAT = "logcat";
    public static final String LOG_PMLOG = "pmlog";
    public static final String LOG_RADIO = "radio";
    public static final String LOG_SLEEP = "sleeplog";
    public static final String LOG_SMD = "smd";
    public static final String LOG_WAKEUP = "wklog";
    public static final String LOG_WLAN = "wlan"; // filter out
    
    public static String getNewestLog(String dir,String prex){
        String path =null;
        if(prex == LOG_PMLOG){
            path = dir +"/"+prex;
        }else{
            path = getLasterLog(dir, prex);
        }
        return path;
    }
    
    private static String getLasterLog(String dir, String prex){
        boolean haveFound =false;
        String regex = "^"+prex+"\\w*\\d{8}";
        Pattern p = Pattern.compile(regex);
        String newestPath =prex;
        File logDir = new File(dir);  
        if (logDir.exists()) {  
           String tempPath = null;
           File[] files = logDir.listFiles();
           for(File f:files){
               tempPath = f.getName();
               if(p.matcher(tempPath).find() && tempPath.compareTo(newestPath)>0){
                   newestPath = tempPath;
                   haveFound = true;
               }
           }
        }
        if(haveFound){
            return dir+"/"+newestPath;
        }else{
            return null;
        }
    }
    
    public static String getDateNewestLog(String dir){
        File logDir = new File(dir);  
        String newsetPath ="";
        if (logDir.exists()) {  
           String tempPath = null;
           File[] files = logDir.listFiles();
           for(File f:files){
               tempPath = f.getName();
               if(tempPath.compareTo(newsetPath)>0){
                   newsetPath = tempPath;
               }
           }
        }
        return newsetPath;
    }
}
