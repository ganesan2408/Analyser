/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.fragment.status;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.yhh.info.InfoFactory;
import com.yhh.utils.CommandUtils;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;


public class PowerFragment extends Fragment
{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "Power";
    private boolean DEBUG = false;
    
    private TextView mPowerTv;
    private int INTERVAL_TIME = 3;
    private ScheduledExecutorService  mScheService;
    
  
    @Override
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
        View v = inflater.inflate(R.layout.status_power_fragment, null) ;
        mPowerTv = (TextView) v.findViewById(R.id.status_power_tv);
        return v ;
    }
  
    private Handler mHandler = new Handler(){
       public void handleMessage(Message msg) {
           if(msg.what == 1){
               if(DEBUG){
                   Log.i(TAG,"on receive power");
               }
               mPowerTv.setText((String)msg.obj);
           }
       };
    };
  
    public void start() {
        if(DEBUG){
            Log.i(TAG,"power start");
        }
        mScheService = Executors.newScheduledThreadPool(1);
        mScheService.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                String value;
                
                String ci = InfoFactory.getInstance().getPowerCurrent();
                sb.append("当前电流：");
                sb.append(ci + "mA \n");
               
                String bi = InfoFactory.getInstance().getScreenBrightness();
                sb.append("当前亮度：");
                sb.append(bi + " \n");
                

                sb.append("GPU clock：");
                value= FileUtils.getCommandNodeValue(CommandUtils.CMD_GPU_CLK);
                if(value !=null && !value.equals("")){
                    sb.append(Long.valueOf(value.trim()) / 1000.0 / 1000.0
                        + "MHz \n\n");
                }else{
                    sb.append("\n\n");
                }
                
                value= FileUtils.getCommandNodeValue(CommandUtils.CMD_POWER_STATUS);
                if(value !=null && !value.equals("")){
                    sb.append(value + " \n");
                }
                
                mHandler.sendMessage(mHandler.obtainMessage(1,sb.toString()));
            }
        },  0, INTERVAL_TIME, TimeUnit.SECONDS);
    }
    
    public void stop() {
        if(DEBUG){
            Log.i(TAG,"power onStop");
        }
        if (mScheService != null) {
            mScheService.shutdownNow();
        }
    }
}