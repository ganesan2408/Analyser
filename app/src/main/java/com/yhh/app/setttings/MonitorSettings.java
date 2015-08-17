package com.yhh.app.setttings;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/17.
 */
public class MonitorSettings {
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private List<Boolean> mSettingList;

    private static final int COUNT = 14;
    private static final String[] KEY_MONITOR_ITEMS = {
	    "mAppCpu", "mSysCpu", "mSysCpuFreq",
	    "mAppMemory", "mSysMemory",
	    "mSysGpu", "mSysGpuFreq",
	    "mCurrent", "mBrightness",
	    "mBatteryLevel", "mBatteryTemperature", "mBatteryVoltage",
	    "trafficRev","trafficSend"
	};


    public MonitorSettings(SharedPreferences pref){
        mSettingList = new ArrayList<>(COUNT);
        mPrefs = pref;
        mEditor = mPrefs.edit();
        readPrefs();
    }

    private void readPrefs(){
       boolean defaultValue = false;
        for (int i = 0; i < COUNT; i++) {
            mSettingList.set(i, mPrefs.getBoolean(KEY_MONITOR_ITEMS[i],defaultValue));
        }
    }
    
    private void commitBoolPref(String key, boolean Value){
        mEditor.putBoolean(key, Value);
        mEditor.commit();
    }

    public List<Boolean> getCheckedList(){
        return mSettingList;
    }

    public void setItemChecked(int index, boolean checked){
        mSettingList.set(index, checked);
        commitBoolPref(KEY_MONITOR_ITEMS[index], checked);
    }

}
