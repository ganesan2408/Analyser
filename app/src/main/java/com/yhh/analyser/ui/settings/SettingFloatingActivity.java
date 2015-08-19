/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.widget.SwitchButton;

public class SettingFloatingActivity extends BaseActivity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingFloating";
	private boolean DEBUG = true;
	
	private final int MONITOR_ITEMS_COUNT = 13;
	private SwitchButton[] mMonitorBtn = new SwitchButton[MONITOR_ITEMS_COUNT];
	
	// In proper order with xml is very important
    private int[] mMonitorBtnIds ={
            R.id.monitor_settings_app_cpu, R.id.monitor_settings_cpu, R.id.monitor_settings_cpu_freq,
            R.id.monitor_settings_app_memory, R.id.monitor_settings_memory,
            R.id.monitor_settings_gpu, R.id.monitor_settings_gpu_freq,
            R.id.monitor_settings_current, R.id.monitor_settings_brightness,
            R.id.monitor_settings_battery_level, R.id.monitor_settings_battery_temperature, R.id.monitor_settings_battery_voltage,
            R.id.monitor_settings_traffic
    };
    
    public static final String[] PREF_FLOATING_ITEMS = {
        "AppCpu", "SysCpu", "SysCpuFreq", 
        "AppMemory", "SysMemory",
        "SysGpu", "SysGpuFreq",
        "Current", "Brightness", 
        "BatteryLevel", "BatteryTemperature", "BatteryVoltage",
        "TrafficeSpeed"
    };
	
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	@SuppressLint("NewApi")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_items_settings);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPreferences.edit();
		
	}
	
	@Override
    protected void onStart() {
        initUI();
        super.onStart();
    }
	
	public void initUI(){
//	    for(int i=0; i<MONITOR_ITEMS_COUNT; i++){
//	        final int index = i;
//	        mMonitorBtn[i] = (SwitchButton) findViewById(mMonitorBtnIds[i]);
//	        if(mPreferences.getBoolean(SettingMonitorActivity.PREF_MONITOR_ITEMS[index], false)){
//	            mMonitorBtn[i].setVisibility(View.GONE);
//
//	        }else{
//	            mMonitorBtn[i].setVisibility(View.VISIBLE);
//    	        mMonitorBtn[i].setChecked(mPreferences.getBoolean(PREF_FLOATING_ITEMS[index], false));
//    	        mMonitorBtn[i].setOnCheckedChangeListener(new OnCheckedChangeListener(){
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView,
//                            boolean isChecked) {
//                        if(DEBUG){
//                            Log.i(TAG,index+"# onCheckedChanged: "+isChecked);
//                        }
//                        mEditor.putBoolean(PREF_FLOATING_ITEMS[index], isChecked);
//                        mEditor.commit();
//                    }
//    	        });
//	        }
//
//	    }
	    
	}
	
}
