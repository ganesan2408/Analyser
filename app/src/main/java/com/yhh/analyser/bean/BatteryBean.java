/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;

public class BatteryBean {

    private String mLevel;
    private String mVoltage;
    private String mTemperature;
    
    public String getLevel(){
        return mLevel;
    }

    public void setLevel(String level){
        this.mLevel = level;
    }
    
    public String getVoltage(){
        return mVoltage;
    }

    public void setVoltage(String voltage){
        this.mVoltage = voltage;
    }
    
    public String getTemperature(){
        return mTemperature;
    }

    public void setmTemperature(String temperature){
        this.mTemperature = temperature;
    }
    

}
