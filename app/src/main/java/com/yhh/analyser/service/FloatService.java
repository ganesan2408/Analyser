package com.yhh.analyser.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.bean.InfoFactory;
import com.yhh.analyser.ui.MonitorSysActivity;
import com.yhh.analyser.R;
import com.yhh.analyser.provider.MonitorShell;
import com.yhh.analyser.ui.settings.SettingFloatingActivity;
import com.yhh.analyser.ui.settings.SettingMonitorActivity;
import com.yhh.analyser.ui.settings.SettingsActivity;
import com.yhh.analyser.provider.InfoManager;
import com.yhh.analyser.provider.FloatCreator;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.utils.DebugLog;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanhh1 on 2015/8/14.
 */
public class FloatService extends Service{

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

    /** 循环监控处理 */
    private Handler handler;

    /** 信息生成器*/
    private InfoManager infoAdmin;

    /** 资源*/
    private String[] itemTitles;
    private String[] itemUnitTitles;
    private boolean[] mIsFloatingItem = new boolean[SettingMonitorActivity.MONITOR_ITEMS_COUNT];
    private SharedPreferences mPreferences;
    HashMap<Integer,String> monitorInfo;


    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.d("on create");

        mContext = this.getApplicationContext();
        mFloatCreator = new FloatCreator(mContext);
        handler = new Handler();
        initView();

        readPrefs(); //读取资源

        sMonitorIsRunning = true;

        mShellMonitor = MonitorShell.newInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFloatCreator.createFloatingWindow(viFloatingTitle, viFloatingWindow);

        initNotification(startId);

        int pid = 0;
        try {
            pid = intent.getExtras().getInt("pid");
        }catch (Exception e){

        }
        DebugLog.d("pid========="+pid);
        infoAdmin = new InfoManager(getBaseContext(), pid);
        infoAdmin.createMonitorFile();
        infoAdmin.writeTitle2File();

        handler.post(task);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void readPrefs(){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for(int i=0;i<13;i++){
            mIsFloatingItem[i] = !mPreferences.getBoolean(SettingFloatingActivity.PREF_FLOATING_ITEMS[i], false);
        }

        int interval = mPreferences.getInt(SettingsActivity.KEY_INTERVAL, 1);
        AppConfig.MONITOR_DELAY_TIME = interval * 1000;
    }

    private void initView() {
        itemTitles = this.getResources().getStringArray(R.array.monitor_items);
        itemUnitTitles = this.getResources().getStringArray(R.array.monitor_unit_items);

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
                Toast.makeText(mContext, "截图", Toast.LENGTH_SHORT).show();
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
        if (mContext.getString(R.string.float_unfold).equals(mFloatHideBtn.getText())) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        sMonitorIsRunning = false;

        handler.removeCallbacks(task);
        mFloatCreator.removeView(viFloatingTitle, viFloatingWindow);
        infoAdmin.closeOpenedStream();
        stopForeground(true);
        InfoFactory.getInstance().destory();

        super.onDestroy();
    }

    /**
     * 后台计算过程
     *
     */
    private Runnable task = new Runnable() {

        public void run() {
            if (!sMonitorIsRunning) {
                stopSelf();
                return;
            }
            if(AppConfig.TYPE ==3){
                topTask();
            }else {
                dataRefresh();
                handler.postDelayed(task, AppConfig.MONITOR_DELAY_TIME);
            }
        }
    };

    /**
     * 数据刷新
     */
    private void dataRefresh(){
        if(!mIsHide) {
            if(AppConfig.TYPE==7 ||AppConfig.TYPE==9){
                monitorInfo = infoAdmin.admin();
                StringBuffer mFloatContent = new StringBuffer();
                for(int i=0;i< itemTitles.length-1;i++){
                    if(mIsFloatingItem[i]){
                        mFloatContent.append(itemTitles[i]+": ");
                        mFloatContent.append(monitorInfo.get(i));
                        mFloatContent.append(itemUnitTitles[i]+"\n");
                    }
                }
                if(mIsFloatingItem[12]){
                    mFloatContent.append(itemTitles[12]+": ");
                    mFloatContent.append(monitorInfo.get(MonitorConst.TRAFFIC_SEND_SPEED)+ "/"+
                            monitorInfo.get(MonitorConst.TRAFFIC_REV_SPEED));
                    mFloatContent.append(itemUnitTitles[12]);
                }
                mFloatLv.setText(mFloatContent.toString().trim());
            }else {
                monitorInfo = infoAdmin.admin(AppConfig.TYPE);
                mFloatLv.setText(getReflashData(AppConfig.TYPE));
            }
        }
    }

    // 临时方案
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private MonitorShell mShellMonitor;
    private  String adbRes="";

    private void topTask(){
        if(sMonitorIsRunning){
            singleThreadExecutor.execute(new Runnable(){
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    adbRes = mShellMonitor.execTop(8);
                    mShellHandler.sendMessage(mShellHandler.obtainMessage());
                }
            });
        }
    }

    private Handler mShellHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(!mIsHide) {
                mFloatLv.setText(adbRes);
            }
            topTask();
        };
    };



    public static final int CPU_TYPE = 0;
    public static final int GPU_TYPE = 1;
    public static final int MEMORY_TYPE = 2;
    public static final int TOP_TYPE = 3;
    public static final int CURRENT_TYPE = 4;
    public static final int BATTERY_TYPE = 5;
    public static final int EXCEPTION_TYPE = 6;
    public static final int DIY_TYPE = 7;
    public static final int ALL_TYPE = 8;
    public static final int CHOOSE_TYPE = 9;

    public static final int APP_TYPE = 11;

    private String getReflashData(int type){
        switch (type){
            case APP_TYPE:
                return getTargetString(3);

            case CPU_TYPE:
                return getTargetString(1,2);

            case MEMORY_TYPE:
                return getTargetString(4);

            case GPU_TYPE:
                return  getTargetString(5,6);

            case TOP_TYPE:
            return getTargetString();

            case CURRENT_TYPE:
            return getTargetString(7,8);

            case BATTERY_TYPE:
            return getTargetString(9,10,11);

            case ALL_TYPE:
            return getTargetString(0,1,2,3,4,5,6,7,8,9,10,11,12);

        }
        return "";
    }

    private String getTargetString(int... index){
        StringBuffer sb = new StringBuffer();
        for(int i:index) {
            sb.append(itemTitles[i]+": ");
            sb.append(monitorInfo.get(i));
            sb.append(itemUnitTitles[i]+"\n");
        }
        return  sb.toString();
    }
}
