/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;


import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.StringUtils;

public class TempInfo {
    private static final String TMEM_PATH = "/sys/class/power_supply/battery/temp";
    /**
     *  get temperature
     *  
     * @return
     */
	public static float get() {
	    float temp = 0;
	    String tempStr = FileUtils.readFile(TMEM_PATH);
        
        if(!StringUtils.isBlank(tempStr)){
            temp = (float) (Integer.valueOf(tempStr.trim()) *1.0/10);
        }
		return temp;
	}

    public static int getTemp() {
        return Integer.parseInt(FileUtils.readFile(TMEM_PATH));
    }

}
