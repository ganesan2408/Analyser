/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.monitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yhh.app.setttings.SettingShellActivity;
import com.yhh.constant.MonitorConst;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.ShellUtils;

public class ShellMonitor {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "ShellMonitor";
    private boolean DEBUG =false;
    
    private boolean mIsEnabled;
    
    public static final int CONDITIONS_COUNT = 1;
    private String[] mThresholdValues;
    public static final int[] CONDITIONS_ITEMS = new int[]{MonitorConst.BATTERY_TEMPERATURE};
    
    public ShellMonitor(Context context){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        mIsEnabled = !mPref.getBoolean(SettingShellActivity.KEY_IS_MONITOR, true);
        
        mThresholdValues = new String[SettingShellActivity.LIMIT_ITEMS_COUNT];
        for(int i=0;i<SettingShellActivity.LIMIT_ITEMS_COUNT;i++){
            mThresholdValues[i] = mPref.getString(SettingShellActivity.PREF_EXCEPTION_ITEMS[i], "");
        }
    }
    
    public static ShellMonitor newInstance(Context context){
        return new ShellMonitor(context);
    }
    
    public String exec(){
        if(mIsEnabled){
            return ShellUtils.execCommand(mThresholdValues[0], false).successMsg;
        }
        return null;
    }
    
    public String execTop(int topNum){
        String topCmd = "top -m "+topNum+" -n 1 -d 1";
        String rawRtn = ShellUtils.execCommand(topCmd, false).successMsg;
        return parseTopResult(rawRtn.trim());
    }
    
    private String parseTopResult(String result){
        StringBuffer topRtn =new StringBuffer();
        String[] lines = result.split("\n");
        int len = lines.length;
        String[] words;
        int wordsLen;
        for(int i=4;i<len;i++){
            words = lines[i].trim().split("\\s+");
            wordsLen = words.length;
            topRtn.append(words[2]).append("  ");
            topRtn.append(words[wordsLen -1]).append("\n");
//            Log.i(TAG, words[2]+" = "+words[wordsLen -1]);
            words = null;
        }
        return topRtn.toString();
    }
    
    public boolean isEnabled(){
        return mIsEnabled;
    }
    
}
