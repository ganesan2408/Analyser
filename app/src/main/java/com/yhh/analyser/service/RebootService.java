package com.yhh.analyser.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

import com.yhh.analyser.utils.RootUtils;

public class RebootService extends Service {
    private long delay;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initRebootTime();
        delayReboot();
        return super.onStartCommand(intent, flags, startId);
    }

    public void delayReboot(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reboot();
            }
        }, delay);
    }

    private void initRebootTime(){
        String delayTime = RootUtils.getInstance().getSystemProperty(RootUtils.REBOOT_TIME_KEY);
        try {
            delay = Long.parseLong(delayTime);
        }catch (Exception e){
            delay = 3000L;
        }
    }

    private void reboot(){
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        pm.reboot("reboot by APK");
    }
}
