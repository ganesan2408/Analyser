package com.yhh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AlarmInfo implements Comparable<AlarmInfo>{

    private String name;

    private String uid;

    private int wakeups;

    private String runningTime;

    private List<AlarmType> alarmTypeList;


    public AlarmInfo(){
        alarmTypeList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getWakeups() {
        return wakeups;
    }

    public void setWakeups(int wakeups) {
        this.wakeups = wakeups;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public List<AlarmType> getAllAlarmType(){
        return alarmTypeList;
    }

    public void addAlarmType(AlarmType alarmType){
        this.alarmTypeList.add(alarmType);
    }

    @Override
    public String toString() {
        return " uid="+getUid()+", name:"+getName() +",run:"+getRunningTime() +", wakeups:"+getWakeups();
    }

    public String toEasyString(){
        return getName()  +" ==> "+getWakeups();
    }

    @Override
    public int compareTo(AlarmInfo another) {
        if(getWakeups()>another.getWakeups()){
            return  -1;
        }else if(getWakeups()<another.getWakeups()){
            return  1;
        }else {
            return 0;
        }

    }
}
