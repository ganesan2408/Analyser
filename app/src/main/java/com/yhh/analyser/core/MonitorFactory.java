package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.MonitorChoice;

/**
 * Created by yuanhh1 on 2015/8/20.
 */
public class MonitorFactory {
    public static Monitor newInstance(Context context, int type){
        switch (type){
            case 0:
                return new MonitorPerf(context);

            case 1:
                return new MonitorBattery(context);

            case 2:
                return new MonitorCpu(context);

            case 3:
                return new MonitorTop(context);

            case 4:
                MonitorChoice.getInstance().setAllChecked(true);
                return new MonitorDiy(context);

            case 5:
                return new MonitorDiy(context);

            default:
                return new MonitorTop(context);
        }
    }
}
