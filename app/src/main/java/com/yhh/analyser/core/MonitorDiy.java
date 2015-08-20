package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.InfoFactory;
import com.yhh.analyser.bean.MonitorChoice;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.utils.ConstUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorDiy extends Monitor {
    private InfoFactory mInfoFactory;
    /**是否监控项*/
    private List<Boolean> mCheckedList;

    /**监控值*/
    private HashMap<Integer, String> mMonitorHm;

    /**
     * step 2
     *
     * @return
     */
    @Override
    public String getMonitorTitle() {
        StringBuffer sb = new StringBuffer();
        for(int i=2; i<MonitorChoice.COUNT;i++){
            if(mCheckedList.get(i)){
                sb.append(i+",");
            }
        }
        sb.append(ConstUtils.LINE_END);
        return sb.toString();
    }

    /**
     * step 3
     *
     * @param context
     */
    public MonitorDiy(Context context) {
        super(context);
        mInfoFactory.init(context);
    }

    /**
     * step 1
     */
    @Override
    public void onStart() {
        mCheckedList =  MonitorChoice.getInstance().getCheckedList();
        mInfoFactory = InfoFactory.getInstance();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInfoFactory.destory();
    }

    @Override
    public String monitor() {
        /**获取监控数据 */
        getMonitors();

        List<String> dataItems = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        for(int i=2; i<MonitorChoice.COUNT;i++){
            if(mCheckedList.get(i)){
                dataItems.add(mMonitorHm.get(i));
                sb.append(getItemShow(i));
            }
        }
        String[] strArr = new String[mMonitorHm.size()];
        dataItems.toArray(strArr);
        write2File(strArr);

        return sb.toString();
    }


    private String getItemShow(int index){
        StringBuffer sb = new StringBuffer();
        sb.append(getItemName(index)).append(":").append(mMonitorHm.get(index));
        sb.append(getItemUnit(index)).append("\n");
        return  sb.toString();
    }

    private void getMonitors(){
        mMonitorHm = new  HashMap<Integer, String>();
        if(mCheckedList.get(MonitorConst.CPU_USED_RATIO)){
            mMonitorHm.put(MonitorConst.CPU_USED_RATIO, mInfoFactory.getCpuTotalUsedRatio().get(0));
        }
        if(mCheckedList.get(MonitorConst.CPU_CLOCK)){
            mMonitorHm.put(MonitorConst.CPU_CLOCK, mInfoFactory.getCpuFreqList());
        }
        if(mCheckedList.get(MonitorConst.MEM_FREE)){
            mMonitorHm.put(MonitorConst.MEM_FREE, mInfoFactory.getMemoryUnusedSize(mContext));
        }
        if(mCheckedList.get(MonitorConst.GPU_USED_RATIO)){
            mMonitorHm.put(MonitorConst.GPU_USED_RATIO, mInfoFactory.getGpuRate());
        }
        if(mCheckedList.get(MonitorConst.GPU_CLOCK)){
            mMonitorHm.put(MonitorConst.GPU_CLOCK, mInfoFactory.getGpuClock());
        }
        if(mCheckedList.get(MonitorConst.POWER_CURRENT)){
            mMonitorHm.put(MonitorConst.POWER_CURRENT, mInfoFactory.getPowerCurrent());
        }
        if(mCheckedList.get(MonitorConst.BATTERY_LEVEL)){
            mMonitorHm.put(MonitorConst.BATTERY_LEVEL, mInfoFactory.getBatteryLevel());
        }
        if(mCheckedList.get(MonitorConst.BATTERY_TEMP)){
            mMonitorHm.put(MonitorConst.BATTERY_TEMP, mInfoFactory.getBatteryTemperature());
        }
        if(mCheckedList.get(MonitorConst.BATTERY_VOLT)){
            mMonitorHm.put(MonitorConst.BATTERY_VOLT, mInfoFactory.getBatteryVoltage());
        }
        if(mCheckedList.get(MonitorConst.SCREEN_BRIGHTNESS)){
            mMonitorHm.put(MonitorConst.SCREEN_BRIGHTNESS, mInfoFactory.getScreenBrightness());
        }
        if(mCheckedList.get(MonitorConst.TRAFFIC_SPEED)){
            mMonitorHm.put(MonitorConst.TRAFFIC_SPEED, mInfoFactory.getTrafficRevSpeed() + "/" + mInfoFactory.getTrafficSendSpeed());
        }

    }


}
