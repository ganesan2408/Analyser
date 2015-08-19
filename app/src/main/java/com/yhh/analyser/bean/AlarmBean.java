package com.yhh.analyser.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AlarmBean implements Comparable<AlarmBean>{

    private String name;

    private String uid;

    /** 唤醒次数 */
    private int wakeups;

    /** 运行时间*/
    private String runningTime;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /** App名称*/
    private String appName;

    private List<AlarmType> alarmTypeList;


    public AlarmBean(){
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

    public String getAlarmTypeString(){
        StringBuffer sb = new StringBuffer();
        for(AlarmType type: alarmTypeList){
            sb.append(type.toString()).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(AlarmBean another) {
        if(getWakeups()>another.getWakeups()){
            return  -1;
        }else if(getWakeups()<another.getWakeups()){
            return  1;
        }else {
            return 0;
        }

    }
}
