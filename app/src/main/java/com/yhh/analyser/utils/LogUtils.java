/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import com.yhh.analyser.provider.LogcatParser;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LogUtils {
    public static final String DEBUG_TAG = "sa_";
    public static final String LINE_END = "\n";
    public static final String MY_PACKAGE_NAME = "com.yhh.analyser";
    
    public static final String CPU_FREQ_TITLE = "CPU频率";
    
    /** log name const */
    public static String LOG_DIR;
    public static String[] LOG_ALL;
    

    public static final String LOG_DMESGlog = "dmesglog";
    public static final String LOG_INTERRUPTES = "interrupts.txt";
    

    public static final String LOG_RADIO = "radio";
    
    public static final String BRAND_LENOVO = "Lenovo";
    public static final String BRAND_HUAWEI = "Huawei";
    
    /** string const */
    public static final String STR_VERSION_SIGN = "internal_framework";

    /** search const */
    public static final String[] CommandArray = new String[] { "ls ", "cat ",
            "echo ", "mkdir ", "rm ", "touch ", "rmdir ", "mv ", "cp ",
            "locate ", "whereis ", "which ", "top ", "free ", "history ",
            "chmod ", "dumpsys ", "find ", "grep ", "getprop ",
            "mount -o rw,remount /system" };

    /** search const */
    public static final String[] SubCommand = new String[] { "SurfaceFlinger",
            "country_detector", "cpuinfo", "dbinfo", "device_policy",
            "devicestoragemonitor", "diskstats", "drm.drmManager", "dropbox",
            "entropy", "fm", "gfxinfo", "hardware", "input", "isms",
            "location", "lock_settings", "meminfo", "network_management",
            "notification", "package", "permission", "phone", "power",
            "samplingprofiler", "scheduling_policy", "search", "sensorservice",
            "serial", "servicediscovery", "sim_manager", "simphonebook" };

    public static HashMap<String, String[]> KEY_TABLE = new HashMap<String, String[]>() {
    };

    static {

        LogUtils.KEY_TABLE.put(LOG_DMESGlog, new String[] {
                "PM: suspend exit", "Enabled clock count", "qpnp_kpdpwr_irq",
                "failed to suspend", "fatal", "health", "battery", "Wakeups",
                "Alarm stats", "brightness", "hall-switch", "key_code", "lux",
                "sensor", "firmware","avc:denied" });

        LogUtils.KEY_TABLE.put(LogcatParser.LOG_LOGCAT, new String[] {
                "AlarmManagerService", "ActivityManager: START", "error",
                "exception", "DisplayPowerC", "SensorManager","Caused by: ","FATAL EXCEPTION: " });

        LogUtils.KEY_TABLE.put(LOG_RADIO, new String[] {
                        "< UNSOL_DATA_CALL_LIST_CHANGED",
                        "< DATA_REGISTRATION_STATE" });
        
    }

    public static String getNewestLog(String dir, String prex){

        if(prex == "pmlog"){
            return dir +"/"+prex;
        }

        boolean haveFound =false;
        String regex = "^"+prex+"\\w*\\d{8}";
        Pattern p = Pattern.compile(regex);
        String newestPath =prex;
        File logDir = new File(dir);
        if (logDir.exists()) {
            String tempPath;
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
        String newestPath ="";
        if (logDir.exists()) {
            String tempPath;
            File[] files = logDir.listFiles();
            for(File f:files){
                tempPath = f.getName();
                if(tempPath.compareTo(newestPath)>0){
                    newestPath = tempPath;
                }
            }
        }
        return newestPath;
    }
}
