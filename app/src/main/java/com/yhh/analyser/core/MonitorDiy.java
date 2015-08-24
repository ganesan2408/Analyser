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
public class MonitorDiy extends Monitor {
    private InfoFactory mInfoFactory;

    private ArrayList<String> mContentList;

    private List<Boolean> mCheckedList;

    @Override
    public Integer[] getItems() {
        return MonitorChoice.getInstance().getSysItems();
    }

    public MonitorDiy(Context context) {
        super(context);

    }

    @Override
    public String getFileType() {
        return "";
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

        if(mCheckedList.get(MonitorConst.CPU_USED_RATIO)){
            mInfoFactory.getCpuInfo().updateAllCpu();
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