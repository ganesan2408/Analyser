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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * parse battery log , and write useful information into file.
 * 
 */
public class LogBatteryParser extends LogParser{
    private static String TAG =  ConstUtils.DEBUG_TAG+ "LogBatteryParser";
    private boolean DEBUG = true;

    private BufferedWriter bw;
    private String mDir;
    public static String newFile ="_电池";
    
    public LogBatteryParser(String dir){
        mDir = dir;
    }
    

    @Override
    public void parse(Handler handler){
        ArrayList<File> files = listTargetLog(mDir, ConstUtils.LOG_BATTERY);
        if(files ==null || files.size() <=0){
            return;
        }
        
        //保证解析过程由旧至新
        Collections.sort(files);
        for(File f: files){
            if(mIsParse){
                parseBatteryLog(f);
                handler.sendMessage(handler.obtainMessage(2));
            }
        }
    }
    
    private void parseBatteryLog(File log){
        BufferedReader br = null;
        int[] usefulData = new int[5];
        String line;
        String regex = "^\\d{4}[/]\\d{2}[/]\\d{2}[\\s]+[0-2]\\d:[0-5]\\d";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        boolean haveAddTime = false;
        
        String TEMP = "temperature";
        String LEVEL = "level";
        String STATUS = "status";
        String HEALTH = "health";
        String VOLTAGE = "voltage";
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
            String curTime =null;
            String lastTime = null;
            String curYYMMDD = null;
            
            ArrayList<Integer> lastStatus = new ArrayList<Integer>();
            ArrayList<Integer> lastHealth = new ArrayList<Integer>();
            ArrayList<Integer> lastLevels = new ArrayList<Integer>();
            ArrayList<Integer> lastVoltage = new ArrayList<Integer>();
            ArrayList<Integer> lastTemps = new ArrayList<Integer>();
            
            long startTime = 0;
            if(DEBUG){
                startTime = System.currentTimeMillis();
            }
            while (mIsParse && (line = br.readLine()) != null) {
                if(!haveAddTime){
                     matcher = pattern.matcher(line);
                     if(matcher.find()){
                         curTime = matcher.group();
                         if(lastTime ==null){
                             lastTime = curTime;
                             curYYMMDD = curTime.substring(0,10);
                             String logPath = LogAnalyActivity.sLogCacheDir+"/"
                                     + curYYMMDD.replace("/", "") +newFile ;
                             Log.d(TAG,"generate new battery log: " + logPath);
                             bw = new BufferedWriter(new FileWriter(logPath, true));
                         }
                         
                         if(!curTime.equals(lastTime)){
                             //电量： %, 温度, status, health
                             usefulData[0] = mChartTool.getAvg(lastLevels);
                             usefulData[1] = mChartTool.getAvg(lastTemps);
                             usefulData[2] = mChartTool.getAvg(lastStatus);
                             usefulData[3] = mChartTool.getAvg(lastHealth);
                             usefulData[4] = mChartTool.getAvg(lastVoltage);
                             
                             addLine2File(bw, lastTime.substring(11),usefulData);
                             lastTime = curTime;
                             
                             lastLevels.clear();
                             lastTemps.clear();
                             lastStatus.clear();
                             lastHealth.clear();
                             lastVoltage.clear();
                             
                             if(!curTime.startsWith(curYYMMDD)){
                                 bw.flush();
                                 bw.close();
                                 curYYMMDD = curTime.substring(0,10);
                                 String logPath = LogAnalyActivity.sLogCacheDir+"/"
                                         + curYYMMDD.replace("/", "") +newFile ;
                                 Log.d(TAG,"generate new battery log: " + logPath);
                                 bw = new BufferedWriter(new FileWriter(logPath,true));
                             }
                         }
                         haveAddTime = true;
                     }
                }else{
                    if(line.contains(STATUS)){
                        int tStatus = Integer.valueOf(line.split(":")[1].trim());
                        lastStatus.add(tStatus);
                    }else if(line.contains(HEALTH)){
                        int health = Integer.valueOf(line.split(":")[1].trim());
                        lastHealth.add(health);
                    }else if(line.contains(LEVEL)){
                        int level = Integer.valueOf(line.split(":")[1].trim());
                        lastLevels.add(level);
                    }else if(line.contains(VOLTAGE)){
                        int voltage = Integer.valueOf(line.split(":")[1].trim());
                        lastVoltage.add(voltage);
                    }else if(line.contains(TEMP)){
                        int fTemp = Integer.valueOf(line.split(":")[1].trim());
                        lastTemps.add(fTemp);
                        haveAddTime = false;
                    }
                }
            }
            bw.flush();
            bw.close();
            
            if(DEBUG){
                Log.i(TAG,"parse "+log.getName()+" COMPLETE: "+ ( System.currentTimeMillis() -startTime)/1000.0+"s");
            }
            
        } catch (Exception e) {
            Log.e(TAG,"Init battery log failure.",e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init battery log close failure.");
                }
            }
        }
    }
}
