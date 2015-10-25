package com.yhh.analyser.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.core.monitor.Monitor;
import com.yhh.analyser.core.monitor.MonitorApp;
import com.yhh.analyser.core.monitor.MonitorAppDiy;
import com.yhh.analyser.core.monitor.MonitorException;
import com.yhh.analyser.provider.FloatCreator;
import com.yhh.analyser.core.monitor.MonitorShell;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.ScreenShot;
import com.yhh.analyser.view.activity.MonitorSysActivity;

/**
 * Created by yuanhh1 on 2015/8/14.
 */
public class MonitorService extends Service {
    private static final String TAG = ConstUtils.DEBUG_TAG + "ms";

    private Context mContext;

    /**
     * 浮窗体
     */
    private View viFloatingWindow;
    /**
     * 浮窗头
     */
    private View viFloatingTitle;

    /**
     * 记录浮窗显示内容
     */
    private TextView mFloatLv;

    /**
     * 记录当前浮窗颜色
     */
    private static int sColorIndex = 0;
    private int[] COLORS = new int[]{
            Color.GREEN, Color.YELLOW,
            Color.CYAN, Color.MAGENTA,
            Color.DKGRAY, Color.RED,
            Color.BLUE
    };

    /**
     * 浮窗生成器
     */
    private FloatCreator mFloatCreator;

    /**
     * 监控是否正在运行
     */
    public static boolean sMonitorIsRunning = false;


    /**
     * 监控器
     */
    private Monitor mMonitor;

    /**
     * 循环监控处理
     */
    private Handler handler;

    private TextView mFloatHideBtn;
    private TextView floatColorBtn;
    private TextView floatStopBtn;
    private TextView floatShotBtn;

    private TextView mShowAllTv;
    private RelativeLayout mFloatTitleRl;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mContext = this.getApplicationContext();
        mFloatCreator = new FloatCreator(mContext);
        handler = new Handler();
        initView();
        sMonitorIsRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        sMonitorIsRunning = true;
        mFloatCreator.createFloatingWindow(viFloatingTitle, viFloatingWindow);
        initNotification(startId);

        int type = intent.getIntExtra("type", 1);

        if (type == MonitorFactory.TYPE_APP) {
            int pid = intent.getIntExtra("pid", 0);
            mMonitor = new MonitorApp(mContext, pid);

        } else if (type == MonitorFactory.TYPE_APP_DIY) {
            int pid = intent.getIntExtra("pid", 0);
            Log.i(TAG,"pid=" + pid);
            mMonitor = new MonitorAppDiy(mContext, pid);

        } else if (type == MonitorFactory.TYPE_SHELL) {
            String commands = intent.getStringExtra("command");
            mMonitor = new MonitorShell(mContext, commands);
        } else if (type == MonitorFactory.TYPE_EXCEPTION) {
            int limit = intent.getIntExtra("limit", 38);
            mMonitor = new MonitorException(mContext, limit);
        } else {
            mMonitor = MonitorFactory.newInstance(mContext, type);
        }

        mMonitor.onStart();

        readPrefs();
        handler.post(task);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }


    private void readPrefs() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int interval = mPreferences.getInt(MonitorSysActivity.KEY_INTERVAL, 1);
        AppConfig.MONITOR_DELAY_TIME = interval * 1000;
    }

    private void initView() {

        viFloatingWindow = LayoutInflater.from(mContext).inflate(R.layout.app_monitor_floating, null);
        viFloatingTitle = LayoutInflater.from(mContext).inflate(R.layout.app_monitor_floating_title, null);
        mFloatLv = (TextView) viFloatingWindow.findViewById(R.id.float_items_lv);
        mFloatLv.setText(R.string.calculating);
        setFloatColor();

        mShowAllTv = (TextView) viFloatingTitle.findViewById(R.id.tv_show_all);
        mFloatTitleRl = (RelativeLayout) viFloatingTitle.findViewById(R.id.rl_float_title);

        mShowAllTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        hideFloat(false);
                        break;
                }
                return true;
            }
        });

        mFloatHideBtn = (TextView) viFloatingTitle.findViewById(R.id.float_hide_btn);
        mFloatHideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloat(true);
            }
        });


        floatStopBtn = (TextView) viFloatingTitle.findViewById(R.id.float_stop_btn);
        floatStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        floatColorBtn = (TextView) viFloatingTitle.findViewById(R.id.float_change_btn);
        floatColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoChangeFloatColor();
            }
        });

        floatShotBtn = (TextView) viFloatingTitle.findViewById(R.id.float_screenshot_btn);
        floatShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenShot.shoot(viFloatingWindow)) {
                    Toast.makeText(mContext, "截图成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MonitorService.this, "截图失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void initNotification(int startId) {
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
                new Intent(this, MonitorSysActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.logo1)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name));
        startForeground(startId, builder.build());
    }


    private void autoChangeFloatColor() {
        sColorIndex++;
        setFloatColor();
    }

    private void setFloatColor() {
        mFloatLv.setTextColor(COLORS[sColorIndex % 7]);
    }

    private void hideFloat(boolean isHide) {
        if (isHide) {
            viFloatingWindow.setVisibility(View.GONE);
//            mFloatTitleRl.setVisibility(View.GONE);

            mFloatHideBtn.setVisibility(View.GONE);
            floatColorBtn.setVisibility(View.GONE);
            floatStopBtn.setVisibility(View.GONE);
            floatShotBtn.setVisibility(View.GONE);


            mShowAllTv.setVisibility(View.VISIBLE);
        } else {
            viFloatingWindow.setVisibility(View.VISIBLE);
//            mFloatTitleRl.setVisibility(View.VISIBLE);
            mFloatHideBtn.setVisibility(View.VISIBLE);
            floatColorBtn.setVisibility(View.VISIBLE);
            floatStopBtn.setVisibility(View.VISIBLE);
            floatShotBtn.setVisibility(View.VISIBLE);

            mShowAllTv.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        sMonitorIsRunning = false;

        handler.removeCallbacks(task);
        mFloatCreator.removeView(viFloatingTitle, viFloatingWindow);
        mMonitor.onDestroy();
        stopForeground(true);

        super.onDestroy();
    }


    /**
     * 后台监控服务
     */
    private Runnable task = new Runnable() {

        public void run() {
            if (sMonitorIsRunning) {
                new MonitorTask().execute();
            } else {
                stopSelf();
            }

        }
    };

    class MonitorTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return mMonitor.monitor();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mFloatLv.setText(s);
            handler.postDelayed(task, AppConfig.MONITOR_DELAY_TIME);
        }
    }

}
