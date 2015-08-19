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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.core.Monitor;
import com.yhh.analyser.core.MonitorApp;
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.provider.FloatCreator;
import com.yhh.analyser.ui.MonitorSysActivity;
import com.yhh.analyser.ui.settings.SettingsActivity;
import com.yhh.analyser.utils.ScreenShot;

/**
 * Created by yuanhh1 on 2015/8/14.
 */
public class MonitorSysService extends Service{

    private Context mContext;

    /** 浮窗体*/
    private View viFloatingWindow;
    /** 浮窗头*/
    private View viFloatingTitle;

    /** 记录浮窗显示内容*/
    private TextView mFloatLv;

    /** 是否隐藏浮窗体*/
    private Button mFloatHideBtn;

    /**记录当前浮窗颜色 */
    private static  int sColorIndex =0;
    private int[] COLORS = new int[]{
            Color.GREEN, Color.YELLOW,
            Color.CYAN, Color.MAGENTA,
            Color.DKGRAY, Color.RED,
            Color.BLUE
    };

    /**浮窗生成器 */
    private FloatCreator mFloatCreator;

    /** 监控是否正在运行*/
    public static boolean sMonitorIsRunning = false;

    /** 是否隐藏浮窗*/
    private boolean mIsHide;

    /** 监控器*/
    private Monitor mMonitor;

    /** 循环监控处理 */
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        mFloatCreator = new FloatCreator(mContext);
        handler = new Handler();
        initView();

        sMonitorIsRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFloatCreator.createFloatingWindow(viFloatingTitle, viFloatingWindow);
        initNotification(startId);

        int type = intent.getIntExtra("type",1);
        if(type==20){
            int pid = intent.getIntExtra("pid",0);
            mMonitor = new MonitorApp(mContext, pid);
        }else {
            mMonitor = MonitorFactory.newInstance(mContext, type);
        }
        readPrefs();
        handler.post(task);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void readPrefs(){
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int interval = mPreferences.getInt(SettingsActivity.KEY_INTERVAL, 1);
        AppConfig.MONITOR_DELAY_TIME = interval * 1000;
    }

    private void initView() {

        viFloatingWindow = LayoutInflater.from(mContext).inflate(R.layout.app_monitor_floating, null);
        viFloatingTitle = LayoutInflater.from(mContext).inflate(R.layout.app_monitor_floating_title, null);
        mFloatLv = (TextView) viFloatingWindow.findViewById(R.id.float_items_lv);
        mFloatLv.setText(R.string.calculating);
        setFloatColor();

        mFloatHideBtn = (Button) viFloatingTitle.findViewById(R.id.float_hide_btn);
        mFloatHideBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hideFloat();
            }
        });


        Button floatStopBtn = (Button) viFloatingTitle.findViewById(R.id.float_stop_btn);
        floatStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        Button floatColorBtn = (Button) viFloatingTitle.findViewById(R.id.float_change_btn);
        floatColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoChangeFloatColor();
            }
        });

        Button floatShotBtn = (Button) viFloatingTitle.findViewById(R.id.float_screenshot_btn);
        floatShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ScreenShot.shoot(viFloatingWindow)) {
                    Toast.makeText(mContext, "截图成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MonitorSysService.this, "截图失败", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void initNotification(int startId){
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
                new Intent(this, MonitorSysActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.logo1)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name));
        startForeground(startId, builder.build());
    }


    private void autoChangeFloatColor(){
        sColorIndex++;
        setFloatColor();
    }

    private void setFloatColor(){
        mFloatLv.setTextColor(COLORS[sColorIndex%7]);
    }

    private void hideFloat(){
        if (isHide()) {
            mIsHide = false;
            mFloatHideBtn.setText(mContext.getString(R.string.float_fold));
            viFloatingWindow.setVisibility(View.VISIBLE);

        }else{
            mIsHide = true;
            mFloatHideBtn.setText(mContext.getString(R.string.float_unfold));
            viFloatingWindow.setVisibility(View.GONE);
        }
    }

    private boolean isHide(){
        return mContext.getString(R.string.float_unfold).equals(mFloatHideBtn.getText());
    }

    @Override
    public void onDestroy() {
        sMonitorIsRunning = false;

        handler.removeCallbacks(task);
        mFloatCreator.removeView(viFloatingTitle, viFloatingWindow);
        mMonitor.onDestory();
        stopForeground(true);

        super.onDestroy();
    }

    /**
     * 后台计算过程
     *
     */
    private Runnable task = new Runnable() {

        public void run() {
            if (sMonitorIsRunning) {
                new MonitorTask().execute();
            }else{
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
