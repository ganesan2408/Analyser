/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.ui.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.yhh.analyser.R;
import com.yhh.analyser.bean.MonitorChoice;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.widget.SwitchButton;

import java.util.List;

public class SettingMonitorActivity extends BaseActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG + "SettingMonitorActivity";
    private boolean DEBUG = true;

    // add items, need change
	private SwitchButton[] mMonitorBtn;
    private String[] itemTitles;

	// In proper order with xml is very important. add items, need change
	private int[] mMonitorBtnIds ={
	        R.id.monitor_settings_app_cpu, R.id.monitor_settings_app_memory,
            R.id.monitor_settings_cpu, R.id.monitor_settings_cpu_freq,

	        R.id.monitor_settings_gpu, R.id.monitor_settings_gpu_freq,
            R.id.monitor_settings_memory, R.id.monitor_settings_current,

            R.id.monitor_settings_brightness,
	        R.id.monitor_settings_battery_level, R.id.monitor_settings_battery_temperature,
            R.id.monitor_settings_battery_voltage, R.id.monitor_settings_traffic
    };

//    private MonitorSettings mMonitorSettings;

    private MonitorChoice mChoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_monitor_items_settings);
        itemTitles= getResources().getStringArray(R.array.monitor_items);

        mChoice = MonitorChoice.getInstance();
        mMonitorBtn = new SwitchButton[mChoice.getCount()];
    }

    @Override
    protected void onStart() {
        initUI();
        super.onStart();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void initUI() {

        List<Boolean> checkedList = mChoice.getCheckedList();
        for (int i = 0; i < mChoice.getCount(); i++) {
            final int index = i;
            mMonitorBtn[i] = (SwitchButton) findViewById(mMonitorBtnIds[i]);
            mMonitorBtn[i].setChecked(!checkedList.get(i));
            mMonitorBtn[i].setText(itemTitles[i]);
            mMonitorBtn[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mChoice.setItemChecked(index, !isChecked);
                }
            });
        }

        //临时方案
        findViewById(R.id.btn_diy_monitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent monitorService = new Intent();
                monitorService.setClass(mContext, MonitorService.class);
                monitorService.putExtra("type", getIntent().getIntExtra("type",-1));
                monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startService(monitorService);
                finish();
            }
        });
    }
}
