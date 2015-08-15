/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.afragment.status;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.utils.CommandUtils;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.ShellUtils;
import com.yhh.utils.ShellUtils.CommandResult;

public class TopProcessFragment extends Fragment
{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "topProcess";
    private boolean DEBUG = false;
    
    private TextView mTopProcessTv;
    
    private int INTERVAL_TIME =3;
    private ScheduledExecutorService  mScheService;
    
    public void onStart() {
        start();
        super.onStart();
    }
    
    @Override
    public void onStop() {
        stop();
        super.onStop();
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.status_top_process_fragment, null) ;
        mTopProcessTv = (TextView) v.findViewById(R.id.status_top_process);
        mTopProcessTv.setMovementMethod(new ScrollingMovementMethod());
        return v ;
     }
    
  
    private Handler mHandler = new Handler(){
       public void handleMessage(Message msg) {
           if(msg.what == 1){
               if(DEBUG){
                   Log.i(TAG,"on receive top process");
               }
               mTopProcessTv.setText((String)msg.obj);
           }
       };
    };
  
  
    public void start() {
        if(DEBUG){
            Log.i(TAG,"topProcess start");
        }
        mScheService = Executors.newScheduledThreadPool(1);
        mScheService.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                CommandResult cr = ShellUtils.execCommand(CommandUtils.CMD_TOP_PROCESS, false);
                mHandler.sendMessage(mHandler.obtainMessage(1,cr.successMsg + "\n" + cr.errorMsg));
            }
        },  0, INTERVAL_TIME, TimeUnit.SECONDS);
    }

    public void stop() {
        if(DEBUG){
            Log.i(TAG,"topProcess stop");
        }
        if (mScheService != null) {
            mScheService.shutdownNow();
        }
    }
}