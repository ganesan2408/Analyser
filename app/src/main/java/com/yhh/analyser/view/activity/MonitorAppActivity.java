/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.model.app.AppInfo;
import com.yhh.analyser.model.app.ProcessInfo;
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.service.LaunchService;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.utils.DialogUtils;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.androidutils.AppUtils;
import com.yhh.androidutils.StringUtils;
import com.yhh.androidutils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorAppActivity extends BaseActivity {

    private static final String TAG = LogUtils.DEBUG_TAG + "AppMonitorA";

//    private Intent monitorService;

    private ImageView mAppLogIv;
    private TextView mAppNameTv;
    private TextView mPkgNameTv;
    private TextView mVersionTv;
    private TextView mPidTv;
    private TextView mUidTv;
    private TextView mLaunchTv;

    private ProcessInfo mProcessInfo = new ProcessInfo();
    private AppInfo mAppInfo;
//    private String mStartActivity;

    private static final int TIMEOUT = 10000;
//    private int selectedFileIndex; //查看统计数据的索引
    public static final String MONITOR_PATH = "monitor_path";

    private static final int PREPARE_COMPLETE = 1;
    private static final int START_FLOAT_WINDOW = 2;


    private GridView mGridView;
    private SimpleAdapter mAdapter;
    private List<Map<String, Object>> mDataList = new ArrayList<>();
    private final String[] mMonitorItems = new String[]{
            "Launch监控", "APP监控", "可选监控"
    };

    private MyReceiver mMyReceiver;
    private String mClickTime;

    public static String ACTION = "com.yhh.app.launchService";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_detailed_info);

        getAppInfo();
        initUI();
        getData();
        initGridView();

        mMyReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        mContext.registerReceiver(mMyReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LaunchService.setLaunch(false);
    }

    private void getAppInfo() {
        mAppInfo = new AppInfo();
        String appName = getIntent().getStringExtra("appName");
        String pkgName = getIntent().getStringExtra("packageName");
        mAppInfo.setName(appName);
        mAppInfo.setPackageName(pkgName);

        mProcessInfo.getRunningInfo(this, mAppInfo);

        AppInfo pkgInfo = mProcessInfo.getPackageInfo(this, mAppInfo.getPackageName());
        mAppInfo.setVersionName(pkgInfo.getVersionName());
        mAppInfo.setLogo(pkgInfo.getLogo());

    }

    private void initGridView() {
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
                    Intent intent = getPackageManager().getLaunchIntentForPackage(mAppInfo.getPackageName());
//                    mStartActivity = intent.resolveActivity(getPackageManager()).getShortClassName();
                    startActivity(intent);

                    Intent sintent = new Intent(mContext, LaunchService.class);
                    sintent.putExtra("startActivity", mAppInfo.getPackageName());
                    startService(sintent);
                    mClickTime = TimeUtils.getCurrentTime(TimeUtils.DATA_MS_FORMAT) + " 点击";

                } else if (position == 1) {
                    Toast.makeText(MonitorAppActivity.this, mAppInfo.getName() + "启动中", Toast.LENGTH_SHORT).show();
                    Intent intent = getPackageManager().getLaunchIntentForPackage(mAppInfo.getPackageName());
//                    mStartActivity = intent.resolveActivity(getPackageManager()).getShortClassName();
                    startActivity(intent);

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                waitForAppStart(mAppInfo.getPackageName());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();


                } else {
                    Intent diy = new Intent(mContext, MonitorDiyActivity.class);
                    diy.putExtra("type", MonitorFactory.TYPE_APP_DIY);
                    diy.putExtra("pkgname", mAppInfo.getPackageName());

                    startActivity(diy);
                }
            }

        });
    }

    public List<Map<String, Object>> getData() {
        for (int i = 0; i < mMonitorItems.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", mMonitorItems[i]);
            mDataList.add(map);
        }
        return mDataList;
    }

    private void initUI() {

        mAppLogIv = (ImageView) findViewById(R.id.app_logo_view);
        mAppNameTv = (TextView) findViewById(R.id.app_name);
        mPkgNameTv = (TextView) findViewById(R.id.app_pkg_value);
        mVersionTv = (TextView) findViewById(R.id.app_version_value);
        mPidTv = (TextView) findViewById(R.id.app_pid_value);
        mUidTv = (TextView) findViewById(R.id.app_uid_value);
        mLaunchTv = (TextView) findViewById(R.id.tv_launch_info);

        setView();

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setView() {
        String appName = mAppInfo.getName();
        mAppNameTv.setText(appName);

        if (!appName.equals(getResources().getString(R.string.system_analyzer))) {
            mAppLogIv.setBackground(mAppInfo.getLogo());
            mPkgNameTv.setText(mAppInfo.getPackageName());
            mVersionTv.setText(mAppInfo.getVersionName());
            if (mAppInfo.getPid() == 0) {
                mPidTv.setText(R.string.status_default);
            } else {
                mPidTv.setText(mAppInfo.getPid() + "");
            }

            if (mAppInfo.getUid() == 0) {
                mUidTv.setText(R.string.status_default);
            } else {
                mUidTv.setText(mAppInfo.getUid() + "");
            }
        }
    }


    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == PREPARE_COMPLETE) {
                getActionBar().setTitle(getResources().getString(R.string.main_app_analyser));
                DialogUtils.closeLoading();

            } else if (msg.what == START_FLOAT_WINDOW) {
                Intent monitorService = new Intent(MonitorAppActivity.this, MonitorService.class);
                monitorService.putExtra("type", MonitorFactory.TYPE_APP);
                monitorService.putExtra("pid", (int) msg.obj);
                startService(monitorService);
                finish();
            }
        }
    };

    /**
     * wait for monitor application started.
     *
     * @param packageName package name of monitor application
     */
    private void waitForAppStart(String packageName) throws InterruptedException {
        Log.d(TAG, "wait for app start");
        long startTime = System.currentTimeMillis();

        int pid = 0;
        while (System.currentTimeMillis() < startTime + TIMEOUT) {
            Thread.sleep(100);
            pid = mProcessInfo.getApkPid(this, packageName);
            if (pid > 0) {
                Log.i(TAG,"waitForAppStart pid="+pid);
                break;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(START_FLOAT_WINDOW, pid));
    }



//    public void showMonitorDataDialog() {
//        final String[] files = listMonitorFiles();
//
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.dialog_choose_title)
//                .setSingleChoiceItems(files, -1, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        selectedFileIndex = which;
//                    }
//                })
//                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i(TAG, "selectedFileIndex=" + selectedFileIndex);
//                        if (files == null || files.length < 1) {
//                            return;
//                        }
//                        Intent intent = new Intent(MonitorAppActivity.this, ChartMonitorActivity.class);
//                        intent.putExtra(MONITOR_PATH, files[selectedFileIndex]);
//                        startActivity(intent);
//                    }
//                }).show();
//    }

//    private String[] listMonitorFiles() {
//        File parentDir = new File(AppConfig.MONITOR_DIR);
//        String[] files = parentDir.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String filename) {
//                File current = new File(dir, filename);
//                return current.isFile();
//            }
//        });
//        if (files == null || files.length <= 0) {
//            return null;
//        }
//        Arrays.sort(files, Collections.reverseOrder());
//        return files;
//    }

    @Override
    protected void onDestroy() {
        if (mMyReceiver != null) {
            mContext.unregisterReceiver(mMyReceiver);
        }

        super.onDestroy();
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getExtras().getString("launch");
            if (!StringUtils.isBlank(str)) {
                mLaunchTv.setText(mClickTime + "\n" + str);
            } else {
                mLaunchTv.setText("启动时间过长");
            }

            AppUtils.stopActivity(mAppInfo.getName());
        }
    }

}
