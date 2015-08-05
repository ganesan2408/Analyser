/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.log.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.os.Handler;
import android.util.Log;

import com.yhh.chart.base.LogParser;
import com.yhh.log.analyser.MainLogAnalyser;
import com.yhh.utils.ConstUtils;

public class PmLogParser extends LogParser{
    private static String TAG =  ConstUtils.DEBUG_TAG+ "PmLogParser";
    private boolean DEBUG = true;
    
    private String mDir;
    public static String newFile ="PmInfo_";
    
    public PmLogParser(String dir){
        mDir = dir;
    }
    
    @Override
    public void parse(Handler handler){
        super.parse();
        ArrayList<File> files = listTargetLog(mDir,ConstUtils.LOG_PMLOG);
        if(files ==null || files.size() <=0){
            return;
        }
        
        //保证解析过程由旧至新
        Collections.sort(files, Collections.reverseOrder());
        for(File f: files){
            if(mIsParse){
                parsePmLog(f);
                handler.sendMessage(handler.obtainMessage(2));
            }
        }
    }
    
    private void parsePmLog(File log){
        int dataItemNums = 11;
        BufferedReader br = null;
        int[] usefulData = new int[dataItemNums];
        String line, curTime, lastTime = null, curYYMMDD = null;
        
        long startTime = System.currentTimeMillis();
        
        ArrayList<ArrayList<Integer>> lastdata = new ArrayList<ArrayList<Integer>>();
        for(int i=0 ;i< dataItemNums;i++){
            lastdata.add(new ArrayList<Integer>());
        }
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
            //获取初始时间
            while (mIsParse &&(line = br.readLine()) != null) {
                if(line.contains("CST")){
                    curTime = line.substring(4,16);
                    if(lastTime == null){
                        lastTime = curTime;
                        curYYMMDD = lastTime.substring(0,6);
                    }else if(!curTime.equals(lastTime)){
                        lastTime = curTime; 
                        curYYMMDD = lastTime.substring(0,6);
                        break;
                    }
                    while ( (line = br.readLine()) != null  && (!line.equals("")) ){ }//忽略后续行，直到遇到空行
                }
            }
            while ( (line = br.readLine()) != null  && (!line.equals("")) ){ }//忽略后续行，直到遇到空行
            
            String logPath2 = MainLogAnalyser.sLogCacheDir+"/"+newFile + curYYMMDD.replace("  ", "");
            bw = new BufferedWriter(new FileWriter(logPath2, true));
            
            while ((line = br.readLine()) != null) {
                     curTime = line.substring(4,16);
                     if(!curTime.equals(lastTime)){
                         //电流, 亮度, GPU Clock, CPU Clock
                         for(int i=0 ;i< dataItemNums;i++){
                             usefulData[i] = mChartTool.getAvg(lastdata.get(i));
                             lastdata.get(i).clear();
                         }
                         addLine2File(lastTime.substring(7), usefulData);
                         lastTime = curTime;
                         
                         if(!curTime.startsWith(curYYMMDD)){
                             bw.flush();
                             bw.close();
                             curYYMMDD = curTime.substring(0,6);
                             String logPath = MainLogAnalyser.sLogCacheDir+"/"+newFile 
                                     + curYYMMDD.replace("  ", "");
                             bw = new BufferedWriter(new FileWriter(logPath,true));
                         }
                     }

                     br.readLine(); // 忽略这一行： current_now, brightness, gpuclk...
                     
                     try{
                         lastdata.get(0).add(Integer.valueOf(br.readLine()));
                     }catch(Exception e){
                         lastdata.get(0).add(Integer.valueOf(br.readLine()));
                     }
                     lastdata.get(1).add(Integer.valueOf(br.readLine()));
                     lastdata.get(2).add(Integer.valueOf(br.readLine())/1000);
                     
                     for(int i=3;i< dataItemNums;i++){
                         lastdata.get(i).add(getCpuClk(br.readLine()));
                     }
                     while ( (line = br.readLine()) != null  && (!line.equals("")) ){ }//忽略后续行，直到遇到空行
            }
            
            if(DEBUG){
                Log.d(TAG, "parse "+log.getName()+" COMPLETE:"+ ( System.currentTimeMillis() -startTime)/1000.0+"s");
            }
        } catch (Exception e) {
            Log.e(TAG,"init PMLOG log failure.",e);
        } finally {
            if (br != null) {
                try {
                    bw.flush();
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init PMLOG log close failure.");
                }
            }
        }
    }
    
    private int getCpuClk(String line){
        int start = line.indexOf("(");
        if(start<0){
            return 0;
        }else{
            int end = line.indexOf(" ", start+1);
            String sub = line.substring(start+1, end);
            int clk = Integer.valueOf(sub);
            return clk;
        }
    }
}
