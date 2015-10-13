package com.yhh.analyser.config;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AppConfig {

    /**analyser的文件夹根路径 */
    public final static String PARENT_DIR = "/sdcard/systemAnalyzer/";

    /**监控文件夹的所在路径 */
    public final static String MONITOR_DIR = PARENT_DIR + "monitor/";

    /**截图文件夹的所在路径 */
    public final static String SCREEN_SHOT_DIR = PARENT_DIR + "screenshot/";

    /**adb监控指令的文件名路径 */
    public final static String ADB_SHELL_FILE = PARENT_DIR + "adb.txt";

    public static final String ONE_KEY_WHITE_LIST =  PARENT_DIR + "whitelist.config";

    /** 监控频率，单位（ms）*/
    public static int MONITOR_DELAY_TIME = 2000;


    /** 临时用*/
    public static int TYPE;
}
