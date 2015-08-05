/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.setttings;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;
import com.yhh.widget.SwitchButton;

public class SettingExcptionActivity extends Activity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingExcptionActivity";
	private boolean DEBUG = true;

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	private SwitchButton mExceptionMonitorSb;
	public static final String KEY_IS_MONITOR = "exceptionMonitor";
	
	public static final int LIMIT_ITEMS_COUNT = 3;
    private int[] mItemIds ={
            R.id.exception_temperature_limit,
            R.id.exception_cpu_limit,
            R.id.exception_current_limit
    };
    
    public static final String[] PREF_EXCEPTION_ITEMS = {
        "exception_temp", "exception_cpu", "exception_current"
    };
    
    private EditText[] mThresholdEt = new EditText[LIMIT_ITEMS_COUNT];
    private String[]  mThresholdValues = new String[LIMIT_ITEMS_COUNT];
	
	
	@SuppressLint("NewApi")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_exception_settings);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPreferences.edit();
		
		ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
        initUI();
        readRefs();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	}
	
	public void initUI(){
	    mExceptionMonitorSb = (SwitchButton) findViewById(R.id.app_exception_monitor_sb);
	    mExceptionMonitorSb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mPreferences.edit().putBoolean(SettingExcptionActivity.KEY_IS_MONITOR, isChecked).commit();
            }
        });
 
	    for(int i=0; i< LIMIT_ITEMS_COUNT; i++){
	        final int index = i;
	        mThresholdEt[i] = (EditText)findViewById(mItemIds[i]);
	        mThresholdEt[i].addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                        int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                        int count) {
                    
                }
    
                @Override
                public void afterTextChanged(Editable s) {
                    try{
                        mThresholdValues[index] = s.toString();
                    }catch(Exception e){
                        mThresholdValues[index] = "";
                    }
                }
	        });
	    };
	}
	
	public void readRefs(){
        boolean isExceptionMonitor = mPreferences.getBoolean(KEY_IS_MONITOR, true);
        mExceptionMonitorSb.setChecked(isExceptionMonitor);
        
        for(int i=0; i<LIMIT_ITEMS_COUNT;i++){
            String value = mPreferences.getString(PREF_EXCEPTION_ITEMS[i], "");
            mThresholdEt[i].setText(value);
        }
    }
	
	public void writeRefs(){
	    for(int i=0; i<LIMIT_ITEMS_COUNT;i++){
            mEditor.putString(PREF_EXCEPTION_ITEMS[i], mThresholdValues[i]);
        }
	    mEditor.commit();
	}
	
	@Override
	protected void onPause() {
	    writeRefs();
	    super.onPause();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == android.R.id.home){
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
}
