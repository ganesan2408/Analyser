/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import com.yhh.utils.NumberUtils;
import com.yhh.utils.ShellUtils;

import android.content.Context;


/**
 * Info factory that contains all kinds of infomation.
 * 
 */
public class InfoFactory {
    private DecimalFormat mFormat;
    private CpuInfo mCpuInfo;
    private GpuInfo mGpuInfo;
    private MemoryInfo mMemoryInfo;
    private BatteryInfo mBatteryInfo;
    private PowerInfo mPowerInfo;
    private ScreenInfo mScreenInfo;
    private TrafficInfo mTrafficInfo;
    private Context mContext;
    
    private static InfoFactory mInfoFactory;
    
    private InfoFactory(){
        mCpuInfo = new CpuInfo();
        mGpuInfo = new GpuInfo();
        mMemoryInfo = new MemoryInfo();
        mBatteryInfo = new BatteryInfo();
        mTrafficInfo = new TrafficInfo();
        mPowerInfo = new PowerInfo();
        mScreenInfo = new ScreenInfo();
        
        mFormat = new DecimalFormat();
        mFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        mFormat.setGroupingUsed(false);
        mFormat.setMaximumFractionDigits(2);
        mFormat.setMinimumFractionDigits(2);
    };
    
    public static InfoFactory getInstance(){
        if(null == mInfoFactory){
            mInfoFactory = new InfoFactory();
        }
        return mInfoFactory;
    }
    
    /********* INIT ***********/
    public void init(Context context){
        mContext = context;
        mBatteryInfo.init(context);
        mBatteryInfo.register();
        
        mTrafficInfo.init(context);
    }
    
    public void destory(){
        mBatteryInfo.unregister();
    }
    
    /******* CPU *********/
    public String getCpuPidUsedRatio(int pid){
        mCpuInfo.updateCpuStat(pid);
        return mCpuInfo.getProcessCpuRatio(pid);
    }
    
    /**
     * 0: total cpu
     * 1-8: cpu 0~7
     * 
     * @return
     */
    public ArrayList<String> getCpuTotalUsedRatio(){
        return mCpuInfo.getTotalCpuRatio();
    }
    
    public String getCpuUsedRatioBySeperate(){
        ArrayList<String> cpuRatios = mCpuInfo.getTotalCpuRatio();
        StringBuffer cpuRatioArray = new StringBuffer();
        for(int i=1; i<cpuRatios.size();i++){
            cpuRatioArray.append(cpuRatios.get(i) + "/");
        }
        for (int i = 0; i < mCpuInfo.getCpuNum() - cpuRatios.size() + 1; i++) {
            cpuRatioArray.append("0.00,");
        }
        return cpuRatioArray.toString();
    }
    
    public String getCpuFreqList(){
        return mCpuInfo.getCpuFreqList();
    }
    
    /******* GPU *********/
    public String getGpuClock(){
        return mFormat.format(mGpuInfo.getGpuClock());
    }
    
    public String getGpuRate(){
        return mFormat.format(mGpuInfo.getGpuRate());
    }
    
    /******* MEMORY *********/
    public String getMemoryPidUsedSize(int pid, Context context){
        long pidMemory =mMemoryInfo.getPidMemorySize(pid, context);
        return mFormat.format((double) pidMemory / 1024);
    }
    
    public String getMemoryUnusedSize(Context context){
        long freeMemory = mMemoryInfo.getFreeMemorySize(context);
        return mFormat.format((double) freeMemory / 1024);
    }
    
    /******* POWER *********/
    public String getPowerCurrent(){
        return mFormat.format(mPowerInfo.getcurrent());
    }
    
    /******* SCREEN *********/
    public String getScreenBrightness() {
        return String.valueOf(mScreenInfo.getBrightness());
    }
    
    /******* BATTERY *********/
    public String getBatteryLevel(){
        return mBatteryInfo.getLevel();
    }
    
    public String getBatteryTemperature(){
        return mBatteryInfo.getTemperature();
    }
    
    public String getBatteryVoltage(){
        return mBatteryInfo.getVoltage();
    }
    
    /********Traffic*********/
    public String getTrafficSendSpeed(){
        return NumberUtils.formatDecimal(mTrafficInfo.getSendSpeed(), 2, false);
    }

    public String getTrafficRevSpeed(){
        return NumberUtils.formatDecimal(mTrafficInfo.getRevSpeed(), 2, false);
    }
}
