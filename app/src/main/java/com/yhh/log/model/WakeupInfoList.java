/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.log.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.yhh.chart.other.InterruptLogParser;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.TimeUtils;

public class WakeupInfoList {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "WakeupInfo";
    private ArrayList<WakeupInfo> mInterruptList;
    /** 总的唤醒次数*/
    private int mWakekupCount;
    /** 总的唤醒时间*/
    private int mWakeUpTotalTime;
    /** 总的休眠时间*/
    private int mSleepTotalTime;
    /** 唤醒时间比重*/
    private float mWakeupRatio;
    
    private InterruptLogParser mParser;
    
    public WakeupInfoList(InterruptLogParser parser){
        mInterruptList = new ArrayList<WakeupInfo>();
        mParser = parser;
    }
    
    /** 获取中断号对应的 中断列表 */
    private int getIndex(int irqNum){
        int index = -1;
        if(mInterruptList ==null && mInterruptList.size()<1){
            return index;
        }
        for(int i=0;i<mInterruptList.size();i++){
            if(mInterruptList.get(i).getIdentifier() == irqNum){
                return i;
            }
        }
        return index;
    }
    
    public ArrayList<WakeupInfo> getInterruptList() {
        return mInterruptList;
    }

    public int getWakekupCount() {
        return mWakekupCount;
    }
   
    public int getWakeUpTotalTime() {
        return mWakeUpTotalTime;
    }
   
    public int getSleepTotalTime() {
        return mSleepTotalTime;
    }
    
    public int getTotalTime(){
        return this.mWakeUpTotalTime + this.mSleepTotalTime;
    }
    
    public float getWakeupRatio(){
        return mWakeupRatio;
    }
    
    
    /** 中断次数 加1 */
    public void addWakeupCount(ArrayList<Integer> irqNums){
        this.mWakekupCount++;
        for(Integer irq:irqNums){
            int index = getIndex(irq);
            if(index != -1){
                mInterruptList.get(index).addWakeupCount();
            }else{
                WakeupInfo info =new WakeupInfo(irq);
                info.addWakeupCount();
                mInterruptList.add(info);
            }
        }
    }
    
    public void setTotalSleepTime(int invidTime){
        Log.i(TAG,"invidTime="+invidTime+", mWakeUpTotalTime"+mWakeUpTotalTime);
        this.mSleepTotalTime = invidTime - this.mWakeUpTotalTime;
    }
    
    /** 增加中断时间 唤醒时间 */
    public void addWakeupTime(ArrayList<Integer> irqNums,int addTime){
        this.mWakeUpTotalTime += addTime;
        for(Integer irq:irqNums){
            int index = getIndex(irq);
            if(index != -1){
                mInterruptList.get(index).addWakeupTime(addTime);
            }else{
                WakeupInfo info =new WakeupInfo(irq);
                info.addWakeupTime(addTime);
                mInterruptList.add(info);
            }
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public String statistic(int beginTime, int endTime){
        Collections.sort(mInterruptList);
        DecimalFormat df = new DecimalFormat("0.0");
        
        mWakeupRatio = (float)mWakeUpTotalTime/(mWakeUpTotalTime + mSleepTotalTime)*100;
        StringBuilder sb = new StringBuilder();
        sb.append("总唤醒次数:   "+getWakekupCount());
        sb.append("\n总唤醒时间:   "+TimeUtils.seconds2Hhmmss(getWakeUpTotalTime()));
        sb.append("\n总休眠时间:   "+ TimeUtils.seconds2Hhmmss(getSleepTotalTime()));
        sb.append("\n唤醒比重:     "+df.format(getWakeupRatio())+"% \n\n");
        
        sb.append("中断号:次数, 平均间隔, 平均时长, 中断名\n");
        for(WakeupInfo info:mInterruptList){
            info.setAvgWakeupTime();
            info.setName(mParser.getInterruptName(info.getIdentifier()));
            float intervalTime = (float)getTotalTime()/info.getTotalWakeupCount();
            
            sb.append(info.getIdentifier()+":  ");
            sb.append(info.getTotalWakeupCount()+",  ");
            sb.append(df.format(intervalTime)+"s,  ");
            sb.append(df.format(info.getAvgWakeupTime())+"s,  ");
            sb.append("\n    "+info.getName()+" ");
            sb.append("\n");
        }
        return sb.toString();
    }
}
