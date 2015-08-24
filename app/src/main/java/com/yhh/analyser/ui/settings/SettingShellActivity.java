/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.StringUtils;

public class SettingShellActivity extends BaseActivity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SettingShell";
	private boolean DEBUG = true;

	private Button mMonitorBtn;
    private EditText mCmdEdit;
    public static String sCommand;

	
	@SuppressLint("NewApi")
    @Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_shell_settings);

        initUI();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	}
	
	public void initUI(){
	    mMonitorBtn = (Button) findViewById(R.id.btn_senior_monitor);
	    mMonitorBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(StringUtils.isBlank(sCommand)){
                    Toast.makeText(SettingShellActivity.this, "adb指令不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent monitorService = new Intent();
                monitorService.setClass(mContext, MonitorService.class);
                monitorService.putExtra("type", MonitorConst.MONITOR_SHELL);
                monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startService(monitorService);
                finish();
            }

        });

        mCmdEdit = (EditText)findViewById(R.id.shell_command);
        mCmdEdit.addTextChangedListener(new TextWatcher() {

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
                try {
                    sCommand = s.toString();
                } catch (Exception e) {
                    sCommand = "";
                }
            }
        });

        findViewById(R.id.txt_monitot_shell_title).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCmdEdit.setText("cat /sys/class/leds/lcd-backlight/brightness");
            }
        });

        if(!StringUtils.isBlank(sCommand)){
            mCmdEdit.setText(sCommand);
        }

	}
}
