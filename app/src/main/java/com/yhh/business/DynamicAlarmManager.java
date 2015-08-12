package com.yhh.business;

import com.yhh.constant.CommandConst;
import com.yhh.utils.ShellUtils;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class DynamicAlarmManager extends AlarmManager{

    public DynamicAlarmManager(){
        super();
    }

    @Override
    String getDumpsysAlarm() {
        return ShellUtils.execCommand(CommandConst.DUMPSYS_ALARM, false).successMsg;
    }


}
