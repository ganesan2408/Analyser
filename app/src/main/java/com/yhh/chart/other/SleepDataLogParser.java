/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

import com.yhh.log.model.WakeupInfoList;
import com.yhh.utils.ConstUtils;

public class SleepDataLogParser extends Parser{
    private static String TAG =  ConstUtils.DEBUG_TAG+ "SleepLogParser";
    private String mSleepLogContent;
    private int mStartRealTime;
    private int mStartTimeStamp;
    private int mEndTimeStamp;
   
    private WakeupInfoList mWakeupList;
    private InterruptLogParser mIrqParser;
    
    public static final int TYPE_WAKE_UP = 1;
    public static final int TYPE_INTO_SLEEP = 2;
    public static final int TYPE_PREVENT_SLEEP = 3;
    public static final int TYPE_NO_KNOWN = 4;
    
    public SleepDataLogParser(String logDir){
        super(logDir,ConstUtils.LOG_SLEEP);
        if(logDir.equals(ConstUtils.LOG_DIR)){
            Log.d(TAG,"parse local interrupt");
            mIrqParser = new InterruptLogParser();
        }else{
            mIrqParser = new InterruptLogParser(mLogDir+"/"+ConstUtils.LOG_INTERRUPTES);
        }
        mWakeupList = new WakeupInfoList(mIrqParser);
    }
    
