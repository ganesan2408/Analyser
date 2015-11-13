/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.view.BaseFragment;
import com.yhh.analyser.view.activity.MonitorAnalyticActivity;
import com.yhh.analyser.view.activity.MonitorAppMainActivity;
import com.yhh.analyser.view.activity.MonitorDiyActivity;
import com.yhh.analyser.view.activity.MonitorExceptionActivity;
import com.yhh.analyser.view.activity.MonitorShellActivity;
import com.yhh.androidutils.DebugLog;
import com.yhh.androidutils.PreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMonitorFragment extends BaseFragment {

    public static final String KEY_INTERVAL = "interval";
    public static final int DEFAULT_FREQ = 2;
    public static final String KEY_HAVE_BACKGROUND = "have_background";
    public static final boolean DEFAULT_BG = false;

    private CheckBox mHaveBackGrounp;
    private GridView mGridView;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList;
    private final String[] mMonitorItems = new String[]{
            "性能", "电池","全监控",
            "CPU频率","Top", "APP",
             "异常", "可选", "高级"

    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setData();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sys_detailed_info, null) ;
        initView(v);
        return v;
    }

    private void initView(View v) {
        Button analyticBtn = (Button) v.findViewById(R.id.btn_analytic_data);
        analyticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MonitorAnalyticActivity.class));

            }
        });

        //是否开启背景
        mHaveBackGrounp = (CheckBox) v.findViewById(R.id.cb_have_background);
        boolean haveBg = PreferencesUtils.getInstance(mContext).get(KEY_HAVE_BACKGROUND, DEFAULT_BG);
        mHaveBackGrounp.setChecked(haveBg);

        mHaveBackGrounp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.getInstance(mContext).put(KEY_HAVE_BACKGROUND, isChecked);
            }
        });

        //初始化监控频率UI
        SeekBar monitorFreqSb = (SeekBar) v.findViewById(R.id.seekbar_freq);
        final TextView monitorFreqTv = (TextView) v.findViewById(R.id.tv_freq_value);

        int freqValue = PreferencesUtils.getInstance(mContext).get(KEY_INTERVAL, DEFAULT_FREQ);
        monitorFreqTv.setText(String.valueOf(freqValue));
        monitorFreqSb.setProgress(freqValue - 1);

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
                PreferencesUtils.getInstance(mContext).put(KEY_INTERVAL, interval);
            }
        });

        //初始化 监控项UI
        String[] from = {"text"};
        int[] to = {R.id.text};
        mAdapter = new SimpleAdapter(mContext, mDataList, R.layout.sys_monitor_item, from, to);

        mGridView = (GridView) v.findViewById(R.id.monitor_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (MonitorService.sMonitorIsRunning) {
                    Toast.makeText(mContext, "监控已启动,请勿重复开启", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(position == MonitorFactory.TYPE_DIY) { //可选监控
                    Intent diy = new Intent(mContext, MonitorDiyActivity.class);
                    diy.putExtra("type", MonitorFactory.TYPE_DIY);
                    startActivity(diy);
                } else if (position == MonitorFactory.TYPE_EXCEPTION) { //异常监控
                    Intent diy = new Intent(mContext, MonitorExceptionActivity.class);
                    startActivity(diy);
                } else if (position == MonitorFactory.TYPE_SHELL) { //高级监控
                    Intent diy = new Intent(mContext, MonitorShellActivity.class);
                    startActivity(diy);
                } else if (position == MonitorFactory.TYPE_APP_MONITOR) { //App监控
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
                }
            }

        });
    }

    private void setData() {
        mDataList = new ArrayList<>();
        for (String item : mMonitorItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", item);
            mDataList.add(map);
        }
    }


}
