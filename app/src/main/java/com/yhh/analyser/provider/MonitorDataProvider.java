/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.provider;

import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.LogUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MonitorDataProvider {
    private static String TAG =  ConstUtils.DEBUG_TAG+ "MonitorDataProvider";
    private boolean DEBUG = true;
    
    private boolean mIsParse;
    
    private ArrayList<String> mTimes;
    private ArrayList<ArrayList<Entry>> mMonitorData;
    private ArrayList<BarEntry> mMulCpuFreq;
    
    private String[] mTitle;
    
    public MonitorDataProvider(Context context){
        mTimes = new ArrayList<>();
        mMonitorData = new ArrayList<ArrayList<Entry>>();
        mMulCpuFreq = new ArrayList<>();
    }
    
    public void parseNewest(){
        String path =  AppConfig.MONITOR_DIR +"/"+LogUtils.getDateNewestLog(AppConfig.MONITOR_DIR);
        Log.i(TAG, "PATH="+path);
        parse(path);
    }
    
    public void parse(String path){
        mIsParse = true;
        BufferedReader br = null;
        String line;
        String[] items;
        int entryIndex = 0;
        float currentValue = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            if((line = br.readLine()) == null){
                return;
            }
            int start = line.indexOf(",");
            mTitle = line.substring(start+1).split(",");
            int itemLen = mTitle.length;
            for(int i=0;i<itemLen;i++){
                mTitle[i] = mTitle[i].trim();
            }
            
            int cpuFreqNum = findItemByTitle(ConstUtils.CPU_FREQ_TITLE);
            if(DEBUG){
                Log.d(TAG,"itemLen= "+itemLen+",cpuFreqNum="+cpuFreqNum);
            }
            
            for(int i=0;i<itemLen; i++){
                mMonitorData.add(new ArrayList<Entry>());
            }
            
            while (mIsParse && (line = br.readLine()) != null) {
                items = line.split(",");
                mTimes.add(items[0].split("\\s+")[1]);
                
                for(int i=0;i<itemLen;i++){
                    try{
                        if(i == cpuFreqNum){
                            String mulfrqStr = items[i+1];
                            String[] mulfrqArr = mulfrqStr.split("/");
                            float[] cpus = new float[mulfrqArr.length];
                            for(int j=0; j<mulfrqArr.length; j++){
                                cpus[j] = Float.valueOf(mulfrqArr[j]);
                            }
                            mMulCpuFreq.add(new BarEntry(cpus, entryIndex));
                            
                        }else{
                            currentValue =  Float.valueOf(items[i+1]);
                            mMonitorData.get(i).add(new Entry(currentValue, entryIndex));
                        }
                        
                    }catch(Exception e){
                        Log.e(TAG, "",e);
                    }
                }
                entryIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(br !=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private int findItemByTitle(String title){
        for(int i=0;i<mTitle.length;i++){
            if(mTitle[i].equals(title)){
                return i;
            }
        }
        return -1;
    }
    
    public String[] getTitles(){
        
        return mTitle;
    }
    
    public ArrayList<String> getXValues(){
        return mTimes;
    }
    
    public ArrayList<ArrayList<Entry>> getMonitorData(){
        return mMonitorData;
    }
    
    public ArrayList<BarEntry> getMulCpuData(){
        return mMulCpuFreq;
    }
    
}
