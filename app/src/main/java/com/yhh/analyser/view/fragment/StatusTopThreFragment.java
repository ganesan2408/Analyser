/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.CommandUtils;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.ShellUtils;
import com.yhh.analyser.utils.ShellUtils.CommandResult;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusTopThreFragment extends Fragment
{
    private static final String TAG = ConstUtils.DEBUG_TAG+ "TopThread";
    private boolean DEBUG = false;
    
    private TextView mTopThreadTv;
    
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
        View v = inflater.inflate(R.layout.status_top_thread_fragment, null) ;
        mTopThreadTv = (TextView) v.findViewById(R.id.status_top_thread);
        return v ;
    }
  
    private Handler mHandler = new Handler(){
       public void handleMessage(Message msg) {
           if(msg.what == 1){
//               Log.i(TAG,"top thread recevie");
               mTopThreadTv.setText((String)msg.obj);
           }
       }
    };
  
    
    public void start() {
        if(DEBUG){
            Log.i(TAG,"topThread start");
        }
        mScheService = Executors.newScheduledThreadPool(1);
        mScheService.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                CommandResult cr = ShellUtils.execCommand(CommandUtils.CMD_TOP_THREAD, false);
                mHandler.sendMessage(mHandler.obtainMessage(1,cr.successMsg + "\n" + cr.errorMsg));
            }
        },  0, INTERVAL_TIME, TimeUnit.SECONDS);
    }

    public void stop() {
        if(DEBUG){
            Log.i(TAG,"topThread stop");
        }
        if (mScheService != null) {
            mScheService.shutdownNow();
        }
    }
}