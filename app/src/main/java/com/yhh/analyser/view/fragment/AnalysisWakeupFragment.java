/**
 * @author yuanhh1
 *
 * @email yuanhh1@lenovo.com
 *
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.adapter.WakupRankAdapter;
import com.yhh.analyser.bean.AlarmBean;
import com.yhh.analyser.provider.AlarmManager;
import com.yhh.analyser.provider.AlarmShellManager;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DebugLog;
import com.yhh.analyser.utils.DialogUtils;
import com.yhh.analyser.view.BaseFragment;

import java.util.Collections;
import java.util.List;

public class AnalysisWakeupFragment extends BaseFragment {

    private static final String TAG = ConstUtils.DEBUG_TAG+ "AnalysisMonitor";
    private ListView mListView;
    private TextView mWakeupTitleTv;
    private AlarmManager alarmManager;
    private List<AlarmBean> mAlarmList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.d("BEGIN");
//        alarmManager = new AlarmFileManager("sdcard/log/2015_01_16_04_19_16/alarm.txt");
        alarmManager = new AlarmShellManager();
        alarmManager.parse(mContext);
        mAlarmList = alarmManager.getAlarmList();
        Collections.sort(mAlarmList);
        DebugLog.d("OVER");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_tool_wakeup, null);
        mListView = (ListView)v.findViewById(R.id.lv_analysis_wakeup);
        mWakeupTitleTv = (TextView) v.findViewById(R.id.tv_wakeup_time_title);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        work();

    }

    private void work(){
        mWakeupTitleTv.setText("统计时间:" + alarmManager.getElapsedTime());
        mListView.setAdapter(new WakupRankAdapter(mContext, mAlarmList));
        DebugLog.d("=======");
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogUtils.showAlergDialog(mContext,  mAlarmList.get(position).getAppName(),
                        mAlarmList.get(position).getAlarmTypeString());
            }
        });
    }



}