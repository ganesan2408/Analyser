package com.yhh.analyser.core;

import android.content.Context;

import com.yhh.analyser.bean.BatteryBean;
import com.yhh.analyser.bean.BatteryInfo;
import com.yhh.analyser.bean.PowerInfo;
import com.yhh.analyser.config.MonitorConst;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorBattery extends Monitor {
    private BatteryInfo mBatteryInfo;
    private PowerInfo mPowerInfo;

    @Override
    public String getMonitorTitle() {
        return MonitorConst.POWER_CURRENT +","
                + MonitorConst.BATTERY_LEVEL +","
                + MonitorConst.BATTERY_TEMP + ","
                + MonitorConst.BATTERY_VOLT;
    }

    public MonitorBattery(Context context) {
        super(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        mPowerInfo = new PowerInfo();
        mBatteryInfo = new BatteryInfo();
        mBatteryInfo.init(mContext);
        mBatteryInfo.register();

    }

    @Override
    public void onDestory() {
        super.onDestory();
        mBatteryInfo.unregister();
    }

    @Override
    public String monitor() {

        BatteryBean bean = mBatteryInfo.getBattery();
        write2File(String.valueOf(mPowerInfo.getcurrent()), bean.getLevel(),
                bean.getTemperature(), bean.getVoltage());

        StringBuffer sb = new StringBuffer();
        sb.append(getItemName(MonitorConst.POWER_CURRENT)).append(":");
        sb.append(String.valueOf(mPowerInfo.getcurrent()));
        sb.append(getItemUnit(MonitorConst.POWER_CURRENT)).append("\n");

        sb.append(getItemName(MonitorConst.BATTERY_LEVEL)).append(":");
        sb.append(bean.getLevel());
        sb.append(getItemUnit(MonitorConst.BATTERY_LEVEL)).append("\n");

        sb.append(getItemName(MonitorConst.BATTERY_TEMP)).append(":");
        sb.append(bean.getTemperature());
        sb.append(getItemUnit(MonitorConst.BATTERY_TEMP)).append("\n");

        sb.append(getItemName(MonitorConst.BATTERY_TEMP)).append(":");
        sb.append(bean.getVoltage());
        sb.append(getItemUnit(MonitorConst.BATTERY_TEMP)).append("\n");

        return sb.toString();
    }




}
