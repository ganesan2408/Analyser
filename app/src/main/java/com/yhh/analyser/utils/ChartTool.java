/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import java.util.ArrayList;

public class ChartTool {
    private ArrayList<String> xValues;
    private static ChartTool mChartTool;
    public static int secendsPerGroup = 14400;
    public static  int pictures = 6;
    
    private ChartTool(){
        init();
    }

    public static ChartTool getInstance(){
        if(null == mChartTool){
            mChartTool = new ChartTool();
        }
        return mChartTool;
    }
    
    private void init(){
        xValues = new ArrayList<String>();
        for(int i=0;i<24;i++){
            int j=0;
            while(j<10){
                xValues.add(i+":0"+j);
                j++;
            }
            while(j<60){
                xValues.add(i+":"+j);
                j++;
            }
        }
    }
    
    public  ArrayList<String> gethhmmssAxis(){
        ArrayList<String> hhmmssValues = new ArrayList<String>();
        for(int i=0;i<24;i++){
            int j=0;
            while(j<10){
                int k=0;
                while(k<10){
                    hhmmssValues.add(i+":0"+j+":0"+k);
                    k++;
                }
                while(k<60){
                    hhmmssValues.add(i+":0"+j+":"+k);
                    k++;
                }
                j++;
            }
            while(j<60){
                int k=0;
                while(k<10){
                    hhmmssValues.add(i+":"+j+":0"+k);
                    k++;
                }
                while(k<60){
                    hhmmssValues.add(i+":"+j+":"+k);
                    k++;
                }
                
                j++;
            }
        }
        return hhmmssValues;
    }
    
    public  ArrayList<ArrayList<String>> gethhmmssAxisGroup(){
        ArrayList<ArrayList<String>> hhmmssValues = new ArrayList<ArrayList<String>>();
        int hoursPerPicture = 4;
        for(int i=0; i<pictures; i++){
            ArrayList<String> axis = gethhmmssAxisPart(hoursPerPicture*i,hoursPerPicture);
            hhmmssValues.add(axis);
        }
        return hhmmssValues;
    }
    
    public int getGroupIndexBySeconds(int seconds){
        return seconds/secendsPerGroup;
    }
    
    public int getGroupIndexByHhmmss(String hhmmss){
        int seconds = hhmmss2Index(hhmmss);
        return seconds/secendsPerGroup;
    }
    
    
    private ArrayList<String> gethhmmssAxisPart(int start, int count){
        ArrayList<String> hhmmssAxis = new ArrayList<String>();
        for(int i=start;i<start+count;i++){
            int j=0;
            while(j<10){
                int k=0;
                while(k<10){
                    hhmmssAxis.add(i+":0"+j+":0"+k);
                    k++;
                }
                while(k<60){
                    hhmmssAxis.add(i+":0"+j+":"+k);
                    k++;
                }
                j++;
            }
            while(j<60){
                int k=0;
                while(k<10){
                    hhmmssAxis.add(i+":"+j+":0"+k);
                    k++;
                }
                while(k<60){
                    hhmmssAxis.add(i+":"+j+":"+k);
                    k++;
                }
                j++;
            }
        }
        return hhmmssAxis;
    }
    
    public ArrayList<String> getXAxisValues(){
        
        return xValues;
    }
    
    public int hhmm2Index(String hhmm){
        int index =-1;
        String[] tmp = hhmm.split(":");
        index = Integer.valueOf(tmp[0]) * 60 + Integer.valueOf(tmp[1]);
        return index;
    }
    
    public int hhmmss2Index(String hhmm){
        int index =-1;
        String[] tmp = hhmm.split(":");
        index = Integer.valueOf(tmp[0]) * 3600 + Integer.valueOf(tmp[1]) *60
                +Integer.valueOf(tmp[2]);
        return index;
    }
    
    public String index2hhmm(int index){
        String hhmm = null;
        int hh = index/60;
        int mm = index%60;
        if(mm<10){
            hhmm = hh+":0"+mm;
        }else{
            hhmm = hh+":"+mm;
        }
        return hhmm;
    }
    
    /**
     * 获取list的平均值
     * 
     * @param values
     * @return
     */
    public int getAvg(ArrayList<Integer> values){
          int len = values.size();
          if(len <=0){
              return 0;
          }
          int avg = 0;
          for(int i=0;i<len;i++){
              avg += values.get(i);
          }
          avg /=len;
          return avg;
      }
}
