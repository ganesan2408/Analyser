package com.yhh.analyser.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/17.
 */
public class MonitorChoice {
    private List<Boolean> mSettingList;
    public static final int COUNT = 13;

    private static MonitorChoice mChoice;

    public static MonitorChoice getInstance(){
        if(null == mChoice){
            mChoice = new MonitorChoice();
        }
        return mChoice;
    }

    private MonitorChoice(){
        mSettingList = new ArrayList<>(COUNT);
        initAllChecked();
    }


    public List<Boolean> getCheckedList(){
        return mSettingList;
    }

    public void setItemChecked(int index, boolean checked){
        mSettingList.set(index, checked);
    }

    public void setAllChecked(boolean checked){
        for(int i=0; i<COUNT; i++){
            setItemChecked(i, checked);
        }
    }

    private void initAllChecked(){
        for(int i=0; i<COUNT; i++){
            mSettingList.add(false);
        }
    }



}
