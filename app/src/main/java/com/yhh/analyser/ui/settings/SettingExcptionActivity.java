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
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.widget.SwitchButton;

public class SettingExcptionActivity extends BaseActivity {
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_exception_settings);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPreferences.edit();
		
        initUI();
        readRefs();
		Toast.makeText(this, "重构中,此功能后续开发", Toast.LENGTH_LONG).show();
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
	    }
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
	
}
