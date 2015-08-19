/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.CommandUtils;
import com.yhh.analyser.utils.ConstUtils;

public class SettingShellActivity extends BaseActivity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingShell";
	private boolean DEBUG = true;

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

        initUI();
        Toast.makeText(this, "此功能正在重构中..", Toast.LENGTH_LONG).show();
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
}
