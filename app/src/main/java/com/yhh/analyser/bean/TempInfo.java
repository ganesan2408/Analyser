/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;

import com.yhh.analyser.utils.FileUtils;



public class TempInfo {
    private static final String TMEM_PATH = "/sys/class/power_supply/battery/temp";
    /**
     *  get temperature
     *  
     * @return
     */
	public static float get() {
	    float temp = 0;
	    String tempStr = FileUtils.getCommandNodeValue(TMEM_PATH);
        
        if(tempStr !=null){
            temp = (float) (Integer.valueOf(tempStr.trim()) *1.0/10);
        }
		return temp;
	}
	
}
