/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.ui.base.BaseActivity;

public class SettingExcptionActivity extends BaseActivity {

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	public static final String KEY_IS_MONITOR = "exceptionMonitor";
	
	public static final int LIMIT_ITEMS_COUNT = 2;
    private int[] mItemIds ={
            R.id.exception_temperature_limit,
            R.id.exception_current_limit
    };
    
    public static final String[] PREF_EXCEPTION_ITEMS = {
        "exception_temp",  "exception_current"
    };
    
    private EditText[] mThresholdEt = new EditText[LIMIT_ITEMS_COUNT];
    private String[]  mThresholdValues = new String[LIMIT_ITEMS_COUNT];

    private Button mStartBtn;
	
	
	@SuppressLint("NewApi")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_exception_settings);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPreferences.edit();
		
        initUI();
        readRefs();
		Toast.makeText(this, "此功能正在重构中..", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	}
	
	public void initUI(){
        mStartBtn = (Button) findViewById(R.id.btn_exception_monitor);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingExcptionActivity.this, "异常监控已启动", Toast.LENGTH_SHORT).show();
                Intent monitorService = new Intent();
                monitorService.setClass(mContext, MonitorService.class);
                monitorService.putExtra("type", MonitorConst.MONITOR_EXCEPTION);
                monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startService(monitorService);
                finish();
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
