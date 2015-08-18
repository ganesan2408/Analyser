/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.provider;

import android.os.Handler;
import android.util.Log;

import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.ChartTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LogParser{
    private static String TAG =  ConstUtils.DEBUG_TAG+ "LogParser";

    protected  boolean mIsParse = true;
    
    protected ChartTool mChartTool = ChartTool.getInstance();
    
    protected void addLine2File(BufferedWriter bw, String dayTime, int[] data){
        if(dayTime == null){
            return ;
        }
        StringBuilder sb = new StringBuilder();
        
        int minuteNum = mChartTool.hhmm2Index(dayTime);
        sb.append(minuteNum);
        for(int d: data){
            sb.append("#"+d);
        }
        
        try {
            bw.write(sb.toString());
            bw.newLine();
        } catch (IOException e) {
            Log.e(TAG,"addLine2File error.", e);
        }
    }
    
    protected ArrayList<File> listTargetLog(String dir,String type){
        ArrayList<File> fileList = new ArrayList<File>();
        File parentDir = new File(dir);  
        if (parentDir.exists()) {  
           File[] files = parentDir.listFiles();
           for(File f:files){
               if(f.getName().contains(type)){
                   fileList.add(f);
               }
           }
           return fileList;
        }
        return null;
    }
   
    public void parse(Handler handler){

    }
}
