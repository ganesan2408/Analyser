/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.provider;

import android.util.Log;

import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.ShellUtils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitorExceptionStat {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "ExceptionStat";
    private boolean DEBUG = false;
    
    private HashMap<String, Float> mTopCpuApp = new HashMap<String, Float>();
    private int mTopCount;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private static final String TOP_CMD = "top -m 5 -n 1 -d 1";
    
    private static MonitorExceptionStat mExceptionStat;
    private MonitorExceptionStat(){};
    
    public static MonitorExceptionStat getInstance(){
        if(mExceptionStat ==null){
            mExceptionStat = new MonitorExceptionStat();
        }
        return mExceptionStat;
    }
    
    public void beginStatistic(){
        singleThreadExecutor.execute(new Runnable(){

            @Override
            public void run() {
                if(DEBUG){
                    Log.i(TAG,"beginStatistic top cpu process.");
                };
                
                String topStr = ShellUtils.execCommand(TOP_CMD, false).successMsg;
                String[] topArr = topStr.split("\n");
                int len = topArr.length;
                
                mTopCount++;
                for(int i=0;i<5;i++){
                    String[] line = topArr[i+7].trim().split("\\s+");
                    int arrSize = line.length;
                    String key = line[arrSize-1].trim();
                    String tmp = line[2].trim();
                    if(DEBUG){
                        Log.i(TAG,"value ="+tmp);
                    }
                    
                    Float value = Float.valueOf(tmp.substring(0,tmp.length()-1));
                    if(mTopCpuApp.containsKey(key)){
                        Float oldValue = mTopCpuApp.get(key);
                        mTopCpuApp.remove(key);
                        mTopCpuApp.put(key, oldValue+value);
                    }else{
                        mTopCpuApp.put(key, value);
                    }
                }
                if(DEBUG){
                    for(Entry<String, Float> entry: mTopCpuApp.entrySet()){
                        Log.i(TAG,entry.getKey()+"-->"+entry.getValue());
                    }
                }
            }
        });
    }
    
    public void clear(){
        mTopCpuApp.clear();
    }
    
    public HashMap<String, Float> getTopCpuApps(){
        return mTopCpuApp;
    }
    
    public int getTopCount(){
        return mTopCount;
    }
}
