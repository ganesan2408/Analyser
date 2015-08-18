/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.ui.settings.SettingExcptionActivity;
import com.yhh.analyser.ui.settings.SettingMonitorActivity;
import com.yhh.analyser.ui.settings.SettingShellActivity;
import com.yhh.analyser.ui.settings.SettingsActivity;
import com.yhh.analyser.service.FloatService;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.utils.DebugLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorSysActivity extends BaseActivity {
    
    private GridView mGridView;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private final String[] mMonitorItems = new String[]{
            "CPU", "GPU",
            "内存", "Top",
            "电流", "电池",
            "异常监控", "自定义监控",
            "全监控" , "可选监控"
    };


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_detailed_info);
	    
		initActionBar();
        initGridView();

	}

    private void initGridView(){
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
                if(!FloatService.sMonitorIsRunning) {
                    if(position==7){
                        Intent diy = new Intent(mContext, SettingShellActivity.class);
                        startActivity(diy);
                        return;
                    }else if(position==9){
                        Intent diy = new Intent(mContext, SettingMonitorActivity.class);
                        startActivity(diy);
                        return;
                    }else if(position==6){
                        Intent diy = new Intent(mContext, SettingExcptionActivity.class);
                        startActivity(diy);
                        return;
                    }

                    DebugLog.d("startup monitor");
                    Toast.makeText(mContext,"启动监控", Toast.LENGTH_SHORT).show();

                    AppConfig.TYPE = position;
                    Intent monitorService = new Intent();
                    monitorService.setClass(mContext, FloatService.class);
                    monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    monitorService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startService(monitorService);

                }else{
                    Toast.makeText(mContext, "监控已启动,请勿重复开启", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void setData(){
        for(String item:mMonitorItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("text", item);
            mDataList.add(map);
        }
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.menu_settings){
            Intent monitorIntent = new Intent(this, SettingsActivity.class);
            startActivity(monitorIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    
    @SuppressLint("NewApi")
    private void initActionBar(){
        ActionBar bar = getActionBar();
        if(bar !=null) {
            bar.setHomeButtonEnabled(true);
            bar.setIcon(R.drawable.nav_back);
        }
    }
}
