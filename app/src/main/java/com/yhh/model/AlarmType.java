package com.yhh.model;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AlarmType {

    private String type;

    private int wakeups;

    private String runningTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWakeups(){
        return wakeups;
    }

    public void setWakeups(int wakeups){
        this.wakeups = wakeups;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    @Override
    public String toString() {
        return " type:"+getType()+", run:"+getRunningTime() +", wakeups:"+ getWakeups();
    }
}
