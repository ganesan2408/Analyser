package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.provider.MonitorShell;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorTop extends Monitor {
    private MonitorShell mShellMonitor;

    @Override
    public String getMonitorTitle() {
        return MonitorConst.TOP+"";
    }

    @Override
    public String monitor() {

        return mShellMonitor.execTop(8);
    }


    public MonitorTop(Context context){
        super(context);
        mShellMonitor = new MonitorShell(context);
    }

}
