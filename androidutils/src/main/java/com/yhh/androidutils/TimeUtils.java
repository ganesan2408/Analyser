package com.yhh.androidutils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuanhh1 on 2015/9/4.
 */
public class TimeUtils {

    /** 日期时间格式 */
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 日期格式 */
    public static String DATE_FORMAT = "yyyy-MM-dd";

    /** 日期和时间，采用下划线的格式 */
    public static String DATETIME_UNDERLINE_FORMAT = "yyyyMMdd_HHmmss";

    /** 日期和时间，采用下划线的格式 */
    public static String DATA_MS_FORMAT = "HH:mm:ss.SSS";


    private TimeUtils(){    }

    /**
     * 获取当前时间，并格式化输出
     *
     * @param dateFormat
     * @return
     */
    public static String getCurrentTime(String dateFormat){
        return getFormatTime(System.currentTimeMillis(),dateFormat);
    }

    /**
     * 获取格式化的时间
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getFormatTime(long timeInMillis, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date(timeInMillis));
    }
}
