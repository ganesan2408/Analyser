/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info;

import com.yhh.info.app.PhoneInfo;
import com.yhh.utils.CommandUtils;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;

class PowerInfo {
    static String mCurrentCmd;
    static {
        if(PhoneInfo.getPhoneType().contains(PhoneInfo.ZOOM) ||
                PhoneInfo.getPhoneType().contains(PhoneInfo.S7)){
            mCurrentCmd = CommandUtils.CMD_CURRENT_NOW;
        }else{
            mCurrentCmd = CommandUtils.CMD_CURRENT_NEW;
        }
    }
    
    public float getcurrent() {
        float current = 0;
        if(PhoneInfo.getBrand().contains(ConstUtils.BRAND_HUAWEI)){
            String cmd = CommandUtils.CMD_CURRENT_NOW_HUAWEI;
            String str = FileUtils.getCommandNodeValue(cmd);
            if(str !=null && !"".equals(str)){
                current = -1*Integer.valueOf(str.trim());
            }
        }else{
            String str = FileUtils.getCommandNodeValue(mCurrentCmd);
            if(str !=null && !"".equals(str)){
                current = (float) (Integer.valueOf(str.trim())/1000.0);
            }
        }
        return current;
    }
}
