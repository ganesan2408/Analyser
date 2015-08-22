package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.provider.MonitorShell;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorTop extends Monitor {
    private MonitorShell mShellMonitor;


    public MonitorTop(Context context){
        super(context);

    }

    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorConst.TOP
        };
    }

    @Override
    public String getFileType() {
        return "_Top";
    }

    @Override
    public void onStart() {
        super.onStart();
        mShellMonitor = new MonitorShell(mContext);
    }

    @Override
    public String monitor() {

        return mShellMonitor.execTop(8);
    }



}
