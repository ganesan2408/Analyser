/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.model;

import com.yhh.analyser.model.app.PhoneInfo;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.StringUtils;

class ScreenInfo {
	public static final String CMD_BRIGHTNESS_NOW = "/sys/class/leds/lcd-backlight/brightness";
	public static final String CMD_BRIGHTNESS_NOW_HUAWEI = "/sys/class/leds/lcd_backlight0/brightness";

	public float getBrightness() {
	    float brightness = 0;
	    String cmd;
	    
	    if(PhoneInfo.getBrand().contains(LogUtils.BRAND_HUAWEI)){
	        cmd = CMD_BRIGHTNESS_NOW_HUAWEI;
	    }else{
	        cmd = CMD_BRIGHTNESS_NOW;
	    }

		String str = FileUtils.readFile(cmd);
		if (!StringUtils.isBlank(str)) {
            brightness =  Integer.valueOf(str.trim());
        }
		return brightness;
	}
	
}
