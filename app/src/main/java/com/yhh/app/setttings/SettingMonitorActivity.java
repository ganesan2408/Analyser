/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.app.setttings;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.yhh.activity.BaseActivity;
import com.yhh.analyser.R;
import com.yhh.service.FloatService;
import com.yhh.utils.ConstUtils;
import com.yhh.widget.SwitchButton;

import java.util.List;

public class SettingMonitorActivity extends BaseActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG + "SettingMonitorActivity";
    private boolean DEBUG = true;

    // add items, need change
	public static final int MONITOR_ITEMS_COUNT = 14;
	private SwitchButton[] mMonitorBtn = new SwitchButton[MONITOR_ITEMS_COUNT];

	// In proper order with xml is very important. add items, need change
	private int[] mMonitorBtnIds ={
	        R.id.monitor_settings_app_cpu, R.id.monitor_settings_cpu,
            R.id.monitor_settings_cpu_freq,
	        R.id.monitor_settings_app_memory, R.id.monitor_settings_memory,
	        R.id.monitor_settings_gpu, R.id.monitor_settings_gpu_freq,
	        R.id.monitor_settings_current, R.id.monitor_settings_brightness,
	        R.id.monitor_settings_battery_level, R.id.monitor_settings_battery_temperature,
            R.id.monitor_settings_battery_voltage,
	        R.id.monitor_settings_traffic_rev, R.id.monitor_settings_traffic_send
    };

    private MonitorSettings mMonitorSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_monitor_items_settings);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mMonitorSettings = new MonitorSettings(prefs);
    }

    @Override
    protected void onStart() {
        initUI();
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void initUI() {
        ActionBar bar = this.getActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setIcon(R.drawable.nav_back);
        }

        List<Boolean> checkedList = mMonitorSettings.getCheckedList();
        for (int i = 0; i < MONITOR_ITEMS_COUNT; i++) {
            final int index = i;
            mMonitorBtn[i] = (SwitchButton) findViewById(mMonitorBtnIds[i]);
            mMonitorBtn[i].setChecked(checkedList.get(i));
            mMonitorBtn[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mMonitorSettings.setItemChecked(index, isChecked);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
