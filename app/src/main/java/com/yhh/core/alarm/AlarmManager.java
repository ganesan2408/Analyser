package com.yhh.core.alarm;

import com.yhh.model.AlarmInfo;
import com.yhh.model.AlarmType;
import com.yhh.utils.DebugLog;
import com.yhh.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public abstract class AlarmManager {
    private String elapsedTime;
    private List<AlarmInfo> alarmInfoList;

    public AlarmManager(){
        alarmInfoList = new ArrayList<AlarmInfo>();
    }

    /**
     * 开始解析alarm信息
     *
     */
    public void parse(){
        String rawInfo = getDumpsysAlarm();

        if(rawInfo ==null){
            DebugLog.e("dumpsys alarm is null");
            return;
        }
        String[] rawInfoArr = rawInfo.split("\\n");
        DebugLog.d(rawInfoArr.length+"");
        int beginLine = getAlarmStatsLine(rawInfoArr);
        if(beginLine == -1){
            DebugLog.e("dumpsys alarm don't have useful information.");
            return;
        }
        setelapsedTime(rawInfoArr[1]);

        int len = rawInfoArr.length;

        AlarmInfo alarmInfo = null;
        for(int i=beginLine+1; i< len - 1; i++){
            if(isAlarmTitle(rawInfoArr[i])){
                if(alarmInfo !=null){
                    alarmInfoList.add(alarmInfo);
                }
                alarmInfo = getAlarmInfo(rawInfoArr[i]);
            }else{
                AlarmType alarmType = getAlarmType(rawInfoArr[i]);
//                DebugLog.d(alarmType.toString());
            }

        }
        if(alarmInfo !=null){
            alarmInfoList.add(alarmInfo);
        }
        DebugLog.i(""+getAlarmList().size());
    }

    /**
     * 获取Dumpsy Alarm的原始信息
     *
     * @return
     */
    abstract String getDumpsysAlarm();

    /**
     * 判断是否含有Alarm Stats字段
     *
     * @param line
     * @return
     */
    private boolean isAlarmStats(String line){
        String alarmStats = "Alarm Stats:";
        if(!StringUtils.isBlank(line)){
            if(line.trim().startsWith(alarmStats)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否以 +号开始；
     *      当以+号开始，则返回false;
     *
     * @param line
     * @return
     */
    private boolean isAlarmTitle(String line){
        if(!StringUtils.isBlank(line)){
            if(line.trim().startsWith("+")){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取Alarm Stats 起始的行号
     *
     * @param lines
     * @return
     */
    private int getAlarmStatsLine(String[] lines){
        int len = lines.length;
        for(int i=0; i< len - 1; i++){
            if(isAlarmStats(lines[i])){
                return i;
            }
        }
        return -1;
    }


    /**
     * 解析Alarm Info
     *
     * @param line
     * @return
     */
    private AlarmInfo getAlarmInfo(String line){
        AlarmInfo alarmInfo = new AlarmInfo();
        if(StringUtils.isBlank(line)){
            DebugLog.e(line+"is blank");
            return null;
        }

        String[] arr = line.trim().split("\\s+");
        if(arr !=null && arr.length==5){
            String[] subArr = arr[0].trim().split(":");
            alarmInfo.setUid(subArr[0]);
            alarmInfo.setName(subArr[1]);
            alarmInfo.setRunningTime(arr[1].substring(1));
            alarmInfo.setWakeups(Integer.valueOf(arr[3].trim()));
        }

        return alarmInfo;
    }

    /**
     * 解析Alarm Type
     *
     * @param line
     * @return
     */
    private AlarmType getAlarmType(String line){
        AlarmType alarmType = new AlarmType();
        if(StringUtils.isBlank(line)){
            DebugLog.e(line+"is blank");
            return null;
        }

        String[] arr = line.trim().split("\\s+");
        if(arr != null && arr.length ==6){
            alarmType.setRunningTime(arr[0].trim());
            alarmType.setWakeups(Integer.valueOf(arr[1].trim()));
            int index = arr[5].indexOf(":");
            alarmType.setType(arr[5].substring(index + 1));
        }
        return  alarmType;
    }

    /**
     * 设置运行时间
     *
     * @param line
     */
    private void setelapsedTime(String line){
        int index = line.lastIndexOf("+");
        elapsedTime = line.substring(index+1).trim();
    }


    /**
     * 获取运行时间
     *
     * @return
     */
    public String getElapsedTime(){
        return  elapsedTime;
    }

    /**
     * 获取格式化的Alarm信息
     *
     * @return
     */
    public  List<AlarmInfo> getAlarmList(){
        return alarmInfoList;
    }
}
