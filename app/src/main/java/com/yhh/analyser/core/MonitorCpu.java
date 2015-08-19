package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.CpuInfo;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.utils.DebugLog;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorCpu extends Monitor {
    private CpuInfo mCpuInfo;
    private int pid;

    @Override
    public String getMonitorTitle() {
        return MonitorConst.CPU_USED_RATIO +"," + MonitorConst.CPU_CLOCK;
    }

    @Override
    public String monitor() {
        mCpuInfo.updateAllCpu();

        String cpuUsed = getCpuTotalUsedRatio().get(0);
        String cpuFreq = getCpuFreqList();
        DebugLog.d("cpuUsed="+cpuUsed);
        DebugLog.d("cpuFreq="+cpuFreq);
        write2File(cpuUsed, cpuFreq);

        StringBuffer sb = new StringBuffer();
        sb.append(getItemName(MonitorConst.CPU_USED_RATIO)).append(":");
        sb.append(cpuUsed);
        sb.append(getItemUnit(MonitorConst.CPU_USED_RATIO)).append("\n");
        sb.append(getItemName(MonitorConst.CPU_CLOCK)).append(":");
        sb.append(cpuFreq);
        sb.append(getItemUnit(MonitorConst.CPU_CLOCK)).append("\n");
        return sb.toString();
    }


    public MonitorCpu(Context context){
        super(context);
        mCpuInfo = new CpuInfo();
    }


    public MonitorCpu(Context context, int pid){
        this(context);
        this.pid = pid;
    }

    /**
     * 获取指定pid进程的CPU使用率
     *
     * @return
     */
    public String getAppUsedRatio(){
        return mCpuInfo.getProcessCpuRatio(pid);
    }

    /**
     * 获取所有CPU的使用率
     *
     * 0: total cpu
     * 1-n: cpu 0~n对应的CPU使用率
     *
     * @return
     */
    public ArrayList<String> getCpuTotalUsedRatio(){
        return mCpuInfo.getRatioList();
    }

    /**
     * 获取所有CPU的频率
     *
     * @return
     */
    public String getCpuFreqList(){
        return mCpuInfo.getCpuFreqList();
    }

//    /**
//     * 获取所有CPU的使用率,并格式化为String
//     *
//     * @return
//     */
//    public String getCpuUsedRatioBySeperate(){
//        ArrayList<String> cpuRatios = mCpuInfo.getTotalCpuRatio();
//        StringBuffer cpuRatioArray = new StringBuffer();
//        for(int i=1; i<cpuRatios.size();i++){
//            cpuRatioArray.append(cpuRatios.get(i) + "/");
//        }
//        for (int i = 0; i < mCpuInfo.getCpuNum() - cpuRatios.size() + 1; i++) {
//            cpuRatioArray.append("0.00,");
//        }
//        return cpuRatioArray.toString();
//    }
}
