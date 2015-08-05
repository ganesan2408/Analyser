/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.log.model;


public class WakeupInfo implements Comparable<Object>{
    private int identifier;
    private String name;
    private int totalWakeupCout;
    private int totalWakeupTime;
    private float avgWakeupTime;
    
    public WakeupInfo(){
        
    }
    
    public WakeupInfo(int identifier){
        super();
        this.identifier = identifier;
    }
    
    public int getIdentifier() {
        return identifier;
    }
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
    public String getName() {
        return name;
    }
    public int getTotalWakeupCount() {
        return totalWakeupCout;
    }
    
    public int getTotalWakeupTime(){
        return totalWakeupTime;
    }
    
    public float getAvgWakeupTime() {
        return avgWakeupTime;
    }
    
    /** 设置中断名 */
    public void setName(String name) {
        this.name = name;
    }
    /** 平均每次中断带来的唤醒时长 */
    public void setAvgWakeupTime() {
        this.avgWakeupTime = (float)totalWakeupTime/totalWakeupCout;
    }
    
    /** 中断次数 加1*/
    public void addWakeupCount(){
        this.totalWakeupCout++;
    }
    
    /** 中断时间 加 addTime*/
    public void addWakeupTime(int addTime){
        this.totalWakeupTime += addTime;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof WakeupInfo){
            if(getTotalWakeupCount() >=((WakeupInfo)another).getTotalWakeupCount()){
                return -1;
            }else if(getTotalWakeupCount() ==((WakeupInfo)another).getTotalWakeupCount()){
                if(getTotalWakeupTime()> ((WakeupInfo)another).getTotalWakeupTime()){
                    return -1;
                }
            }
        }
        return 1;
    }
    
}
