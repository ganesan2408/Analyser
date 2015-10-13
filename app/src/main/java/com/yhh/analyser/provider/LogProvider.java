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
import com.yhh.analyser.bean.app.PhoneInfo;
import com.yhh.analyser.view.activity.LogAnalyActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.Utils;
import com.yhh.analyser.utils.ChartTool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * provider useful infomation for chart.
 * 
 */
public class LogProvider {
    private static String TAG =  ConstUtils.DEBUG_TAG+ "LogProvider";
    private boolean DEBUG = true;
    
    private Context mContext;
    
    private String mTitleDay;
    
    protected ChartTool mChartTool = ChartTool.getInstance();
    
    private ArrayList<Entry> mLevelEntry;
    private ArrayList<Entry> mTempEntry;
    private ArrayList<Entry> mVoltageEntry;
    private ArrayList<BarEntry> mStatusEntry;
    private ArrayList<BarEntry> mHealthEntry;
    
    private ArrayList<Entry> mCurrentEntry;
    private ArrayList<Entry> mBrightnessEntry;
    private ArrayList<Entry> mGpuClkEntry;
    private ArrayList<BarEntry> mMulCpuFreq;
    
    private ArrayList<ArrayList<BarEntry>> mWakeupEntry;
    
    private ArrayList<ArrayList<String>> mForeApps;
    
    public LogProvider(Context context){
        mContext = context;
    }
    
    public void generateData(String path){
        Log.i(TAG,"======"+path);
        if(path.endsWith(LogBatteryParser.newFile)){
            generateBatteryData(path);
        }else if(path.endsWith(LogPmParser.newFile)){
            generatePmData(path);
        }else if(path.endsWith(LogSleepParser.newFile)){
            generateWakeupData(path);
        }else if(path.endsWith(LogcatParser.newFile)){
            generateLogcatData(LogcatParser.newFile);
        }
    }
    
