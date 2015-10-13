package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.bean.BatteryBean;
import com.yhh.analyser.bean.BatteryInfo;
import com.yhh.analyser.bean.PowerInfo;
import com.yhh.analyser.core.MonitorFactory;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorBattery extends Monitor {
    private BatteryInfo mBatteryInfo;
    private PowerInfo mPowerInfo;

    private ArrayList<String> mContentList;

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
        mBatteryInfo = new BatteryInfo();
        mBatteryInfo.init(mContext);
        mBatteryInfo.register();

        mContentList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBatteryInfo.unregister();
    }

    @Override
    public String monitor() {
        BatteryBean bean = mBatteryInfo.getBattery();

        mContentList.clear();
        mContentList.add(String.valueOf(mPowerInfo.getcurrent()));
        mContentList.add(bean.getLevel());
        mContentList.add(bean.getTemperature());
        mContentList.add(bean.getVoltage());

        write2File(mContentList);

        return getFloatBody(mContentList);
    }
}
