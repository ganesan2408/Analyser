/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.bean.app.AppInfo;
import com.yhh.analyser.bean.app.ProcessInfo;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.service.MonitorSysService;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.ui.settings.SettingMonitorActivity;
import com.yhh.analyser.ui.settings.SettingsActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DialogUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorAppActivity extends BaseActivity {
    
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SingleAppMonitor";

	private Intent monitorService;

	private ImageView mAppLogIv;
	private TextView mAppNameTv;
	private TextView mPkgNameTv;
	private TextView mVersionTv;
	private TextView mPidTv;
	private TextView mUidTv;
	
	private ProcessInfo mProcessInfo = new ProcessInfo();
	private AppInfo mAppInfo;
	private String mStartActivity;
	
	private static final int TIMEOUT = 10000;
	private int selectedFileIndex; //查看统计数据的索引
    public static final String MONITOR_PATH = "monitor_path";


    private GridView mGridView;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
    private final String[] mMonitorItems = new String[]{
            "APP监控", "可选监控"
    };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_detailed_info);
	    
        getAppInfo();
		initUI();
        getData();
        initGridView();
	}
	
	private void getAppInfo(){
	    mAppInfo = new AppInfo();
	    String appName = getIntent().getStringExtra("appName");
	    String pkgName = getIntent().getStringExtra("packageName");
	    mAppInfo.setName(appName);
	    mAppInfo.setPackageName(pkgName);

        mProcessInfo.getRunningApp(this, mAppInfo);

	    AppInfo pkgInfo  = mProcessInfo.getPackageInfo(this, mAppInfo.getPackageName());
	    mAppInfo.setVersionName(pkgInfo.getVersionName());
	    mAppInfo.setLogo(pkgInfo.getLogo());
	    
	}

    private void initGridView(){
        String[] from = {"text"};
        int[] to = {R.id.text};
        mAdapter = new SimpleAdapter(this, mDataList, R.layout.main_monitor_item, from, to);

        mGridView = (GridView) findViewById(R.id.monitor_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    Toast.makeText(MonitorAppActivity.this, mAppInfo.getName() + "启动中", Toast.LENGTH_SHORT).show();
                    Intent intent = getPackageManager().getLaunchIntentForPackage(mAppInfo.getPackageName());
                       try {
                           mStartActivity = intent.resolveActivity(getPackageManager()).getShortClassName();
                           startActivity(intent);
                       } catch (Exception e) {
                           Toast.makeText(MonitorAppActivity.this, getString(R.string.can_not_start_app_toast), Toast.LENGTH_SHORT).show();
                           return;
                       }
                       new Thread(new Runnable() {

                           @Override
                           public void run() {
                               waitForAppStart(mAppInfo.getPackageName());
                           }

                       }).start();


                } else {
                    Intent diy = new Intent(mContext, SettingMonitorActivity.class);
                    startActivity(diy);
                }
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
	   monitorService = new Intent();
       monitorService.setClass(MonitorAppActivity.this, MonitorSysService.class);
	   
//       mMonitorBtn = (Button) findViewById(R.id.app_monitor_btn);
//       mViewMonitorBtn = (Button) findViewById(R.id.app_monitor_view);
       mAppLogIv = (ImageView) findViewById(R.id.app_logo_view);
       mAppNameTv = (TextView) findViewById(R.id.app_name);
       mPkgNameTv = (TextView) findViewById(R.id.app_pkg_value);
       mVersionTv = (TextView) findViewById(R.id.app_version_value);
       mPidTv = (TextView) findViewById(R.id.app_pid_value);
       mUidTv = (TextView) findViewById(R.id.app_uid_value);


       setView();

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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setView(){
        String appName = mAppInfo.getName();
        mAppNameTv.setText(appName);

        if(!appName.equals(getResources().getString(R.string.system_analyzer))){
            mAppLogIv.setBackground(mAppInfo.getLogo());
            mPkgNameTv.setText(mAppInfo.getPackageName());
            mVersionTv.setText(mAppInfo.getVersionName());
            if(mAppInfo.getPid() ==0) {
                mPidTv.setText(R.string.status_default);
            }else{
                mPidTv.setText(mAppInfo.getPid()+"");
            }

            if(mAppInfo.getUid() ==0){
                mUidTv.setText(R.string.status_default);
            }else{
                mUidTv.setText(mAppInfo.getUid()+"");
            }
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
//		if (MonitorService.isServiceStoped) {
//            mMonitorBtn.setText(getString(R.string.start_monitor));
//		}else{
//		    mMonitorBtn.setText(getString(R.string.stop_monitor));
//		}
	}
	
	Handler mHandler = new Handler(){
	    public void handleMessage(android.os.Message msg) {
	        if(msg.what ==0x1){
                getActionBar().setTitle(getResources().getString(R.string.main_app_analyser));
                DialogUtils.closeLoading();
	        }else if(msg.what ==0x2){  //start floating windows
	            Log.d(TAG, "begin startup float window.");
                AppConfig.TYPE = 11;
	            monitorService.putExtra("appName", mAppInfo.getName());
                monitorService.putExtra("pid", mAppInfo.getPid());
                monitorService.putExtra("uid", mAppInfo.getUid());
                monitorService.putExtra("packageName", mAppInfo.getPackageName());
                monitorService.putExtra("startActivity", mStartActivity);
                startService(monitorService);
	        }
	    }
    };
	
	 /**
     * wait for monitor application started.
     * 
     * @param packageName
     *            package name of monitor application
     */
    private void waitForAppStart(String packageName) {
        Log.d(TAG, "wait for app start");
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() < startTime + TIMEOUT) {
            mProcessInfo.getRunningPackage(this, mAppInfo);
            if(mAppInfo.getPid() != 0){
                break;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(0x2));
    }
	
	private void goToSettingsActivity() {
		Intent intent = new Intent();
		intent.setClass(MonitorAppActivity.this, SettingsActivity.class);
		startActivityForResult(intent, Activity.RESULT_FIRST_USER);
	}

	
    public void showMonitorDataDialog(){
        final String[] files = listMonitorFiles();
        
        new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_choose_title)
        .setSingleChoiceItems(files, -1, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedFileIndex = which;
            }
        })
        .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        })
        .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"selectedFileIndex="+selectedFileIndex);
                if(files==null || files.length <1){
                    return;
                }
                Intent intent = new Intent(MonitorAppActivity.this, ChartMonitorActivity.class);
                intent.putExtra(MONITOR_PATH, files[selectedFileIndex]);
                startActivity(intent);
            }
        }).show();
    }
    
    private String[] listMonitorFiles(){
        File parentDir = new File(AppConfig.MONITOR_DIR);
        String[] files = parentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File current = new File(dir, filename);
                return current.isFile();
            }
        });
        if(files ==null || files.length <=0){
            return null;
        }
        Arrays.sort(files, Collections.reverseOrder());
        return files;
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_settings){
            Intent monitorIntent = new Intent(this, SettingsActivity.class);
            startActivity(monitorIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    
}
