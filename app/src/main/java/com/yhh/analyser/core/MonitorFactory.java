package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.MonitorChoice;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.provider.MonitorException;
import com.yhh.analyser.provider.MonitorShell;

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
            case MonitorConst.MONITOR_DIY:
                return new MonitorDiy(context);

            case MonitorConst.MONITOR_SHELL:
                return new MonitorShell(context);

            case  MonitorConst.MONITOR_EXCEPTION:
                return new MonitorException(context);

            default:
                return new MonitorTop(context);
        }
    }
}
