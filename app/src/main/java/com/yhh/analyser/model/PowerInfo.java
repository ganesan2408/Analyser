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

public class PowerInfo {
    public static final String CMD_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    public static final String CMD_CURRENT_NEW = "/sys/module/qpnp_fg/parameters/update_curr";

    public static final String CMD_CURRENT_NOW_HUAWEI = "/sys/class/power_supply/Battery/current_now";

    static String mCurrentCmd;
    static {
        if(PhoneInfo.getPhoneType().contains(PhoneInfo.ZOOM) ||
                PhoneInfo.getPhoneType().contains(PhoneInfo.S7)){
            mCurrentCmd = CMD_CURRENT_NOW;
        }else{
            mCurrentCmd = CMD_CURRENT_NEW;
        }
    }
    
    public float getcurrent() {
        float current = 0;
        if(PhoneInfo.getBrand().contains(LogUtils.BRAND_HUAWEI)){
            String cmd = CMD_CURRENT_NOW_HUAWEI;
            String str = FileUtils.readFile(cmd);
            if(!StringUtils.isBlank(str)){
                current = -1*Integer.valueOf(str.trim());
            }
        }else{
            String str = FileUtils.readFile(mCurrentCmd);
            if(!StringUtils.isBlank(str)){
                current = (float) (Integer.valueOf(str.trim())/1000.0);
            }
        }
        return current;
    }
}
