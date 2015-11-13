/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.provider;

import android.util.Log;

import com.yhh.analyser.utils.ChartTool;
import com.yhh.analyser.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public abstract class AbtractLogSleepParser {
    private static String TAG =  LogUtils.DEBUG_TAG+ "AbtractLogSleepParser";
    protected String mLogDir;
    protected String mLogNameType;
    protected ChartTool mChartTool = ChartTool.getInstance();
    
    protected int mDays =0;
    protected ArrayList<String> mYYMMDD;
    
    public static int DATA_FULL = 0;
    public static int DATA_NEWEST = 1;
    public static int DATA_SELECT = 2;
    
    protected static boolean mIsParse=true;
    
    public AbtractLogSleepParser(String logDir, String logType){
        mYYMMDD = new ArrayList<String>();
        mLogDir = logDir;
        mLogNameType = logType;
    }
    
    public static void stopParse(){
        mIsParse = false;
    }
    
    public boolean parse(int dataType){
        if(dataType == DATA_FULL){
            parseAll();
        }else if(dataType == DATA_NEWEST){
            return parseNewest();
        }
        return true;
    }
    
    public abstract void parseTarget(File log);
    
    private void parseAll(){
        mIsParse = true;
        ArrayList<File> files = listTargetLog(mLogNameType);
        Collections.sort(files);
        for(File f: files){
            if(mIsParse){
                parseTarget(f);
            }
        }
    }
    
    private boolean parseNewest(){
        String path = LogUtils.getNewestLog(mLogDir, mLogNameType);
        if(path ==null){
            return false;
        }
        Log.i(TAG,"parseNewest PATH:"+path);
        File f = new File(path);
        parseTarget(f);
        return true;
    }
    
    
    private ArrayList<File> listTargetLog(String type){
        ArrayList<File> fileList = new ArrayList<File>();
        File dir = new File(mLogDir);  
        if (dir.exists()) {  
           File[] files = dir.listFiles();
           for(File f:files){
               if(f.getName().contains(type)){
                   fileList.add(f);
               }
           }
           return fileList;
        }
        return null;
    }
    
    protected int findDayIndex(String yymmdd){
        for(int i= mDays-1;i>=0;i--){
            if(mYYMMDD.get(i).equals(yymmdd)){
                return i;
            }
        }
        return -1;
    }
    
    public int getDays(){
        return mDays;
    }
    
    public ArrayList<String> getDayList(){
        return mYYMMDD;
    }
    
    public void setLogDir(String dir){
        this.mLogDir = dir;
    }
}
