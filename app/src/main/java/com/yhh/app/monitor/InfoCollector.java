/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.monitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yhh.analyser.R;
import com.yhh.app.setttings.SettingMonitorActivity;
import com.yhh.info.InfoFactory;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;
import com.yhh.utils.TimeUtils;


public class InfoCollector {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "InfoMonitor";
    private boolean DEBUG =true;
    
    public static final int TIME_NOW = 99;
    
    public static final int APP_CPU_USED_RATIO = 0;
    public static final int CPU_USED_RATIO = 1;
    public static final int CPU_CLOCK = 2;
    
    public static final int APP_MEM_USED = 3;
    public static final int MEM_FREE = 4;
    
    public static final int GPU_USED_RATIO = 5;
    public static final int GPU_CLOCK = 6;
    
    public static final int POWER_CURRENT =7;
    public static final int SCREEN_BRIGHTNESS = 8;
    public static final int BATTERY_LEVEL = 9;
    public static final int BATTERY_TEMPERATURE = 10;
    public static final int BATTERY_VOLTAGE = 11;
    
    public static final int TRAFFIC_SEND_SPEED = 101;
    public static final int TRAFFIC_REV_SPEED = 102;
    
    public static final int CPU_USED_RARIO_COMMA=14;
    
    private boolean[] mIsMonitorItem = new boolean[SettingMonitorActivity.MONITOR_ITEMS_COUNT];
    
    private HashMap<Integer,String> mMonitorInfo = new HashMap<Integer, String>();
    private Context mContext;
    private BufferedWriter bw;
    private int pid;
    private InfoFactory mInfoFactory;
    private SimpleDateFormat formatterFile;
    
    private SharedPreferences mPreferences;
    private ExceptionMonitor mExceptionMonitor;
    
    public InfoCollector(Context context, int pid){
        this.mContext = context;
        this.pid = pid;
        
        mMonitorInfo = new HashMap<Integer, String>(); 
        mInfoFactory = InfoFactory.getInstance();
        mInfoFactory.init(context);
        
        readPrefs();
        
        mExceptionMonitor = new ExceptionMonitor(context);
    }
    
