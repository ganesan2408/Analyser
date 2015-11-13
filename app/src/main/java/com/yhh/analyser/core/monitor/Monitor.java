package com.yhh.analyser.core.monitor;

import android.content.Context;
import android.util.Log;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.androidutils.DebugLog;
import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.TimeUtils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Monitor
 *
 * Created by yuanhh1 on 2015/8/19.
 */
public abstract class Monitor {
    private static final String TAG = LogUtils.DEBUG_TAG+ "Monitor";

    private BufferedWriter bw;

    protected Context mContext;
    /**标题 */
    private String[] itemTitles;
    /** 单位*/
    private String[] itemUnitTitles;
    /**监控项 */
    private Integer[] monitorItems;

    public Monitor(Context context){
        mContext = context;
    }


    public void onStart(){
        monitorItems = getItems();
        initResources();

        createMonitorFile();
        writeTitle2File();
    }

    public void onDestroy(){
        close();
    }

    /**
     * 获取监控文件的名称
     *
     * @return
     */
    public String getFileType(){
        return  "";
    }

    /**
     * 获取监控文件的头信息
     */
    public abstract Integer[] getItems();

    /**
     * 开始监控
     */
    public abstract String monitor();

    /**
     * 将信息持久化到文件中
     *
     * @param infoList
     */
    protected void write2File(ArrayList<String> infoList){
        try {
            if(bw !=null) {
                bw.write(getContentBody(infoList));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getItemName(int index){
        return itemTitles[index];
    }

    public String getItemUnit(int index){
        return itemUnitTitles[index];
    }


    /**
     * 获取监控文件中的监控信息标题
     *
     * @return
     */
    private String getContentTitle(){
        int len = monitorItems.length;
        DebugLog.d("content title size= " + len);
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<len-1; i++){
            sb.append(monitorItems[i] + ",");
        }
        sb.append(monitorItems[len - 1] + LogUtils.LINE_END);
        return sb.toString();
    }

    /**
     * 获取监控文件中的监控信息内容
     *
     * @param infoList
     * @return
     */
    private String getContentBody(ArrayList<String> infoList){
        int len = infoList.size();
        StringBuffer sb = new StringBuffer();
        sb.append(TimeUtils.getCurrentTime(TimeUtils.DATE_TIME_FORMAT)+",");
        for(int i=0; i<len; i++){
            sb.append(infoList.get(i) + ",");
        }
        sb.append(LogUtils.LINE_END);
        return  sb.toString();
    }

    /**
     * 获取监控文件中的监控信息内容
     *
     * @param infoList
     * @return
     */
    protected String getFloatBody(ArrayList<String> infoList){
        int len = infoList.size();
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<len; i++){
            sb.append(getItemName(monitorItems[i]) + ":");
            sb.append(infoList.get(i));
            sb.append(getItemUnit(monitorItems[i]) + "\n");
        }
        return  sb.toString();
    }


    private void initResources(){
        itemTitles = mContext.getResources().getStringArray(R.array.monitor_items);
        itemUnitTitles = mContext.getResources().getStringArray(R.array.monitor_unit_items);
    }


    private void createMonitorFile() {
        String fileType = getFileType()==null ? "" : getFileType();
        String fileFullName = AppConfig.MONITOR_DIR + "/" + TimeUtils.getCurrentTime(TimeUtils.DATETIME_UNDERLINE_FORMAT) + fileType;

        try {
            FileUtils.createFile(fileFullName);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileFullName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (bw != null) {
                bw.close();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void writeTitle2File(){
        try {
            if(bw != null) {
                bw.write(getContentTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
