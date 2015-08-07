/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.monitor;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.Main;
import com.yhh.analyser.R;
import com.yhh.app.analyser.AppChartAnalyser;
import com.yhh.app.setttings.SettingsActivity;
import com.yhh.info.app.AppInfo;
import com.yhh.info.app.ProcessInfo;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;

public class SingleAppMonitor extends Activity {
    
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "SingleAppMonitor";
    
	private Intent monitorService;
	private UpdateReceiver receiver;
	
	private Button mMonitorBtn;
	private Button mViewMonitorBtn;
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_detailed_info);
	    
	     getAppInfo();
		initActionBar();
		initUI();
		
		receiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MonitorService.MONITOR_SERVICE_ACTION);
		registerReceiver(receiver, filter);
	}
	
	private void getAppInfo(){
	    mAppInfo = new AppInfo();
	    String appName = getIntent().getStringExtra("appName");
	    String pkgName = getIntent().getStringExtra("packageName");
	    mAppInfo.setName(appName);
	    mAppInfo.setPackageName(pkgName);

	    if(pkgName !=null){
	        AppInfo tmpInfo  = mProcessInfo.getRunningApp(this, mAppInfo.getPackageName());
	        mAppInfo.setPid(tmpInfo.getPid());
	        mAppInfo.setUid(tmpInfo.getUid());
	    }
	    
	    AppInfo pkgInfo  = mProcessInfo.getPackageInfo(this, mAppInfo.getPackageName());
	    mAppInfo.setVersionName(pkgInfo.getVersionName());
	    mAppInfo.setLogo(pkgInfo.getLogo());
	    
	}
	
	@SuppressLint("NewApi")
    private void initUI(){
	   monitorService = new Intent();
       monitorService.setClass(SingleAppMonitor.this, MonitorService.class);
	   
       mMonitorBtn = (Button) findViewById(R.id.app_monitor_btn);
       mViewMonitorBtn = (Button) findViewById(R.id.app_monitor_view);
       mAppLogIv = (ImageView) findViewById(R.id.app_logo_view);
       mAppNameTv = (TextView) findViewById(R.id.app_name);
       mPkgNameTv = (TextView) findViewById(R.id.app_pkg_value);
       mVersionTv = (TextView) findViewById(R.id.app_version_value);
       mPidTv = (TextView) findViewById(R.id.app_pid_value);
       mUidTv = (TextView) findViewById(R.id.app_uid_value);
       
       mAppLogIv.setBackground(mAppInfo.getLogo());
       mAppNameTv.setText(mAppInfo.getName());
       mPkgNameTv.setText(mAppInfo.getPackageName());
       mVersionTv.setText(mAppInfo.getVersionName());
       mPidTv.setText(mAppInfo.getPid()+"");
       mUidTv.setText(mAppInfo.getUid()+"");
       
       
       mMonitorBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.start_monitor).equals(mMonitorBtn.getText().toString())) {
                    if (!mAppInfo.getName().equals("-1") && !mAppInfo.getName().equals("系统监控")) {
                        Toast.makeText(SingleAppMonitor.this, mAppInfo.getName()+"启动中", Toast.LENGTH_SHORT).show();
                        Intent intent = getPackageManager().getLaunchIntentForPackage(mAppInfo.getPackageName());
                        try {
                            mStartActivity = intent.resolveActivity(getPackageManager()).getShortClassName();
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(SingleAppMonitor.this, getString(R.string.can_not_start_app_toast), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Thread(new Runnable(){

                            @Override
                            public void run() {
                                waitForAppStart(mAppInfo.getPackageName());
                            }
                            
                        }).start();
                    } else {
                        monitorService.putExtra("appName", -1);
                        monitorService.putExtra("pid", -1);
                        monitorService.putExtra("uid", -1);
                        monitorService.putExtra("packageName", -1);
                        monitorService.putExtra("startActivity", -1);
                        
                        monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        monitorService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startService(monitorService);
                    }
                    MonitorService.isServiceStoped = false;
                    mMonitorBtn.setText(getString(R.string.stop_monitor));
                    
                } else {
                    MonitorService.isServiceStoped = true;
                    mMonitorBtn.setText(getString(R.string.start_monitor));
                    stopService(monitorService);
                }
            }
        });
       
       mViewMonitorBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                showMonitorDataDialog();
            }
       });
	}
	

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (MonitorService.isServiceStoped) {
            mMonitorBtn.setText(getString(R.string.start_monitor));
		}else{
		    mMonitorBtn.setText(getString(R.string.stop_monitor));
		}
	}
	
	Handler mHandler = new Handler(){
	    public void handleMessage(android.os.Message msg) {
	        if(msg.what ==0x1){
                getActionBar().setTitle(getResources().getString(R.string.main_app_analyser));
                DialogUtils.closeLoading();
	        }else if(msg.what ==0x2){  //start floating windows
	            Log.d(TAG,"begin startup float window.");
	            monitorService.putExtra("appName", mAppInfo.getName());
                monitorService.putExtra("pid", mAppInfo.getPid());
                monitorService.putExtra("uid", mAppInfo.getUid());
                monitorService.putExtra("packageName", mAppInfo.getPackageName());
                monitorService.putExtra("startActivity", mStartActivity);
                startService(monitorService);
	        }
	    };
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
        
        AppInfo appInfo = null;
        while (System.currentTimeMillis() < startTime + TIMEOUT) {
            appInfo = mProcessInfo.getRunningApp(this, packageName);
            if(appInfo.getPid() != 0){
                mAppInfo.setPid(appInfo.getPid());
                mAppInfo.setUid(appInfo.getUid());
                break;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(0x2));
    }
	
	private void goToSettingsActivity() {
		Intent intent = new Intent();
		intent.setClass(SingleAppMonitor.this, SettingsActivity.class);
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
                Intent intent = new Intent(SingleAppMonitor.this, AppChartAnalyser.class);
                intent.putExtra(MONITOR_PATH, files[selectedFileIndex]);
                startActivity(intent);
            }
        }).show();
    }
    
    private String[] listMonitorFiles(){
        File parentDir = new File(Main.MONITOR_PARENT_PATH);
        String[] files = parentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File current = new File(dir, filename);
                if (current.isFile()) {
                    return true;
                }
                return false;
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
		unregisterReceiver(receiver);
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
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }
    
    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MonitorService.isServiceStoped) {
                mMonitorBtn.setText(getString(R.string.start_monitor));
            }
        }
    }
}
