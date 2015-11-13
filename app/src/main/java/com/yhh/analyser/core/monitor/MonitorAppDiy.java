package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.model.InfoFactory;
import com.yhh.analyser.model.MonitorChoice;
import com.yhh.analyser.core.MonitorFactory;

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
        write2File(mContentList);
        return getFloatBody(mContentList);
    }


    private void getMonitors(){

        mContentList.clear();

        /**更新CPU节点值 */
        if(mCheckedList.get(MonitorFactory.APP_CPU_USED_RATIO)){
            mInfoFactory.getCpuInfo().updateCpu(pid);

            if(mCheckedList.get(MonitorFactory.CPU_USED_RATIO)){
                mContentList.add(mInfoFactory.getCpuPidUsedRatio(pid));
                mContentList.add(mInfoFactory.getCpuRatioList().get(0));
            }else{
                mContentList.add(mInfoFactory.getCpuPidUsedRatioComplete(pid));
            }

        }else if(mCheckedList.get(MonitorFactory.CPU_USED_RATIO)){
            mInfoFactory.getCpuInfo().updateAllCpu();
            mContentList.add(mInfoFactory.getCpuRatioList().get(0));
        }

        if(mCheckedList.get(MonitorFactory.APP_MEM_USED)){
            mContentList.add(mInfoFactory.getMemoryPidUsedSize(pid, mContext));
        }

        if(mCheckedList.get(MonitorFactory.CPU_CLOCK)){
            mContentList.add(mInfoFactory.getCpuFreqList());
        }

        if(mCheckedList.get(MonitorFactory.GPU_USED_RATIO)){
            mContentList.add(mInfoFactory.getGpuRate());
        }
        if(mCheckedList.get(MonitorFactory.GPU_CLOCK)){
            mContentList.add(mInfoFactory.getGpuClock());
        }
        if(mCheckedList.get(MonitorFactory.MEM_FREE)){
            mContentList.add(mInfoFactory.getMemoryUnusedSize(mContext));
        }
        if(mCheckedList.get(MonitorFactory.POWER_CURRENT)){
            mContentList.add(mInfoFactory.getPowerCurrent());
        }

        if(mCheckedList.get(MonitorFactory.SCREEN_BRIGHTNESS)){
            mContentList.add(mInfoFactory.getScreenBrightness());
        }
        if(mCheckedList.get(MonitorFactory.BATTERY_LEVEL)){
            mContentList.add(mInfoFactory.getBatteryLevel());
        }
        if(mCheckedList.get(MonitorFactory.BATTERY_TEMP)){
            mContentList.add(mInfoFactory.getBatteryTemperature());
        }
        if(mCheckedList.get(MonitorFactory.BATTERY_VOLT)){
            mContentList.add(mInfoFactory.getBatteryVoltage());
        }
        if(mCheckedList.get(MonitorFactory.TRAFFIC_SPEED)){
            mContentList.add(mInfoFactory.getTrafficRevSpeed()
                    + "/" + mInfoFactory.getTrafficSendSpeed());
        }

    }


}
