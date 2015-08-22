/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;

public class SettingsActivity extends BaseActivity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingsActivity";
	private boolean DEBUG = true;

	private SeekBar mMonitorFreqSb;
	private TextView mMonitorFreqTv;
	private SharedPreferences preferences;
	
    public static final String KEY_INTERVAL = "interval";
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_settings);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		initUI();
		readRefs();
	}
	
	public void initUI(){
        mMonitorFreqTv = (TextView) findViewById(R.id.time);
        mMonitorFreqSb = (SeekBar) findViewById(R.id.app_frequence_sb);
        
        mMonitorFreqSb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                mMonitorFreqTv.setText(Integer.toString(arg1 + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                int interval = arg0.getProgress() + 1;
                preferences.edit().putInt(KEY_INTERVAL, interval).commit();
                if(DEBUG){
                    Log.d(TAG,"KEY_INTERVAL="+ interval);
                }
            }
        });
	}
	
	public void readRefs(){
	    int interval = preferences.getInt(KEY_INTERVAL, 2);
        mMonitorFreqTv.setText(String.valueOf(interval));
        mMonitorFreqSb.setProgress(interval-1);
	}
	
	public void settingHandler(View v){
	    if(v.getId() == R.id.app_monitor_items_rl){
	        Intent i = new Intent(this, SettingMonitorActivity.class);
	        startActivity(i);
	    }else if(v.getId() == R.id.app_floating_items_rl){
	        Intent i = new Intent(this, SettingFloatingActivity.class);
            startActivity(i);
	    }else if(v.getId() == R.id.app_exception_monitor_rl){
    	    Intent i = new Intent(this, SettingExcptionActivity.class);
    	    startActivity(i);
	    }else if(v.getId() == R.id.shell_monitor_rl){
    	    Intent i = new Intent(this, SettingShellActivity.class);
    	    startActivity(i);
	    }
	}
	
}
