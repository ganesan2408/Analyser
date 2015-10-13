package com.yhh.analyser.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yhh.analyser.bean.AppLaunchBean;
import com.yhh.analyser.bean.AppLaunchList;
import com.yhh.analyser.view.activity.MonitorAppActivity;
import com.yhh.analyser.utils.DebugLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yuanhh1 on 2015/8/25.
 */
public class LaunchService extends Service {
    private static boolean isLaunching;

    private Handler mHandler;
    private String startActivity;

    private int currentCount;

    private AppLaunchList mAppLaunchList;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppLaunchList = new AppLaunchList();
        mHandler = new Handler();
        currentCount = 0;
        isLaunching = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startActivity  = intent.getStringExtra("startActivity");

        DebugLog.d("startActivity="+startActivity);

        mHandler.post(task);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        isLaunching = false;
        mHandler.removeCallbacks(task);
    }

    private Runnable task = new Runnable() {

        public void run() {
            if (checkOk()) {
                processStartTimeFromLogcat(startActivity);
                mHandler.postDelayed(task, 1000);
            }else{
                Intent intent = new Intent();
                intent.putExtra("launch", mAppLaunchList.toString());
                intent.setAction(MonitorAppActivity.ACTION);
                sendBroadcast(intent);

                stopSelf();
            }

        }
    };

    private boolean checkOk(){
        if(isLaunching && currentCount <10 && mAppLaunchList.getSize()<2) {
            return true;
        }
        return false;
    }

    @Deprecated
    public static void setLaunch(boolean b){
        isLaunching = b;
    }

    private void processStartTimeFromLogcat(String startActivity) {
        StringBuffer sb = new StringBuffer();

        String logcatCommand = "logcat -v time -d ActivityManager:I *:S";
        String regex = ".*Displayed.*" + startActivity + ".*ms.*";

        try {
            Process process = Runtime.getRuntime().exec(logcatCommand);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.matches(regex)) {
//                    line = "08-25 17:43:15.370 I/ActivityManager( 1085): Displayed com.baidu.searchbox_lenovo/com.baidu.searchbox.MainActivity: +767ms";
                    AppLaunchBean bean = getTargetInfo(line);
                    if(bean !=null ) {
                        if(mAppLaunchList.getSize()<= 0 || !mAppLaunchList.get(0).isEqual(bean)) {
                            mAppLaunchList.add(bean);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentCount ++;
    }

    private AppLaunchBean getTargetInfo(String line) {
        DebugLog.d("### " + line);

        StringBuffer sb = new StringBuffer();
        int startIndex = line.indexOf(":");
        if(startIndex < 2){
            DebugLog.e("startIndex="+startIndex);
            return null;
        }
        AppLaunchBean bean = new AppLaunchBean();
        bean.setCompleteTime(line.substring(startIndex-2, startIndex + 10));
        bean.setActivityName((line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(":"))));
        bean.setSpendTime((line.substring(line.lastIndexOf("+") + 1, line.lastIndexOf("ms") + 2)));

        return  bean;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
