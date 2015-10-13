/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.activity;

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
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.view.BaseActivity;

public class MonitorExceptionActivity extends BaseActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public static final String PREF_EXCEPTION_ITEM = "exception_temp";

    private EditText mThresholdEt;
    private Button mStartBtn;

    private int mThresholdValue;
    private int defaultThreshold = 38;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_monitor_exception_settings);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mPreferences.edit();

        initUI();
        readRefs();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initUI() {
        mStartBtn = (Button) findViewById(R.id.btn_exception_monitor);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MonitorExceptionActivity.this, "异常监控已启动", Toast.LENGTH_SHORT).show();
                Intent monitorService = new Intent();
                monitorService.setClass(mContext, MonitorService.class);
                monitorService.putExtra("type", MonitorFactory.TYPE_EXCEPTION);
                monitorService.putExtra("limit", mThresholdValue);
                monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startService(monitorService);
                finish();
            }
        });

        mThresholdEt = (EditText) findViewById(R.id.et_temp_limit);
        mThresholdEt.addTextChangedListener(new TextWatcher() {

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
                    mThresholdValue = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    mThresholdValue = 0;
                }
            }
        });
    }

    public void readRefs() {
        mThresholdEt.setText(mPreferences.getInt(PREF_EXCEPTION_ITEM, defaultThreshold)+"");
    }

    public void writeRefs() {
        mEditor.putInt(PREF_EXCEPTION_ITEM, mThresholdValue).commit();
    }

    @Override
    protected void onPause() {
        writeRefs();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        writeRefs();
        super.onDestroy();
    }
}
