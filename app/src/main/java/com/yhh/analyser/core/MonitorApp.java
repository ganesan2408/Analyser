package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.CpuInfo;
import com.yhh.analyser.bean.MemoryInfo;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.utils.ConstUtils;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorApp extends Monitor {
    private CpuInfo mCpuInfo;
    private MemoryInfo mMemoryInfo;
    private int pid;

    @Override
    public String getMonitorTitle() {
        return MonitorConst.APP_CPU_USED_RATIO +"," + MonitorConst.APP_MEM_USED + ConstUtils.LINE_END;
    }

    @Override
    public String monitor() {
        mCpuInfo.updateCpu(pid);

        String appCpu = mCpuInfo.getProcessCpuRatio(pid);
        String appMem = mMemoryInfo.getPidMemorySize(pid, mContext)/1024 +"";
        write2File(appCpu, appMem);

        StringBuffer sb = new StringBuffer();
        sb.append(getItemName(MonitorConst.APP_CPU_USED_RATIO)).append(": ");
        sb.append(appCpu);
        sb.append(getItemUnit(MonitorConst.APP_CPU_USED_RATIO)).append("\n");
        sb.append(getItemName(MonitorConst.APP_MEM_USED)).append(": ");
        sb.append(appMem);
        sb.append(getItemUnit(MonitorConst.APP_MEM_USED)).append("\n");
        return sb.toString();
    }


    public MonitorApp(Context context, int pid){
        super(context);
        mCpuInfo = new CpuInfo();
        mMemoryInfo = new MemoryInfo();
        this.pid = pid;
    }
}
