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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;
import android.util.Log;

import com.yhh.chart.base.LogParser;
import com.yhh.log.analyser.MainLogAnalyser;
import com.yhh.utils.ConstUtils;

public class LogcatParser extends LogParser{
    private static String TAG =  ConstUtils.DEBUG_TAG+ "LogcatParser";
    private boolean DEBUG = true;
    
    private String mDir;
    public static String newFile ="AppStats";
    
    ArrayList<String> foregroundTimes = new ArrayList<String>();
    ArrayList<String> foregroundApps = new ArrayList<String>();
    String curForeApp = " ";
    String lastForeApp = " ";
    
    public LogcatParser(String dir){
        mDir = dir;
    }
    
    @Override
    public void parse(Handler handler){
        super.parse();
        ArrayList<File> files = listTargetLog(mDir,ConstUtils.LOG_LOGCAT);
        if(files ==null || files.size() <=0){
            return;
        }
        
        //保证解析过程由旧至新
        Collections.sort(files, Collections.reverseOrder());
        for(File f: files){
            if(mIsParse){
                parseLogcat(f);
                handler.sendMessage(handler.obtainMessage(2));
            }
        }
        write2File(foregroundTimes, foregroundApps);
    }
    
    private void parseLogcat(File log){
        BufferedReader br = null;
        String line = null;
        String timeRegex = "[0-2]\\d:[0-5]\\d:[0-5]\\d";
        Pattern timePattern = Pattern.compile(timeRegex);
        Matcher matcher;
        
        String ACTIVITY_MANAGER = "ActivityManager";
        String APP_START = " START";
        
        String PM_TAG = "PowerManagerService";
        String SLEEP_TAG = " Going to sleep";
        String WAKEUP_TAG = " Waking up from dozing";
        boolean isNewest = log.getName().equals("logcat");
        String tmpLine = null;
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
            
            long startTime = 0;
            if(DEBUG){
                startTime = System.currentTimeMillis();
            }
            
            while (mIsParse && (line = br.readLine()) != null) {
                 if(line.contains(ACTIVITY_MANAGER) && line.contains(APP_START)){
                     int startIndex = -1;
                     int endIndex = -1;
                     String curTime = " ";
                     
                     matcher = timePattern.matcher(line);
                     if(matcher.find()){
                         curTime = matcher.group();
                     }
                     
                     startIndex = line.indexOf("cmp=");
                     if(startIndex != -1){
                         endIndex = line.indexOf("/", startIndex+4);
                         if(endIndex != -1){
                             curForeApp = line.substring(startIndex + 4 , endIndex);
                             if(!curForeApp.equals(lastForeApp)){
                                 lastForeApp = curForeApp;
                                 foregroundTimes.add(curTime);
                                 foregroundApps.add(curForeApp);
                             }
                         }
                     }
                 }else if(line.contains(PM_TAG)){
                     if(line.contains(SLEEP_TAG)){
                         String curTime = " ";
                         matcher = timePattern.matcher(line);
                         if(matcher.find()){
                             curTime = matcher.group();
                             foregroundTimes.add(curTime);
                             foregroundApps.add("SLEEP");
                         }
                     }else if(line.contains(WAKEUP_TAG)){
                         String curTime = " ";
                         matcher = timePattern.matcher(line);
                         if(matcher.find()){
                             curTime = matcher.group();
                             foregroundTimes.add(curTime);
                             foregroundApps.add("WAKEUP");
                         }
                     };
                 }
                 tmpLine = line;
            }
            
            if(isNewest){
                Log.i(TAG,"LAST LINE="+tmpLine);
                String curTime = " ";
                matcher = timePattern.matcher(tmpLine);
                if(matcher.find()){
                    curTime = matcher.group();
                    foregroundTimes.add(curTime);
                    foregroundApps.add("END");
                }
            }
            
            if(DEBUG){
                Log.i(TAG,"parse "+log.getName()+" COMPLETE: "+ ( System.currentTimeMillis() -startTime)/1000.0+"s");
            }
            
        } catch (Exception e) {
            Log.e(TAG,"Init logcat failure.",e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG,"init logcat close failure.");
                }
            }
        }
    }
    
    protected void write2File(ArrayList<String> time, ArrayList<String> data){
        StringBuilder sb = new StringBuilder();
        int len = data.size();
        for(int i=0;i<len;i++){
            sb.append(time.get(i));
            sb.append("#");
            sb.append(data.get(i));
            sb.append("\n");
        }
        
        try {
            String logPath = MainLogAnalyser.sLogCacheDir+"/"+newFile;
            bw = new BufferedWriter(new FileWriter(logPath, true));
            bw.write(sb.toString());
            bw.close();
        } catch (IOException e) {
            Log.e(TAG,"write2File error.", e);
        }
    }
}
