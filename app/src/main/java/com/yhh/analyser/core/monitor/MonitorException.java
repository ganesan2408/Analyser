package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.bean.TempInfo;
import com.yhh.analyser.core.MonitorFactory;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorException extends Monitor {


    private int mThresholdValue;
    private MonitorExceptionStat monitorExceptionStat;

    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorFactory.TYPE_EXCEPTION
        };
    }

    @Override
    public String getFileType() {
        return "_Exception";
    }

    public MonitorException(Context context, int thresValue) {
        super(context);
        mThresholdValue = thresValue*10;
        monitorExceptionStat = MonitorExceptionStat.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        monitorExceptionStat.clear();
    }

    @Override
    public String monitor() {
        if (!isSatisfy()) {
            return "";
        }
        monitorExceptionStat.beginStatistic();

        return monitorExceptionStat.getTopString();
    }

    private boolean isSatisfy() {
        if(TempInfo.getTemp() >= mThresholdValue){
            return true;
        }
        return false;
    }
}
