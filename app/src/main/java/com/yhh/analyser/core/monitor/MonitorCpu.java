package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.bean.CpuInfo;
import com.yhh.analyser.core.MonitorFactory;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorCpu extends Monitor {
    private CpuInfo mCpuInfo;

    private ArrayList<String> mContentList;

    public MonitorCpu(Context context){
        super(context);
    }


    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorFactory.CPU_CLOCK
        };
    }

    @Override
    public String getFileType() {
        return "_CPU";
    }

    @Override
    public void onStart() {
        super.onStart();
        mCpuInfo = new CpuInfo();
        mContentList = new ArrayList<>();
    }

    @Override
    public String monitor() {

        mContentList.clear();
        mContentList.add(mCpuInfo.getCpuFreqList());

        write2File(mContentList);

        return getFloatBody(mContentList);
    }
}
