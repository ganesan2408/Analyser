package com.yhh.analyser.core;

import android.content.Context;

/**
 * Created by yuanhh1 on 2015/8/20.
 */
public class MonitorFactory {
    public static Monitor newInstance(Context context, int type){
        switch (type){
            case 0:
                return new MonitorCpu(context);

            case 1:
                return new MonitorPerf(context);

            case 2:
                return new MonitorBattery(context);

            case 3:
                return new MonitorTop(context);

        }
        return new MonitorCpu(context);
    }
}
