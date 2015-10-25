package com.yhh.analyser.provider;


import com.yhh.androidutils.FileUtils;

/**
 * Created by yuanhh1 on 2015/8/12.
 */
public class AlarmFileManager extends AlarmManager{
    private String file;

    public AlarmFileManager(String file){
        super();
        this.file = file;
    }

    @Override
    String getDumpsysAlarm() {
        return FileUtils.readFile(file) ;
    }
}
