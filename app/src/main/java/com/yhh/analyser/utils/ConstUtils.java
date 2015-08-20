/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import com.yhh.analyser.bean.app.PhoneInfo;

import java.util.HashMap;

public class ConstUtils {
    public static final String DEBUG_TAG = "sa_";
    public static final String LINE_END = "\n";
    public static final String MY_PACKAGE_NAME = "com.yhh.analyser";
    
    public static final String CPU_FREQ_TITLE = "CPU频率";
    
    public static final String[] AUTO_CASES= new String[]{};
    
    /** log name const */
    public static String LOG_DIR;
    public static String LOG_LOGCAT;
    public static String[] LOG_ALL;
    
    public static final String LOG_ALARM = "ALARM";
    public static final String LOG_BATTERY = "battery";
    public static final String LOG_BLUETOOTH = "bluetooth"; // filter out
    public static final String LOG_DMESGlog = "dmesglog";
    public static final String LOG_INTERRUPTES = "interrupts.txt";
    
    public static final String LOG_PMLOG = "pmlog";
    public static final String LOG_RADIO = "radio";
    public static final String LOG_SLEEP = "sleep";
    public static final String LOG_SMD = "smd";
    public static final String LOG_WAKEUP = "wklog";
    public static final String LOG_WLAN = "wlan"; // filter out
    
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
       //兼容老版本的log机制
        if(PhoneInfo.getPhoneType().contains(PhoneInfo.ZOOM) ||
                PhoneInfo.getPhoneType().contains(PhoneInfo.S7)){
            LOG_DIR = "/data/local/log/aplog";
            LOG_LOGCAT = "logcat";
        }else{
            LOG_DIR ="/data/local/log/curlog";
            LOG_LOGCAT = "mainlog";
        }
        
        LOG_ALL = new String[] { LOG_ALARM, LOG_INTERRUPTES,
                LOG_BATTERY, LOG_DMESGlog, LOG_LOGCAT, LOG_PMLOG,
                LOG_RADIO, LOG_SLEEP, LOG_SMD, LOG_WAKEUP };
        
        ConstUtils.KEY_TABLE.put(LOG_DMESGlog, new String[] {
                "PM: suspend exit", "Enabled clock count", "qpnp_kpdpwr_irq",
                "failed to suspend", "fatal", "health", "battery", "Wakeups",
                "Alarm stats", "brightness", "hall-switch", "key_code", "lux",
                "sensor", "firmware","avc:denied" });

        ConstUtils.KEY_TABLE.put(LOG_LOGCAT, new String[] {
                "AlarmManagerService", "ActivityManager: START", "error",
                "exception", "DisplayPowerC", "SensorManager","Caused by: ","FATAL EXCEPTION: " });

        ConstUtils.KEY_TABLE.put(LOG_RADIO, new String[] {
                        "< UNSOL_DATA_CALL_LIST_CHANGED",
                        "< DATA_REGISTRATION_STATE" });
        
    }
}
