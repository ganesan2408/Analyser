/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;

import java.util.ArrayList;

public class LogInfo {
    public static final int LINES_PRE_PAGE = 100;
    private ArrayList<ArrayList<String>> logList;
    private int currentNum;
    private int maxNum;
    
    public LogInfo(){
        logList = new ArrayList<ArrayList<String>>();
    }
    
    public void setCurrent(int current){
        this.currentNum = Math.max(0, Math.min(maxNum-1,current));
    }
    
    public void nextCurrent(){
        if(currentNum < maxNum-1){
            currentNum++;
        }
    }
    
    public void preCurrent(){
        if(currentNum>0){
            currentNum--;
        }
    }
    
    public int getCurrentNum(){
        return currentNum;
    }
    
    public int getMaxNum() {
        return maxNum;
    }
    
    public void setMax(int max){
        this.maxNum = max >0? max:0;
    }
    
    // 增加一组log
    public void appendLog(ArrayList<String> logArr){
        logList.add(logArr);
        maxNum++;
    }
    
    //获取当前页的一组log
    public ArrayList<String> getCurrentLog(){
        if(logList ==null || logList.size() <=0){
            return null;
        }
        return logList.get(currentNum);
    }
    
    // 获取指定页码的一组log
    public ArrayList<String> getLog(int id){
        if(id >=0 && id<= maxNum-1){
            return logList.get(id);
        }else{
            return null;
        }
    }
    
    //获取选中log的对应行号
    public int getRowFromLine(String line){
        int row = 0;
        int start = line.indexOf("[");
        int end = line.indexOf("]");
        String tmp = line.substring(start+1, end);
        row = Integer.valueOf(tmp);
        return row;
    }
    
    //清空log
    public void clearAll(){
        this.setCurrent(0);
        this.setMax(0);
        this.logList.clear();
    }
    
    public void updateLog(ArrayList<ArrayList<String>> logList) {
        if(logList ==null || logList.size() <=0){
            return;
        }
        this.logList = logList;
        this.maxNum = logList.size();
    }
    
}
