package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.InfoFactory;
import com.yhh.analyser.bean.MonitorChoice;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorAppDiy extends Monitor {
    private InfoFactory mInfoFactory;

    private ArrayList<String> mContentList;

    private List<Boolean> mCheckedList;

    private int pid;

    @Override
    public Integer[] getItems() {
        return MonitorChoice.getInstance().getAppItems();
    }

    public MonitorAppDiy(Context context, int pid) {
        super(context);
        this.pid = pid;
    }

    @Override
    public String getFileType() {
        return "_App2";
    }

    @Override
    public void onStart() {
        super.onStart();
        mContentList = new ArrayList<>();

        mCheckedList = MonitorChoice.getInstance().getCheckedList();
        mInfoFactory = InfoFactory.getInstance();
        mInfoFactory.init(mContext);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInfoFactory.destory();
    }

    @Override
    public String monitor() {
        getMonitors();
        DebugLog.i("mContentList size= "+mContentList.size());
        write2File(mContentList);
        return getFloatBody(mContentList);
    }


    private void getMonitors(){

        mContentList.clear();
        if(mCheckedList.get(MonitorConst.APP_CPU_USED_RATIO)){
            mInfoFactory.getCpuInfo().updateCpu(pid);
        }else if(mCheckedList.get(MonitorConst.CPU_USED_RATIO)){
            mInfoFactory.getCpuInfo().updateAllCpu();
        }

        if(mCheckedList.get(MonitorConst.APP_CPU_USED_RATIO)){
            mContentList.add(mInfoFactory.getCpuPidUsedRatio(pid));
        }
        if(mCheckedList.get(MonitorConst.APP_MEM_USED)){
            mContentList.add(mInfoFactory.getMemoryPidUsedSize(pid, mContext));
        }

        if(mCheckedList.get(MonitorConst.CPU_USED_RATIO)){
            mContentList.add(mInfoFactory.getCpuTotalUsedRatio().get(0));
        }
        if(mCheckedList.get(MonitorConst.CPU_CLOCK)){
            mContentList.add(mInfoFactory.getCpuFreqList());
        }

        if(mCheckedList.get(MonitorConst.GPU_USED_RATIO)){
            mContentList.add(mInfoFactory.getGpuRate());
        }
        if(mCheckedList.get(MonitorConst.GPU_CLOCK)){
            mContentList.add(mInfoFactory.getGpuClock());
        }
        if(mCheckedList.get(MonitorConst.MEM_FREE)){
            mContentList.add(mInfoFactory.getMemoryUnusedSize(mContext));
        }
        if(mCheckedList.get(MonitorConst.POWER_CURRENT)){
            mContentList.add(mInfoFactory.getPowerCurrent());
        }

        if(mCheckedList.get(MonitorConst.SCREEN_BRIGHTNESS)){
            mContentList.add(mInfoFactory.getScreenBrightness());
        }
        if(mCheckedList.get(MonitorConst.BATTERY_LEVEL)){
            mContentList.add(mInfoFactory.getBatteryLevel());
        }
        if(mCheckedList.get(MonitorConst.BATTERY_TEMP)){
            mContentList.add(mInfoFactory.getBatteryTemperature());
        }
        if(mCheckedList.get(MonitorConst.BATTERY_VOLT)){
            mContentList.add(mInfoFactory.getBatteryVoltage());
        }
        if(mCheckedList.get(MonitorConst.TRAFFIC_SPEED)){
            mContentList.add(mInfoFactory.getTrafficRevSpeed()
                    + "/" + mInfoFactory.getTrafficSendSpeed());
        }

    }


}
