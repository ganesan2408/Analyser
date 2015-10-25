/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.androidutils.DebugLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorSysActivity extends BaseActivity {

    private SharedPreferences preferences;
    private static final int DEFAULT_FREQ = 2;
    public static final String KEY_INTERVAL = "interval";


    private GridView mGridView;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private final String[] mMonitorItems = new String[]{
            "性能", "电池",
            "CPU频率", "Top",
            "全监控", "可选监控",
            "异常监控", "高级监控"
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_detailed_info);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        initGridView();
        initProgressView();
    }

    private void initProgressView() {
        SeekBar monitorFreqSb = (SeekBar) findViewById(R.id.seekbar_freq);
        final TextView monitorFreqTv = (TextView) findViewById(R.id.tv_freq_value);

        int freqValue = preferences.getInt(KEY_INTERVAL, DEFAULT_FREQ);
        monitorFreqTv.setText(freqValue+"");
        monitorFreqSb.setProgress(freqValue-1);

        monitorFreqSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                monitorFreqTv.setText(Integer.toString(arg1 + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                int interval = arg0.getProgress() + 1;
                preferences.edit().putInt(KEY_INTERVAL, interval).commit();
            }
        });
    }

    private void initGridView() {
        setData();

        String[] from = {"text"};
        int[] to = {R.id.text};
        mAdapter = new SimpleAdapter(this, mDataList, R.layout.sys_monitor_item, from, to);

        mGridView = (GridView) findViewById(R.id.monitor_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (MonitorService.sMonitorIsRunning) {
                    Toast.makeText(mContext, "监控已启动,请勿重复开启", Toast.LENGTH_SHORT).show();
                }

                if (position == MonitorFactory.TYPE_DIY) { //可选监控
                    Intent diy = new Intent(mContext, MonitorDiyActivity.class);
                    diy.putExtra("type", MonitorFactory.TYPE_DIY);
                    startActivity(diy);
                } else if (position == MonitorFactory.TYPE_EXCEPTION) { //异常监控
                    Intent diy = new Intent(mContext, MonitorExceptionActivity.class);
                    startActivity(diy);
                } else if (position == MonitorFactory.TYPE_SHELL) { //高级监控
                    Intent diy = new Intent(mContext, MonitorAppMainActivity.class);
                    startActivity(diy);

                } else {

                    DebugLog.d("startup monitor");
                    Toast.makeText(mContext, "启动监控", Toast.LENGTH_SHORT).show();

                    AppConfig.TYPE = position;
                    Intent monitorService = new Intent();
                    monitorService.putExtra("type", position);
                    monitorService.setClass(mContext, MonitorService.class);
                    monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    monitorService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startService(monitorService);
                    finish();
                }
            }

        });
    }

    private void setData() {
        for (String item : mMonitorItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", item);
            mDataList.add(map);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        this.getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
