package com.yhh.business.alarm;

import com.yhh.utils.FileUtils;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class FileAlarmManager extends AlarmManager{
    private String file;

    public FileAlarmManager(String file){
        super();
        this.file = file;
    }

    @Override
    String getDumpsysAlarm() {
        return FileUtils.readFromFile(file);
    }
}
