/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

class BatteryInfo {
    private Context mContext;
    private BatteryInfoBroadcastReceiver batteryBroadcast;
    
    private String mBatteryLevel;
    private String mBatteryVoltage;
    private String mBatteryTemperature;
    
    public void init(Context context){
        mContext = context;
    }
    
    public String getLevel(){
        return mBatteryLevel;
    }
    
    public String getVoltage(){
        return mBatteryVoltage;
    }
    
    public String getTemperature(){
        return mBatteryTemperature;
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
                mBatteryLevel = String.valueOf(level * 100 / scale);
                mBatteryVoltage = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000);
                mBatteryTemperature = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10);
            }
        }
    }
}
