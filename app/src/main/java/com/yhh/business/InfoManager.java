/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yhh.analyser.R;
import com.yhh.app.monitor.ExceptionMonitor;
import com.yhh.app.setttings.SettingMonitorActivity;
import com.yhh.service.FloatService;
import com.yhh.config.AppConfig;
import com.yhh.constant.MonitorConst;
import com.yhh.info.InfoFactory;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;
import com.yhh.utils.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class InfoManager {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "InfoManager";
    private boolean DEBUG =true;
    
    public static final int TIME_NOW = 99;
    
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

    public static String resultFilePath;
    
    public InfoManager(Context context, int pid){
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
        
        mMonitorInfo.put(TIME_NOW, TimeUtils.getStandardTime());

        // CPU Item
        if(mIsMonitorItem[MonitorConst.APP_CPU_USED_RATIO]){
            mMonitorInfo.put(MonitorConst.APP_CPU_USED_RATIO, mInfoFactory.getCpuPidUsedRatio(pid));
        }
        if(mIsMonitorItem[MonitorConst.CPU_USED_RATIO]){
            mMonitorInfo.put(MonitorConst.CPU_USED_RATIO, mInfoFactory.getCpuTotalUsedRatio().get(0));
        }
        
        if(mIsMonitorItem[MonitorConst.CPU_CLOCK]){
            mMonitorInfo.put(MonitorConst.CPU_CLOCK, mInfoFactory.getCpuFreqList());
        }
        
        // MEMORY Item     
        if(mIsMonitorItem[MonitorConst.APP_MEM_USED]){
            mMonitorInfo.put(MonitorConst.APP_MEM_USED, mInfoFactory.getMemoryPidUsedSize(pid, mContext));
        }
        if(mIsMonitorItem[MonitorConst.MEM_FREE ]){
            mMonitorInfo.put(MonitorConst.MEM_FREE, mInfoFactory.getMemoryUnusedSize(mContext));
        }
        
        // GPU Item    
        if(mIsMonitorItem[MonitorConst.GPU_USED_RATIO]){
            mMonitorInfo.put(MonitorConst.GPU_USED_RATIO, mInfoFactory.getGpuRate());
        }
        if(mIsMonitorItem[MonitorConst.GPU_CLOCK]){
            mMonitorInfo.put(MonitorConst.GPU_CLOCK, mInfoFactory.getGpuClock());
        }
        
        // POWER Item
        if(mIsMonitorItem[MonitorConst.POWER_CURRENT]){
            mMonitorInfo.put(MonitorConst.POWER_CURRENT , mInfoFactory.getPowerCurrent());
        }
        if(mIsMonitorItem[MonitorConst.SCREEN_BRIGHTNESS]){
            mMonitorInfo.put(MonitorConst.SCREEN_BRIGHTNESS , mInfoFactory.getScreenBrightness());
        }
        
        // BATTERY Item
        if(mIsMonitorItem[MonitorConst.BATTERY_LEVEL ]){
            mMonitorInfo.put(MonitorConst.BATTERY_LEVEL, mInfoFactory.getBatteryLevel());
        }
        if(mIsMonitorItem[MonitorConst.BATTERY_TEMPERATURE ]){
            mMonitorInfo.put(MonitorConst.BATTERY_TEMPERATURE, mInfoFactory.getBatteryTemperature());
        }
        if(mIsMonitorItem[MonitorConst.BATTERY_VOLTAGE ]){
            mMonitorInfo.put(MonitorConst.BATTERY_VOLTAGE, mInfoFactory.getBatteryVoltage());
        }
        
        // TRAFFIC Item
        if(mIsMonitorItem[12]){
            mMonitorInfo.put(MonitorConst.TRAFFIC_SEND_SPEED, mInfoFactory.getTrafficSendSpeed());
            mMonitorInfo.put(MonitorConst.TRAFFIC_REV_SPEED, mInfoFactory.getTrafficRevSpeed());
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


    public HashMap<Integer,String> admin(int type){

        mMonitorInfo.put(TIME_NOW, TimeUtils.getStandardTime());


        // CPU Item
        if(type == FloatService.CPU_TYPE){
            mMonitorInfo.put(MonitorConst.CPU_CLOCK, mInfoFactory.getCpuFreqList());
        }

        // MEMORY Item
        if(type == FloatService.MEMORY_TYPE){
            mMonitorInfo.put(MonitorConst.MEM_FREE, mInfoFactory.getMemoryUnusedSize(mContext));
        }

        // GPU Item
        if(type == FloatService.GPU_TYPE){
            mMonitorInfo.put(MonitorConst.GPU_USED_RATIO, mInfoFactory.getGpuRate());
            mMonitorInfo.put(MonitorConst.GPU_CLOCK, mInfoFactory.getGpuClock());
        }

        // POWER Item
        if(type == FloatService.CURRENT_TYPE){
            mMonitorInfo.put(MonitorConst.POWER_CURRENT , mInfoFactory.getPowerCurrent());
            mMonitorInfo.put(MonitorConst.SCREEN_BRIGHTNESS , mInfoFactory.getScreenBrightness());
        }

        // BATTERY Item
        if(type == FloatService.BATTERY_TYPE){
            mMonitorInfo.put(MonitorConst.BATTERY_LEVEL, mInfoFactory.getBatteryLevel());
            mMonitorInfo.put(MonitorConst.BATTERY_TEMPERATURE, mInfoFactory.getBatteryTemperature());
            mMonitorInfo.put(MonitorConst.BATTERY_VOLTAGE, mInfoFactory.getBatteryVoltage());
        }

        // app Item
        if(type == FloatService.APP_TYPE){
            mMonitorInfo.put(MonitorConst.APP_CPU_USED_RATIO, mInfoFactory.getCpuPidUsedRatio(pid));
            mMonitorInfo.put(MonitorConst.CPU_USED_RATIO, mInfoFactory.getCpuTotalUsedRatio().get(0));
            mMonitorInfo.put(MonitorConst.APP_MEM_USED, mInfoFactory.getMemoryPidUsedSize(pid, mContext));

        }

        // TRAFFIC Item
        if(type == FloatService.ALL_TYPE){
            mMonitorInfo.put(MonitorConst.APP_CPU_USED_RATIO, mInfoFactory.getCpuPidUsedRatio(pid));
            mMonitorInfo.put(MonitorConst.CPU_USED_RATIO, mInfoFactory.getCpuTotalUsedRatio().get(0));
            mMonitorInfo.put(MonitorConst.CPU_CLOCK, mInfoFactory.getCpuFreqList());

            mMonitorInfo.put(MonitorConst.APP_MEM_USED, mInfoFactory.getMemoryPidUsedSize(pid, mContext));
            mMonitorInfo.put(MonitorConst.MEM_FREE, mInfoFactory.getMemoryUnusedSize(mContext));

            mMonitorInfo.put(MonitorConst.GPU_USED_RATIO, mInfoFactory.getGpuRate());
            mMonitorInfo.put(MonitorConst.GPU_CLOCK, mInfoFactory.getGpuClock());

            mMonitorInfo.put(MonitorConst.POWER_CURRENT , mInfoFactory.getPowerCurrent());
            mMonitorInfo.put(MonitorConst.SCREEN_BRIGHTNESS , mInfoFactory.getScreenBrightness());

            mMonitorInfo.put(MonitorConst.BATTERY_LEVEL, mInfoFactory.getBatteryLevel());
            mMonitorInfo.put(MonitorConst.BATTERY_TEMPERATURE, mInfoFactory.getBatteryTemperature());
            mMonitorInfo.put(MonitorConst.BATTERY_VOLTAGE, mInfoFactory.getBatteryVoltage());

            mMonitorInfo.put(MonitorConst.TRAFFIC_SEND_SPEED, mInfoFactory.getTrafficSendSpeed());
            mMonitorInfo.put(MonitorConst.TRAFFIC_REV_SPEED, mInfoFactory.getTrafficRevSpeed());
        }

        // EXCEPTION Item
        if(type == FloatService.EXCEPTION_TYPE){
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
            File resultFile = new File(resultFilePath);
            FileUtils.createFile(resultFilePath);
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
            sb.append(mMonitorInfo.get(MonitorConst.TRAFFIC_SEND_SPEED)+",");
            sb.append(mMonitorInfo.get(MonitorConst.TRAFFIC_REV_SPEED));
        }
        sb.append(ConstUtils.LINE_END);
        return sb.toString();
    }

    public void createMonitorFile() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd_HHmmss");
        String mDateTime = formatter.format(Calendar.getInstance().getTime().getTime());
        resultFilePath = AppConfig.MONITOR_DIR + "/" + mDateTime + "";
    }
    
}