    public void generateBatteryData(String path){
        mLevelEntry = new ArrayList<Entry>();
        mTempEntry = new ArrayList<Entry>();
        mVoltageEntry = new ArrayList<Entry>();
        mStatusEntry = new ArrayList<BarEntry>();
        mHealthEntry = new ArrayList<BarEntry>();
        
        String line;
        BufferedReader br = null;
        String targetPath = LogAnalyActivity.sLogCacheDir +"/" +path;
        mTitleDay = path.split("_")[1];
        Log.i(TAG,"generateBatteryData targetPath: "+targetPath);
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetPath)));
            while ((line = br.readLine()) != null) {
                String[] split = line.split("#");
                mLevelEntry.add(new Entry(Float.parseFloat(split[1])/100, Integer.parseInt(split[0])));
                mTempEntry.add(new Entry(Float.parseFloat(split[2])/10, Integer.parseInt(split[0])));
                mStatusEntry.add(new BarEntry(Integer.parseInt(split[3]), Integer.parseInt(split[0])));
                mHealthEntry.add(new BarEntry(Integer.parseInt(split[4]), Integer.parseInt(split[0])));
                mVoltageEntry.add(new Entry(Float.parseFloat(split[5])/1000, Integer.parseInt(split[0])));
                
            }
            if(DEBUG){
                Log.d(TAG,"mLevelEntry SIZE: " + mLevelEntry.size());
                Log.i(TAG,"generateBatteryData completely");
            }
            
        } catch (FileNotFoundException e) {
            Log.e(TAG,"",e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init battery log close failure.");
                }
            }
        }
    }
    
    public void generateLogcatData(String path){
        mForeApps = new ArrayList<ArrayList<String>>();
        
        ArrayList<String> appStart = new ArrayList<String>();
        ArrayList<String> appName = new ArrayList<String>();
        
        String line;
        BufferedReader br = null;
        String targetPath = LogAnalyActivity.sLogCacheDir +"/" +path;
        mTitleDay = path;
        Log.i(TAG,"targetPath: "+targetPath);
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetPath)));
            while ((line = br.readLine()) != null) {
                String[] split = line.split("#");
                appStart.add(split[0]);
                appName.add(Utils.getAppName(mContext, split[1]));
//                Log.i(TAG,split[1]+" ==> "+Utils.getAppName(mContext, split[1]));
            }
            mForeApps.add(appStart);
            mForeApps.add(appName);
            
            if(DEBUG){
                Log.i(TAG,"generateLogcatData completely");
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init logcat close failure.");
                }
            }
        }
    }
    
    public void generatePmData(String path){
        mCurrentEntry = new ArrayList<Entry>();
        mBrightnessEntry = new ArrayList<Entry>();
        mGpuClkEntry = new ArrayList<Entry>();
        mMulCpuFreq = new ArrayList<BarEntry>();

        int cpuNum = PhoneInfo.isX3()?6:8;

        String line;
        BufferedReader br = null;
        String targetPath = LogAnalyActivity.sLogCacheDir +"/" +path;
        mTitleDay = path.split("_")[1];
        Log.i(TAG,"targetPath: "+targetPath);
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetPath)));
            float[] mulCpuFreq= new float[8];
            while ((line = br.readLine()) != null) {
                String[] split = line.split("#");
                mCurrentEntry.add(new Entry(Integer.parseInt(split[1]), Integer.parseInt(split[0])));
                mBrightnessEntry.add(new Entry(Integer.parseInt(split[2]), Integer.parseInt(split[0])));
                mGpuClkEntry.add(new Entry(Integer.parseInt(split[3]), Integer.parseInt(split[0])));
                
                for(int i=0;i<cpuNum;i++){
                    mulCpuFreq[i] = (float)Integer.parseInt(split[4+i]);
                }
                mMulCpuFreq.add(new BarEntry(mulCpuFreq, Integer.parseInt(split[0])));
            }
            if(DEBUG){
                Log.d(TAG,"mCurrentEntry SIZE: " + mCurrentEntry.size());
                Log.i(TAG,"generatePmData completely");
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init pm log close failure.");
                }
            }
        }
    }
    
    public void generateWakeupData(String path){
        mWakeupEntry = new ArrayList<ArrayList<BarEntry>>();
        for(int i=0;i< ChartTool.pictures;i++){
            ArrayList<BarEntry> wakeups = new ArrayList<BarEntry>();
            mWakeupEntry.add(wakeups);
        }
        
        int curGroup =-1;
        String line;
        BufferedReader br = null;
        String targetPath = LogAnalyActivity.sLogCacheDir +"/" +path;
        mTitleDay = path.split("_")[1];
        Log.i(TAG,"targetPath: "+targetPath);
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(targetPath)));
            while ((line = br.readLine()) != null) {
                if(!line.startsWith("#")){
                    mWakeupEntry.get(curGroup).add(new BarEntry(1, Integer.parseInt(line)-curGroup*ChartTool.secendsPerGroup));
                }else{
                    curGroup = Integer.valueOf(line.substring(1,2));
                    Log.d(TAG,"curGroup="+curGroup);
                }
            }
            if(DEBUG){
                Log.d(TAG,"mWakeupEntry SIZE: " + mWakeupEntry.size());
                Log.i(TAG,"generateWakeupData completely");
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init generateWakeupData log close failure.");
                }
            }
        }
    }
    
    public String getTitleDay(){
        return mTitleDay;
    }
    
    public ArrayList<Entry> getLevelEntryList(){
        return mLevelEntry;
    }
    
    public ArrayList<Entry> getTemperatureEntryList(){
        return mTempEntry;
    }
    
    public ArrayList<BarEntry> getStatusEntryList(){
        return mStatusEntry;
    }
    
    public ArrayList<Entry> getVoltageEntryList(){
        return mVoltageEntry;
    }
    
    public ArrayList<BarEntry> getHealthEntryList(){
        return mHealthEntry;
    }
    
    
    public ArrayList<Entry> getCurrentEntryList(){
        return mCurrentEntry;
    }
    
    public ArrayList<Entry> getBrightnessEntryList(){
        return mBrightnessEntry;
    }
    
    public ArrayList<Entry> getGpuClkEntryList(){
        return mGpuClkEntry;
    }
    
    public ArrayList<BarEntry> getCpuClkEntryList(){
        return mMulCpuFreq;
    }
    
    
    public ArrayList<ArrayList<BarEntry>> getWakeupEntry(){
        return mWakeupEntry;
    }
    
    public ArrayList<ArrayList<String>> getForeApps(){
        return mForeApps;
    }
    
    public String foreAppsToString(){
        StringBuffer sb = new StringBuffer();
        if(mForeApps ==null || mForeApps.size() <=0){
            return "";
        }
        
        int len = mForeApps.get(0).size();
        if(len>0){
            for(int i=0;i<len-1;i++){
                sb.append(mForeApps.get(0).get(i));
                sb.append(":  ");
                sb.append(mForeApps.get(1).get(i));
                sb.append(getStamp(mForeApps.get(0).get(i), mForeApps.get(0).get(i+1)));
                sb.append("\n");
            }
            
            sb.append(mForeApps.get(0).get(len-1));
            sb.append(":  ");
            sb.append(mForeApps.get(1).get(len-1)); 
        }
        return sb.toString();
    }
    
    private String getStamp(String startTime, String endTime){
        StringBuffer stamp = new StringBuffer();
        int hours = Integer.valueOf(endTime.substring(0, 2))-
                Integer.valueOf(startTime.substring(0, 2));
        int mins = Integer.valueOf(endTime.substring(3, 5))-
                Integer.valueOf(startTime.substring(3, 5));
        int seconds = Integer.valueOf(endTime.substring(6, 8))-
                Integer.valueOf(startTime.substring(6, 8));
        
        if(seconds<0){
            seconds += 60;
            --mins;
        }
        
        if(mins<0){
            mins += 60;
            --hours;
        }
        
        if(hours<0){
            hours += 24;
        }
        
        stamp.append(" (");
        if(hours >0){
            stamp.append(hours+"h");
        }
        if(mins >0){
            stamp.append(mins+"m");
        }
        if(seconds >0){
            stamp.append(seconds+"s");
        }
        
        stamp.append(")");
        return stamp.toString();
    }
}
