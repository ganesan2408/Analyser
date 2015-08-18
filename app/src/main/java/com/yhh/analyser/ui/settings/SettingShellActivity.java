/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.CommandUtils;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.widget.SwitchButton;

public class SettingShellActivity extends Activity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingShellActivity";
	private boolean DEBUG = true;

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	private SwitchButton mShellMonitorSb;
	private Button mTopBtn;
	public static final String KEY_IS_MONITOR = "shellMonitor";
	
	public static final int LIMIT_ITEMS_COUNT = 1;
    private int[] mItemIds ={
            R.id.shell_command,
    };
    
    public static final String[] PREF_EXCEPTION_ITEMS = {
        "shell_command",
    };
    
    private EditText[] mThresholdEt = new EditText[LIMIT_ITEMS_COUNT];
    private String[]  mThresholdValues = new String[LIMIT_ITEMS_COUNT];
	
	
	@SuppressLint("NewApi")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_shell_settings);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mEditor = mPreferences.edit();
		
		ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
        initUI();
        readRefs();
		Toast.makeText(this,"重构中,此功能后续开发", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	}
	
	public void initUI(){
	    mTopBtn = (Button) findViewById(R.id.shell_top);
	    mTopBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                mThresholdEt[0].setText(CommandUtils.CMD_TOP_PROCESS);
            }
	        
	    });
	    mShellMonitorSb = (SwitchButton) findViewById(R.id.shell_monitor_sb);
	    mShellMonitorSb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mPreferences.edit().putBoolean(SettingShellActivity.KEY_IS_MONITOR, isChecked).commit();
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
        boolean isShellMonitor = mPreferences.getBoolean(KEY_IS_MONITOR, true);
        mShellMonitorSb.setChecked(isShellMonitor);
        
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
