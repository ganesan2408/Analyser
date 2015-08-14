/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.setttings;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.yhh.activity.BaseActivity;
import com.yhh.analyser.R;
import com.yhh.service.FloatService;
import com.yhh.utils.ConstUtils;
import com.yhh.widget.SwitchButton;

public class SettingMonitorActivity extends BaseActivity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingMonitorActivity";
	private boolean DEBUG = true;
	
	// add items, need change
	public static final int MONITOR_ITEMS_COUNT = 13;
	private SwitchButton[] mMonitorBtn = new SwitchButton[MONITOR_ITEMS_COUNT];
	
	// In proper order with xml is very important. add items, need change
	private int[] mMonitorBtnIds ={
	        R.id.monitor_settings_app_cpu, R.id.monitor_settings_cpu, R.id.monitor_settings_cpu_freq,
	        R.id.monitor_settings_app_memory, R.id.monitor_settings_memory,
	        R.id.monitor_settings_gpu, R.id.monitor_settings_gpu_freq,
	        R.id.monitor_settings_current, R.id.monitor_settings_brightness,
	        R.id.monitor_settings_battery_level, R.id.monitor_settings_battery_temperature, R.id.monitor_settings_battery_voltage,
	        R.id.monitor_settings_traffic_speed
	};
	
	//add items, need change
	public static final String[] PREF_MONITOR_ITEMS = {
	    "mAppCpu", "mSysCpu", "mSysCpuFreq", 
	    "mAppMemory", "mSysMemory",
	    "mSysGpu", "mSysGpuFreq",
	    "mCurrent", "mBrightness", 
	    "mBatteryLevel", "mBatteryTemperature", "mBatteryVoltage",
	    "mTrafficeSpeed"
	};

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;;
	
	@SuppressLint("NewApi")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_items_settings);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPreferences.edit();
		
		ActionBar bar = this.getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
		
	}
	
	@Override
	protected void onStart() {
	    initUI();
	    super.onStart();
	}
	
	public void initUI(){
	    for(int i=0; i<MONITOR_ITEMS_COUNT; i++){
	        final int index = i;
	        mMonitorBtn[i] = (SwitchButton) findViewById(mMonitorBtnIds[i]);
	        mMonitorBtn[i].setChecked(mPreferences.getBoolean(PREF_MONITOR_ITEMS[index], false));
	        mMonitorBtn[i].setOnCheckedChangeListener(new OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    mEditor.putBoolean(PREF_MONITOR_ITEMS[index], isChecked);
                    mEditor.putBoolean(SettingFloatingActivity.PREF_FLOATING_ITEMS[index], isChecked);
                    mEditor.commit();
                }
	        });
	        
	    }

		//临时方案
		findViewById(R.id.btn_diy_monitor).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent monitorService = new Intent();
				monitorService.setClass(mContext, FloatService.class);
				monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				monitorService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startService(monitorService);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == android.R.id.home){
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
}
