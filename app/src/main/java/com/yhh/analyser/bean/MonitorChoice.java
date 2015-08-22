package com.yhh.analyser.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/17.
 */
public class MonitorChoice {
    private List<Boolean> mSettingList;
    private int mCount = 13;

    private static MonitorChoice mChoice;

    public static MonitorChoice getInstance(){
        if(null == mChoice){
            mChoice = new MonitorChoice();
        }
        return mChoice;
    }

    private MonitorChoice(){
        mSettingList = new ArrayList<>(mCount);
        initAllChecked();
    }

    public Integer[] getSysItems(){
        ArrayList<Integer> checkedItem = new ArrayList<>();
        for (int i = 2; i < mCount; i++) {
            if(mSettingList.get(i)){
                checkedItem.add(i);
            }
        }
        int len = checkedItem.size();
        Integer[] checkedArr = new Integer[len];
        checkedItem.toArray(checkedArr);
        return checkedArr;
    }

    public Integer[] getAppItems(){
        ArrayList<Integer> checkedItem = new ArrayList<>();
        for (int i = 0; i < mCount; i++) {
            if(mSettingList.get(i)){
                checkedItem.add(i);
            }
        }
        int len = checkedItem.size();
        Integer[] checkedArr = new Integer[len];
        checkedItem.toArray(checkedArr);
        return checkedArr;
    }

    public int getCount(){
        return mCount;
    }

    public List<Boolean> getCheckedList(){
        return mSettingList;
    }

    public void setItemChecked(int index, boolean checked){
        mSettingList.set(index, checked);
    }

    public void setAllChecked(boolean checked){
        for(int i=0; i< mCount; i++){
            setItemChecked(i, checked);
        }
    }

    private void initAllChecked(){
        for(int i=0; i< mCount; i++){
            mSettingList.add(false);
        }
    }



}
