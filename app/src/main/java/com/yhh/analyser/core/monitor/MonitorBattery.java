package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.model.BatteryWorker;
import com.yhh.analyser.model.PowerInfo;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorBattery extends Monitor {
    private BatteryWorker mBatteryWorker;
    private PowerInfo mPowerInfo;

//    private ArrayList<String> mContentList;
    private ArrayList<String> mWriteableList;

    public MonitorBattery(Context context) {
        super(context);

    }

    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorFactory.POWER_CURRENT,
                MonitorFactory.BATTERY_LEVEL,
                MonitorFactory.BATTERY_TEMP,
                MonitorFactory.BATTERY_VOLT
        };
    }

    @Override
    public String getFileType() {
        return "_Battery";
    }

    @Override
    public void onStart() {
        super.onStart();
        mPowerInfo = new PowerInfo();
        mBatteryWorker = new BatteryWorker();
        mBatteryWorker.init(mContext);
        mBatteryWorker.register();

//        mContentList = new ArrayList<>();
        mWriteableList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBatteryWorker.unregister();
    }

    @Override
    public String monitor() {
        writeBatteryInfo();

        return "current: " + mPowerInfo.getcurrent() + "mA \n" + mBatteryWorker.getBatteryShowInfo();
    }

    /**
     * 将电池信息吸入到文件
     */
    private void writeBatteryInfo(){
        mWriteableList.clear();
        mWriteableList.add(String.valueOf(mPowerInfo.getcurrent()));
        mWriteableList.addAll(mBatteryWorker.getBatteryInfo());
        write2File(mWriteableList);
    }
}