    @Override
    public void parseTarget(File log){
        BufferedReader br = null;
        String line;
        String tmp;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(log)));
            //获取开始时间
            if((line = br.readLine()) != null){
                mStartRealTime = this.getRealTime(line);
                mStartTimeStamp = this.getTimeStamp(line);
                Log.i(TAG,"mStartRealTime="+mStartRealTime);
                Log.i(TAG,"mStartTimeStamp="+mStartTimeStamp);
                sb.append(line + "\n");
            }
            while ((tmp = br.readLine()) != null) {
                line = tmp;
                sb.append(line + "\n");
            }
            mEndTimeStamp = this.getTimeStamp(line);
            Log.i(TAG,"mEndTimeStamp="+mEndTimeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSleepLogContent = sb.toString();
    }
    
    
    public String statistic(){
        return statistic(mStartTimeStamp, mEndTimeStamp);
    }
    
    /**
     * 
     * @param beginTime
     *      begin time stamp
     * @param endTime
     *      end time stamp
     * @param init
     */
    public String statistic(int beginTime, int endTime){
        int validTime = 0;
        int currentTime;
        int currentSleepStamp = 0;
        int curentWakeupStamp = 0;
        int elapsedStamp = 0;
        
        if(mSleepLogContent ==null){
            Log.e(TAG,"mSleepLogContent IS nuLL.");
            return null;
        }
        String[] logArr = mSleepLogContent.split("\n");
        int cusor = 1;
        if(logArr ==null || logArr.length <= 1){
            Log.e(TAG,"LOG array is nuLL.");
            return null;
        }
        int len = logArr.length;
        //寻找开始的目标节点
        currentTime = getTimeStamp(logArr[cusor]);
        while(cusor<len && currentTime < beginTime){
            currentTime = getTimeStamp(logArr[++cusor]);
        }
        Log.d(TAG,"BEGIN LINE NUMBER:"+cusor+",currentTime="+currentTime);
        //开始读取有效数据
        ArrayList<Integer> irqs = new ArrayList<Integer>();
        //单独解析第一行 START
        if(getType(logArr[cusor])==TYPE_INTO_SLEEP){
            currentSleepStamp = getTimeStamp(logArr[cusor]);
        }else if(getType(logArr[cusor])==TYPE_WAKE_UP){ 
            irqs = this.getIrq(logArr[cusor]);
            mWakeupList.addWakeupCount(irqs);
            curentWakeupStamp = this.getTimeStamp(logArr[cusor]);
            
        }else if(getType(logArr[cusor])==TYPE_PREVENT_SLEEP){
            curentWakeupStamp = this.getTimeStamp(logArr[cusor]);
        }
        cusor++;
        //单独解析第一行 END
        
        while(cusor<len){
            if(getTimeStamp(logArr[cusor]) > endTime){
                Log.d(TAG,"END LINE NUMBER:"+cusor);
                break;
            }
            
            if(getType(logArr[cusor])==TYPE_INTO_SLEEP){
                currentSleepStamp = getTimeStamp(logArr[cusor]);
                elapsedStamp = currentSleepStamp -curentWakeupStamp;
                if(elapsedStamp >3600*24 || elapsedStamp<0){
                    validTime += (curentWakeupStamp-currentTime);
                    currentTime = currentSleepStamp;
                    Log.w(TAG,"[>]currentTime="+currentTime+", validTime="+validTime
                            +", cusor="+cusor);
                }else{
                    mWakeupList.addWakeupTime(irqs, elapsedStamp);
                }
            }else if(getType(logArr[cusor])==TYPE_WAKE_UP){ 
                irqs = this.getIrq(logArr[cusor]);
                mWakeupList.addWakeupCount(irqs);
                
                curentWakeupStamp = this.getTimeStamp(logArr[cusor]);
                elapsedStamp = curentWakeupStamp - currentSleepStamp;
                if(elapsedStamp >3600*24 || elapsedStamp<0){
                    validTime += (currentSleepStamp-currentTime);
                    currentTime = curentWakeupStamp;
                    Log.w(TAG,"[<]currentTime="+currentTime+", validTime="+validTime
                            +", cusor="+cusor);
                }
            }else if(getType(logArr[cusor])==TYPE_PREVENT_SLEEP){
                curentWakeupStamp = this.getTimeStamp(logArr[cusor]);
                elapsedStamp = curentWakeupStamp - currentSleepStamp;
                if(elapsedStamp >3600*24 || elapsedStamp<0){
                    validTime += (currentSleepStamp-currentTime);
                    currentTime = curentWakeupStamp;
                    Log.w(TAG,"[^]currentTime="+currentTime+", validTime="+validTime
                            +", cusor="+cusor);
                }
            }else{
                Log.e(TAG,"get line type unknown.");
            }
            cusor++;
        }
        validTime += (endTime - currentTime);
        mWakeupList.setTotalSleepTime(validTime);
        
        String sta = mWakeupList.statistic(beginTime, endTime);
        return sta;
    }
    
    
    private int getTimeStamp(String line){
        int timeStamp = 0;
        int begin = line.indexOf("[");
        int end = line.indexOf("]");
        timeStamp = Integer.valueOf(line.substring(begin+1,end));
        return timeStamp;
    }
    
    private int getRealTime(String line){
        int realTime = 0;
        int startIndex = line.indexOf(" ");
        String real = line.substring(startIndex+1, startIndex+9);
        Log.i(TAG,"START:"+real);
        String[] realArr = real.split(":");
        if(realArr !=null && realArr.length ==3){
            realTime = Integer.valueOf(realArr[0])*3600 
                    + Integer.valueOf(realArr[1])*60 
                    + Integer.valueOf(realArr[2]);
        }
        return realTime;
    }
    
    private int realTime2Stamp(int realtime){
        return realtime - mStartRealTime + mStartTimeStamp;
    }
    
    /**
     *  get the type of action
     * @param line
     * @return
     */
    private int getType(String line){
        int type;
        if(line.startsWith(">")){
            type = TYPE_INTO_SLEEP;
        }else if(line.startsWith("<")){
            type = TYPE_WAKE_UP;
        }else if(line.startsWith("^")){
            type = TYPE_PREVENT_SLEEP;
        }else{
            type = TYPE_NO_KNOWN;
        }
        return type;
    }
    
    private ArrayList<Integer> getIrq(String line){
        ArrayList<Integer> irqs = new ArrayList<Integer>();
        int startIndex = line.indexOf("(");
        int endIndex = line.indexOf(")");
        if(startIndex != -1 && endIndex != -1){
            String irqTmp = line.substring(startIndex+1, endIndex);
            String[] irqArr = irqTmp.split(",");
            
            if(irqArr.length>=3){
               String[] irqNumArr = irqArr[2].trim().split("\\s+");
               for(int i=0;i<irqNumArr.length;i++){
                    irqs.add(Integer.valueOf(irqNumArr[i].trim()));
                }
            }
        }
        return irqs;
    }
    
    public WakeupInfoList getWakeupList(){
        return mWakeupList;
    }

}
