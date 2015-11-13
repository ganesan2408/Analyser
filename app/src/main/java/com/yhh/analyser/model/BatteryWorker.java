/**
 * @author yuanhh1
 *
 * @email yuanhh1@lenovo.com
 *
 */
package com.yhh.analyser.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.ArrayList;

public class BatteryWorker {
    private BatteryModel mBattery;
    private Context mContext;
    private BatteryInfoBroadcastReceiver batteryBroadcast;

    public void init(Context context){
        mContext = context;
        mBattery = new BatteryModel();
    }

    public BatteryModel getBattery(){
        return mBattery;
    }

    public void register(){
        batteryBroadcast = new BatteryInfoBroadcastReceiver();
        mContext.registerReceiver(batteryBroadcast, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public void unregister(){
        mContext.unregisterReceiver(batteryBroadcast);
    }

    class BatteryInfoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                mBattery.setStatus(intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0));
                mBattery.setHealth(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0));
                mBattery.setLevel(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
                mBattery.setVoltage(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0));
                mBattery.setTemperature(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0));
                mBattery.setPlugged(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));
                mBattery.setPresent(intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false));
                mBattery.setTechnology(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
            }
        }
    }

    public ArrayList<String> getBatteryInfo(){
        return mBattery.getInfo();
    }

    public String getBatteryShowInfo(){
       return mBattery.getShowInfo();
    }
}