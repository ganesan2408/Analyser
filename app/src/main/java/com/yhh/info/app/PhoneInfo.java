/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info.app;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.yhh.utils.CommandUtils;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;

public class PhoneInfo {
    public static String ZOOM = "Z90";
    public static String S7 = "X2";
    
    /**
     * get the sdk version of phone.
     * 
     * @return sdk version
     */
    public String getSDKVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    
    public static String getBrand(){
        return android.os.Build.BRAND;
    }
    
    /**
     * get phone type.
     * 
     * @return phone type
     */
    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }
    
    public static String getIMEI(Context context){
        TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        String imei = TelephonyMgr.getDeviceId(); 
        return imei;
    }
    
    /**
     * get internal framework version
     * 
     * @return
     */
    public static String getInternalVersion() {
        String phoneVersion = null;
        
        String value= FileUtils.getCommandNodeValue(CommandUtils.CMD_VERSION_CONF);
        String[] versionArr = value.split("\\n");
        for (String s : versionArr) {
            if (s.contains(ConstUtils.STR_VERSION_SIGN)) {
                String[] arr = s.split(",");
                phoneVersion = arr[arr.length - 1];
            }
        }
        return phoneVersion;
      }
    
    @Override
    public String toString() {
        return  "\n型号:    " + getPhoneType() + ConstUtils.LINE_END
                +"Android版本:  " + getSDKVersion()+ ConstUtils.LINE_END 
                + "内部版本号: " + getInternalVersion()+ ConstUtils.LINE_END;
    }
}
