package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.GpuInfo;
import com.yhh.analyser.bean.MemoryInfo;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.utils.ConstUtils;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorPerf extends Monitor {
    private MemoryInfo mMemoryInfo;
    private GpuInfo mGpuInfo;

    @Override
    public String getMonitorTitle() {
        return MonitorConst.GPU_USED_RATIO +","
                + MonitorConst.GPU_CLOCK +","
                + MonitorConst.MEM_FREE
                + ConstUtils.LINE_END;
    }

    public MonitorPerf(Context context) {
        super(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        mMemoryInfo = new MemoryInfo();
        mGpuInfo = new GpuInfo();


    }



    @Override
    public String monitor() {
        String gpuRate = String.valueOf(mGpuInfo.getGpuRate());
        String gpuClock = String.valueOf(mGpuInfo.getGpuClock());
        String freeMemory  = String.valueOf(mMemoryInfo.getFreeMemorySize(mContext)/1024);

        write2File(gpuRate, gpuClock, freeMemory);

        StringBuffer sb = new StringBuffer();
        sb.append(getItemName(MonitorConst.GPU_USED_RATIO)).append(":");
        sb.append(gpuRate);
        sb.append(getItemUnit(MonitorConst.GPU_USED_RATIO)).append("\n");

        sb.append(getItemName(MonitorConst.GPU_CLOCK)).append(":");
        sb.append(gpuClock);
        sb.append(getItemUnit(MonitorConst.GPU_CLOCK)).append("\n");

        sb.append(getItemName(MonitorConst.MEM_FREE)).append(":");
        sb.append(freeMemory);
        sb.append(getItemUnit(MonitorConst.MEM_FREE)).append("\n");


        return sb.toString();
    }




}
