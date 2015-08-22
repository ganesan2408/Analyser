package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.CpuInfo;
import com.yhh.analyser.bean.MemoryInfo;
import com.yhh.analyser.config.MonitorConst;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorApp extends Monitor {
    private CpuInfo mCpuInfo;
    private MemoryInfo mMemoryInfo;
    private int pid;

    private ArrayList<String> mContentList;



    public MonitorApp(Context context, int pid){
        super(context);
        this.pid = pid;
    }

    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorConst.APP_CPU_USED_RATIO,
                MonitorConst.APP_MEM_USED
        };
    }

    @Override
    public String getFileType() {
        return "_App";
    }

    @Override
    public void onStart() {
        super.onStart();
        mCpuInfo = new CpuInfo();
        mMemoryInfo = new MemoryInfo();
        mContentList = new ArrayList<>();
    }

    @Override
    public String monitor() {
        mCpuInfo.updateCpu(pid);

        mContentList.clear();
        mContentList.add(mCpuInfo.getProcessCpuRatio(pid));
        mContentList.add( mMemoryInfo.getPidMemorySize(pid, mContext)/1024 +"");

        write2File(mContentList);

        return getFloatBody(mContentList);
    }
}
