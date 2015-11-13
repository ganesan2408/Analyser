package com.yhh.analyser.config;

import android.os.Environment;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AppConfig {

    /**analyser的文件夹根路径 */
    public final static String ROOT_DIR = Environment.getExternalStorageDirectory().getPath()
            + "/systemAnalyzer/";

    /** log文件夹*/
    public static final String PATH_SD_LOG = Environment.getExternalStorageDirectory().getPath()
            + "/Log";

    /**监控的文件夹 */
    public final static String MONITOR_DIR = ROOT_DIR + "monitor/";
    /**截图的文件夹 */
    public final static String SCREEN_SHOT_DIR = ROOT_DIR + "screenshot/";
    /**adb指令的文件名 */
    public final static String ADB_SHELL_FILE = ROOT_DIR + "adb.txt";
    /** 白名单文件*/
    public static final String ONE_KEY_WHITE_LIST =  ROOT_DIR + "whitelist.config";
    public static final String BENCHMACH_FILE =  ROOT_DIR + "benchmark/";

    /** 临时用*/
    public static int TYPE;

}
