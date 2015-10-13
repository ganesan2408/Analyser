package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.MonitorChoice;
import com.yhh.analyser.core.monitor.Monitor;
import com.yhh.analyser.core.monitor.MonitorBattery;
import com.yhh.analyser.core.monitor.MonitorCpu;
import com.yhh.analyser.core.monitor.MonitorDiy;
import com.yhh.analyser.core.monitor.MonitorPerf;
import com.yhh.analyser.core.monitor.MonitorTop;

/**
 * Created by yuanhh1 on 2015/8/20.
 */
public class MonitorFactory {
    public static final int APP_CPU_USED_RATIO = 0;
    public static final int APP_MEM_USED = 1;
    public static final int CPU_USED_RATIO = 2;
    public static final int CPU_CLOCK = 3;
    public static final int GPU_USED_RATIO = 4;
    public static final int GPU_CLOCK = 5;
    public static final int MEM_FREE = 6;
    public static final int POWER_CURRENT = 7;
    public static final int SCREEN_BRIGHTNESS = 8;
    public static final int BATTERY_LEVEL = 9;
    public static final int BATTERY_TEMP = 10;
    public static final int BATTERY_VOLT = 11;
    public static final int TRAFFIC_SPEED = 12;

    /** 性能 */
    public static final int TYPE_PERF = 0;
    /** 电池 */
    public static final int TYPE_BATTERY = 1;
    /** 全监控 */
    public static final int TYPE_ALL = 2;
    /** CPU频率 */
    public static final int TYPE_CPU = 3;
    /** Top */
    public static final int TYPE_TOP = 4;
    /** App */
    public static final int TYPE_APP_MONITOR = 5;
    /** 异常 */
    public static final int TYPE_EXCEPTION = 6;
    /** 可选 */
    public static final int TYPE_DIY = 7;
    /** 高级 */
    public static final int TYPE_SHELL = 8;

    /** APP 监控 */
    public static final int TYPE_APP = 11;
    /** APP 可选监控*/
    public static final int TYPE_APP_DIY = 12;


    public static Monitor newInstance(Context context, int type){
        switch (type){
            case TYPE_PERF:   //性能监控
                return new MonitorPerf(context);

            case TYPE_BATTERY:  //电池监控
                return new MonitorBattery(context);

            case TYPE_CPU:     //CPU监控
                return new MonitorCpu(context);

            case TYPE_TOP:    //Top监控
                return new MonitorTop(context);

            case TYPE_ALL:    //全监控
                MonitorChoice.getInstance().setAllChecked(true);
                return new MonitorDiy(context);

            case TYPE_DIY:    //可选监控
                return new MonitorDiy(context);

//            case  TYPE_EXCEPTION: //异常监控
//                return new MonitorException1(context);

//            case TYPE_SHELL: //高级监控
//                return new MonitorShell(context);

            default:
                return new MonitorTop(context);
        }
    }
}
