/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.yhh.analyser.R;
import com.yhh.app.setttings.SettingsActivity;
import com.yhh.service.MonitorService;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysMonitorActivity extends BaseActivity {
    
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SysMonitor";

	private Intent monitorService;

    private GridView mGridView;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
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
		initUI();
        getData();
        initGridView();

	}

    private void initGridView(){
        String[] from = {"text"};
        int[] to = {R.id.text};
        mAdapter = new SimpleAdapter(this, mDataList, R.layout.sys_monitor_item, from, to);

        mGridView = (GridView) findViewById(R.id.monitor_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }

        });
    }

    public List<Map<String, Object>> getData(){
        for(int i=0;i<mMonitorItems.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", mMonitorItems[i]);
            mDataList.add(map);
        }
        return mDataList;
    }
	
    private void initUI(){
//       mMonitorBtn.setOnClickListener(new OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               if (getString(R.string.start_monitor).equals(mMonitorBtn.getText().toString())) {
//                   if (!mAppInfo.getName().equals("-1") && !mAppInfo.getName().equals("系统监控")) {
//                       Toast.makeText(AppMonitorActivity.this, mAppInfo.getName() + "启动中", Toast.LENGTH_SHORT).show();
//                       Intent intent = getPackageManager().getLaunchIntentForPackage(mAppInfo.getPackageName());
//                       try {
//                           mStartActivity = intent.resolveActivity(getPackageManager()).getShortClassName();
//                           startActivity(intent);
//                       } catch (Exception e) {
//                           Toast.makeText(AppMonitorActivity.this, getString(R.string.can_not_start_app_toast), Toast.LENGTH_SHORT).show();
//                           return;
//                       }
//                       new Thread(new Runnable() {
//
//                           @Override
//                           public void run() {
//                               waitForAppStart(mAppInfo.getPackageName());
//                           }
//
//                       }).start();
//                   } else {
//                       monitorService.putExtra("appName", -1);
//                       monitorService.putExtra("pid", -1);
//                       monitorService.putExtra("uid", -1);
//                       monitorService.putExtra("packageName", -1);
//                       monitorService.putExtra("startActivity", -1);
//
//                       monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                       monitorService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                       startService(monitorService);
//                   }
//                   MonitorService.isServiceStoped = false;
//                   mMonitorBtn.setText(getString(R.string.stop_monitor));
//
//               } else {
//                   MonitorService.isServiceStoped = true;
//                   mMonitorBtn.setText(getString(R.string.start_monitor));
//                   stopService(monitorService);
//               }
//           }
//       });
//
//       mViewMonitorBtn.setOnClickListener(new OnClickListener() {
//
//           @Override
//           public void onClick(View v) {
//               showMonitorDataDialog();
//           }
//       });
	}

	Handler mHandler = new Handler(){
	    public void handleMessage(android.os.Message msg) {
            Intent monitorService = new Intent();
            monitorService.setClass(SysMonitorActivity.this, MonitorService.class);
	        if(msg.what ==0x1){
                getActionBar().setTitle(getResources().getString(R.string.main_app_analyser));
                DialogUtils.closeLoading();
	        }else if(msg.what ==0x2){  //start floating windows
	            Log.d(TAG,"begin startup float window.");
	            monitorService.putExtra("appName", "");
                monitorService.putExtra("pid",  "");
                monitorService.putExtra("uid",  "");
                monitorService.putExtra("packageName", "");
                monitorService.putExtra("startActivity", "");
                startService(monitorService);
	        }
	    };
	};
	


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
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }
}