    private void readPrefs(){
        if(DEBUG){
            Log.i(TAG,"INTO readPrefs");
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        for(int i=0;i<SettingMonitorActivity.MONITOR_ITEMS_COUNT;i++){
            mIsMonitorItem[i] = !mPreferences.getBoolean(SettingMonitorActivity.PREF_MONITOR_ITEMS[i], false);
        }
    }
    
    @SuppressLint("UseSparseArrays")
    public HashMap<Integer,String> admin(){
        
        mMonitorInfo.put(TIME_NOW, TimeUtils.getCurrentTime2());

        // CPU Item
        if(mIsMonitorItem[APP_CPU_USED_RATIO]){
            mMonitorInfo.put(APP_CPU_USED_RATIO, mInfoFactory.getCpuPidUsedRatio(pid));
        }
        if(mIsMonitorItem[CPU_USED_RATIO]){
            mMonitorInfo.put(CPU_USED_RATIO, mInfoFactory.getCpuTotalUsedRatio().get(0));
        }
        
        if(mIsMonitorItem[CPU_CLOCK]){
            mMonitorInfo.put(CPU_CLOCK, mInfoFactory.getCpuFreqList());
        }
        
        // MEMORY Item     
        if(mIsMonitorItem[APP_MEM_USED]){
            mMonitorInfo.put(APP_MEM_USED, mInfoFactory.getMemoryPidUsedSize(pid, mContext));
        }
        if(mIsMonitorItem[MEM_FREE ]){
            mMonitorInfo.put(MEM_FREE, mInfoFactory.getMemoryUnusedSize(mContext));
        }
        
        // GPU Item    
        if(mIsMonitorItem[GPU_USED_RATIO]){
            mMonitorInfo.put(GPU_USED_RATIO, mInfoFactory.getGpuRate());
        }
        if(mIsMonitorItem[GPU_CLOCK]){
            mMonitorInfo.put(GPU_CLOCK, mInfoFactory.getGpuClock());
        }
        
        // POWER Item
        if(mIsMonitorItem[POWER_CURRENT]){
            mMonitorInfo.put(POWER_CURRENT , mInfoFactory.getPowerCurrent());
        }
        if(mIsMonitorItem[SCREEN_BRIGHTNESS]){
            mMonitorInfo.put(SCREEN_BRIGHTNESS , mInfoFactory.getScreenBrightness());
        }
        
        // BATTERY Item
        if(mIsMonitorItem[BATTERY_LEVEL ]){
            mMonitorInfo.put(BATTERY_LEVEL, mInfoFactory.getBatteryLevel());
        }
        if(mIsMonitorItem[BATTERY_TEMPERATURE ]){
            mMonitorInfo.put(BATTERY_TEMPERATURE, mInfoFactory.getBatteryTemperature());
        }
        if(mIsMonitorItem[BATTERY_VOLTAGE ]){
            mMonitorInfo.put(BATTERY_VOLTAGE, mInfoFactory.getBatteryVoltage());
        }
        
        // TRAFFIC Item
        if(mIsMonitorItem[12]){
            mMonitorInfo.put(TRAFFIC_SEND_SPEED, mInfoFactory.getTrafficSendSpeed());
            mMonitorInfo.put(TRAFFIC_REV_SPEED, mInfoFactory.getTrafficRevSpeed());
        }
        
        // EXCEPTION Item
        if(mExceptionMonitor.isEnabled()){
            HashMap<Integer, String> conditions = new HashMap<Integer, String>();
            for(int i=0;i<ExceptionMonitor.CONDITIONS_COUNT;i++){
                conditions.put(ExceptionMonitor.CONDITIONS_ITEMS[i], 
                        mMonitorInfo.get(ExceptionMonitor.CONDITIONS_ITEMS[i]));
            }
            mExceptionMonitor.monitor(conditions);
        }
        
        try {
            bw.write(monitorData2String());
        } catch (IOException e) {
            Log.e(TAG,"IOException ",e);
        }
        return mMonitorInfo;
    }
    
    public void writeTitle2File(){
        try {
            File resultFile = new File(MonitorService.resultFilePath);
            FileUtils.createFile(MonitorService.resultFilePath);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile)));
            bw.write(monitorTitle2String());  
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    
    public void closeOpenedStream() {
        try {
            if (bw != null) {
                bw.close();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
    
    /**
     * monitor title
     */
    private String monitorTitle2String(){
        StringBuilder sb = new StringBuilder();
        
//        String multiCpuTitle = "";
//        CpuInfo cpuInfo = new CpuInfo();
//        List<String> cpuList = cpuInfo.getCpuListName();
//        for (int i = 0; i < cpuList.size(); i++) {
//            multiCpuTitle +=  cpuList.get(i) + mContext.getString(R.string.total_usage) +",";
//        }
        String[] monitorItems = mContext.getResources().getStringArray(R.array.monitor_items);
        
        sb.append("时间, ");
        for(int i=0;i<SettingMonitorActivity.MONITOR_ITEMS_COUNT-1;i++){
            if(mIsMonitorItem[i]){
                sb.append(monitorItems[i] +", ");
            }
        }
        if(mIsMonitorItem[12]){
            sb.append("发送(KB/s), ");
            sb.append("接收(KB/s)");
        }
//        sb.append(multiCpuTitle); //八个核CPU使用率
        sb.append(ConstUtils.LINE_END);
        return sb.toString();
    }
    
    /**
     * monitor data
     */
    private String monitorData2String(){
        StringBuilder sb = new StringBuilder();
        sb.append(mMonitorInfo.get(TIME_NOW)+",");
        for(int i=0;i<SettingMonitorActivity.MONITOR_ITEMS_COUNT -1;i++){
            if(mIsMonitorItem[i]){
                    sb.append(mMonitorInfo.get(i)+",");
            }
        }
        if(mIsMonitorItem[12]){
            sb.append(mMonitorInfo.get(TRAFFIC_SEND_SPEED)+",");
            sb.append(mMonitorInfo.get(TRAFFIC_REV_SPEED));
        }
        sb.append(ConstUtils.LINE_END);
        return sb.toString();
    }
    
}
