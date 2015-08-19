/**
 * @author yuanhh1
 *
 * @email yuanhh1@lenovo.com
 *
 */
package com.yhh.analyser.bean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryInfo {
    private BatteryBean mBattery;
    private Context mContext;
    private BatteryInfoBroadcastReceiver batteryBroadcast;

    public BatteryBean getBattery(){
        return  mBattery;
    }


    public void init(Context context){
        mContext = context;
        mBattery = new BatteryBean();
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
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                mBattery.setLevel(String.valueOf(level * 100 / scale));
                mBattery.setVoltage(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000));
                mBattery.setmTemperature(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10));
            }
        }
    }
}