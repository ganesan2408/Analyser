/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.provider;

import android.os.Handler;
import android.util.Log;

import com.yhh.analyser.view.activity.LogAnalyActivity;
import com.yhh.analyser.utils.ConstUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class LogSleepParser extends LogParser{
    private static String TAG =  ConstUtils.DEBUG_TAG+ "LogSleepAbtractLogSleepParser";
    private boolean DEBUG = true;

    private BufferedWriter bw;
    private String mDir;
    private  int curGroupIndex;
    public static String newFile ="_休眠与唤醒";
    
    public LogSleepParser(String dir){
        mDir = dir;
    }
    
    @Override
    public void parse(Handler handler){
        ArrayList<File> files = listTargetLog(mDir,ConstUtils.LOG_SLEEP);
        if(files ==null || files.size() <=0){
            return;
        }
      //保证解析过程由旧至新
        Collections.sort(files);
        for(File f: files){
            if(mIsParse){
                parseLog(f);
                handler.sendMessage(handler.obtainMessage(2));
            }
        }
    }
    
    private void parseLog(File log){
        BufferedReader br = null;
        String line;
        
        String curHHMMSS =null;
        String lastHHMMSS = null;
        
        String curYYMMDD = null;
        String lastYYMMDD = null;
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
            //初始化
            while (mIsParse &&(line = br.readLine()) != null) {
                if(line.startsWith("^") || line.startsWith("<")){
                    curHHMMSS = lastYYMMDD = line.substring(13,23);
                    curHHMMSS = lastHHMMSS = line.substring(24,32);
                    Log.d(TAG,"frist YYMMDD="+lastYYMMDD +",frist HHMMSS="+lastHHMMSS);
                    
                    String logPath = LogAnalyActivity.sLogCacheDir+"/"
                            + lastYYMMDD.replace("-", "") +newFile ;
                    Log.d(TAG,"generate new sleep log: " + logPath);
                    bw = new BufferedWriter(new FileWriter(logPath, true));
                    curGroupIndex = mChartTool.getGroupIndexByHhmmss(lastHHMMSS);
                    writeNewGroupTitle(curGroupIndex);
                    if(DEBUG){
                        Log.d(TAG, lastHHMMSS+":"+mChartTool.hhmmss2Index(lastHHMMSS));
                    }
                    break;
                }
            }
            
            while (mIsParse &&(line = br.readLine()) != null) {
                if(line.startsWith("^") || line.startsWith("<")){
                    curYYMMDD = line.substring(13,23);
                    curHHMMSS = line.substring(24,32);
                    
                    if(!curHHMMSS.equals(lastHHMMSS)){
                        writeTimePoint(mChartTool.hhmmss2Index(lastHHMMSS));
                        lastHHMMSS = curHHMMSS;
                    }
                    
                    if(!curYYMMDD.equals(lastYYMMDD)){
//                        writeTimePoint(mChartTool.hhmmss2Index(lastHHMMSS));
                        bw.flush();
                        bw.close();
                        
                        String logPath = LogAnalyActivity.sLogCacheDir+"/"
                                + curYYMMDD.replace("-", "") +newFile ;
                        Log.d(TAG,"generate new sleep log: " + logPath);
                        bw = new BufferedWriter(new FileWriter(logPath,true));
                        curGroupIndex = mChartTool.getGroupIndexByHhmmss(curHHMMSS);
                        writeNewGroupTitle(curGroupIndex);
                        lastYYMMDD = curYYMMDD;
                        lastHHMMSS = curHHMMSS;
                    }
                }else if(line.startsWith(">")){
                    curYYMMDD = line.substring(13,23);
                    curHHMMSS = line.substring(24,32);
                    if(curYYMMDD.equals(lastYYMMDD)){
                        if(!curHHMMSS.equals(lastHHMMSS)){
                            writeTimePeriod(lastHHMMSS,curHHMMSS);
                            lastHHMMSS = curHHMMSS;
                        }
                    }else {
//                        writeTimePoint(mChartTool.hhmmss2Index(lastHHMMSS));
                        bw.flush();
                        bw.close();
                        String logPath = LogAnalyActivity.sLogCacheDir+"/"
                                + curYYMMDD.replace("-", "") +newFile ;
                        Log.d(TAG,"generate new sleep log: " + logPath);
                        bw = new BufferedWriter(new FileWriter(logPath,true));
                        curGroupIndex = mChartTool.getGroupIndexByHhmmss(curHHMMSS);
                        writeNewGroupTitle(curGroupIndex);
                        lastYYMMDD = curYYMMDD;
                        lastHHMMSS = curHHMMSS;
                    }
                }
            }
            if(bw !=null) {
                bw.flush();
                bw.close();
            }
        } catch (Exception e) {
            Log.e(TAG,"init Sleep log failure.",e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init Sleep log close failure.");
                }
            }
        }
    }
    
    private void writeNewGroupTitle(int groupIndex){
        try {
            bw.write("#"+groupIndex);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeTimePoint(int secondsIndex){
        int tmpGroup = mChartTool.getGroupIndexBySeconds(secondsIndex);
        if(tmpGroup != curGroupIndex){
            curGroupIndex = tmpGroup;
            writeNewGroupTitle(curGroupIndex);
        }
        try {
            bw.write(String.valueOf(secondsIndex));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void writeTimePeriod(String startTime, String endTime){
        int start = mChartTool.hhmmss2Index(startTime);
        int end = mChartTool.hhmmss2Index(endTime);
        if(end -start >1){
            int curIndex = start +1;
            while(curIndex <end){
                writeTimePoint(curIndex);
                curIndex++;
            }
        }
    }
}
