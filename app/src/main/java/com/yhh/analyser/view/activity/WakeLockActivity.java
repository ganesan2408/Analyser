package com.yhh.analyser.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;

public class WakeLockActivity extends BaseActivity {
    
    private static final String tag = "WK";
    private WakeLock wakeLock;
    private PowerManager pm;
    
    private TextView mStatusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakelock);

        pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mStatusTv = (TextView) findViewById(R.id.status);
    }
    
    @SuppressLint("Wakelock")
    public void ClickHandler(View v){
        if(wakeLock !=null){
            wakeLock.release();
            wakeLock =null;
        }
        switch(v.getId()){
            case    R.id.partical_wake_btn:
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
                wakeLock.acquire();
                mStatusTv.setText(R.string.partical_status);
                break;
                
            case    R.id.screen_wake_btn:
                wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, tag);
                wakeLock.acquire();
                mStatusTv.setText(R.string.screen_status);
                break;

            case    R.id.unlock_btn:
                if(wakeLock !=null){
                    wakeLock.release();
                    wakeLock =null;
                }
                mStatusTv.setText(R.string.unlock_status);
                break;
                
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("sa_","onDestory");
        if(wakeLock !=null){
            wakeLock.release();
            wakeLock =null;
        }
    }

}
