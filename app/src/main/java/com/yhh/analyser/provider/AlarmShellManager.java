package com.yhh.analyser.provider;

import com.yhh.analyser.config.CommandConfig;
import com.yhh.analyser.utils.ShellUtils;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AlarmShellManager extends AlarmManager{

    public AlarmShellManager(){
        super();
    }

    @Override
    String getDumpsysAlarm() {
        return ShellUtils.execCommand(CommandConfig.DUMPSYS_ALARM, false).successMsg;
    }


}
