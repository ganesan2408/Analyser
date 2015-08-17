package com.yhh.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.core.alarm.AlarmManager;
import com.yhh.core.alarm.FileAlarmManager;
import com.yhh.model.AlarmInfo;

import java.util.Collections;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class WakeupActivity extends BaseActivity {
    TextView mWakeupTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_wakeup);
        initView();
        work();
    }

    private void initView(){
        mWakeupTv = (TextView) findViewById(R.id.tv_tool_wakeup_info);
    }

    protected void work() {
        AlarmManager alarmManager = new FileAlarmManager("sdcard/log/2015_08_06_17_54_44/alarm.txt");
        alarmManager.parse();
        List<AlarmInfo> list = alarmManager.getAlarmList();

        StringBuffer sb = new StringBuffer();
        sb.append(alarmManager.getElapsedTime()+"\n");

        Collections.sort(list);
        for(int i=0; i<list.size() - 1 ; i++){
            sb.append(list.get(i).toEasyString()).append("\n");
        }
        mWakeupTv.setText(sb.toString());

    }
}
