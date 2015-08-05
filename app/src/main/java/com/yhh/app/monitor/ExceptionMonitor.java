/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.monitor;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yhh.app.setttings.SettingExcptionActivity;
import com.yhh.utils.ConstUtils;

public class ExceptionMonitor {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "ExceptionMonitor";
    private boolean DEBUG =false;
    
    private boolean mIsEnabled;
    
    public static final int CONDITIONS_COUNT = 3;
    private String[] mThresholdValues;
    public static final int[] CONDITIONS_ITEMS = new int[]{InfoCollector.BATTERY_TEMPERATURE,
            InfoCollector.CPU_USED_RATIO,
            InfoCollector.POWER_CURRENT};
    
    public ExceptionMonitor(Context context){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        mIsEnabled = !mPref.getBoolean(SettingExcptionActivity.KEY_IS_MONITOR, true);
        
        mThresholdValues = new String[SettingExcptionActivity.LIMIT_ITEMS_COUNT];
        for(int i=0;i<SettingExcptionActivity.LIMIT_ITEMS_COUNT;i++){
            mThresholdValues[i] = mPref.getString(SettingExcptionActivity.PREF_EXCEPTION_ITEMS[i], "");
        }
    }
    
    public void monitor(HashMap<Integer, String> conditions){
        if(isSatisfy(conditions)){
            if(DEBUG){
                Log.d(TAG,"###Exception monitor is beginning.");
            }
            ExceptionStat.getInstance().beginStatistic();
        }
    }
    
    public boolean isEnabled(){
        return mIsEnabled;
    }
    
    private boolean isSatisfy(HashMap<Integer, String> conditions){
        for(int i=0;i< CONDITIONS_COUNT;i++){
            if(!mThresholdValues[i].equals("")){
                String data = conditions.get(CONDITIONS_ITEMS[i]);
                if(data !=null && !data.equals("") 
                   && Float.parseFloat(data) < Integer.parseInt(mThresholdValues[i]) ){
                           return false;
                }
            }
        }
        return true;
    }
}
