package com.yhh.analyser.provider;

import android.content.Context;

import com.yhh.analyser.bean.AlarmBean;
import com.yhh.analyser.bean.AlarmType;
import com.yhh.androidutils.AppUtils;
import com.yhh.androidutils.DebugLog;
import com.yhh.androidutils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public abstract class AlarmManager {
    private String elapsedTime;
    private List<AlarmBean> alarmBeanList;

    public AlarmManager(){
        alarmBeanList = new ArrayList<AlarmBean>();
    }

    /**
     * 开始解析alarm信息
     *
     */
    public boolean parse(Context context){
        String rawInfo = getDumpsysAlarm();

        if(rawInfo ==null){
            DebugLog.e("dumpsys alarm is null");
            return false;
        }
        String[] rawInfoArr = rawInfo.split("\\n");
        DebugLog.d(rawInfoArr.length+"");
        int beginLine = getAlarmStatsLine(rawInfoArr);
        if(beginLine == -1){
            DebugLog.e("dumpsys alarm don't have useful information.");
            return false;
        }
        setelapsedTime(rawInfoArr[1]);

        int len = rawInfoArr.length;

        AlarmBean alarmBean = null;
        for(int i=beginLine+1; i< len - 1; i++){
            if(isAlarmTitle(rawInfoArr[i])){
                if(alarmBean !=null){
                    alarmBeanList.add(alarmBean);
                }
                alarmBean = getAlarmInfo(context, rawInfoArr[i]);
            }else{
                AlarmType alarmType = getAlarmType(rawInfoArr[i]);
                alarmBean.addAlarmType(alarmType);
            }

        }
        if(alarmBean !=null){
            alarmBeanList.add(alarmBean);
        }
        DebugLog.i(""+getAlarmList().size());
        return true;
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
    private AlarmBean getAlarmInfo(Context context, String line){
        AlarmBean alarmBean = new AlarmBean();
        if(StringUtils.isBlank(line)){
            DebugLog.e(line+"is blank");
            return null;
        }

        String[] arr = line.trim().split("\\s+");
        if(arr !=null && arr.length==5){
            String[] subArr = arr[0].trim().split(":");
            alarmBean.setUid(subArr[0]);
            alarmBean.setName(subArr[1]);
            alarmBean.setRunningTime(arr[1].substring(1));
            alarmBean.setWakeups(Integer.valueOf(arr[3].trim()));
            alarmBean.setAppName(AppUtils.getAppName(context, alarmBean.getName()));
        }

        return alarmBean;
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
    public  List<AlarmBean> getAlarmList(){
        return alarmBeanList;
    }
}
