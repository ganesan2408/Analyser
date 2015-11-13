/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.model.app;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.yhh.analyser.utils.LogUtils;
import com.yhh.androidutils.FileUtils;

public class PhoneInfo {
    /** phone version info*/
    public static final String CMD_VERSION_CONF = "/system/etc/version.conf";

    public static String ZOOM = "Z90";
    public static String S7 = "X2";
    public static String X3 = "X3";

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

    public static  boolean isX3( ){
        return getPhoneType().contains(X3);
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
        
        String value= FileUtils.readFile(CMD_VERSION_CONF);
        String[] versionArr = value.split("\\n");
        for (String s : versionArr) {
            if (s.contains(LogUtils.STR_VERSION_SIGN)) {
                String[] arr = s.split(",");
                phoneVersion = arr[arr.length - 1];
            }
        }
        return phoneVersion;
      }
    
    @Override
    public String toString() {
        return  "\n型号:    " + getPhoneType() + LogUtils.LINE_END
                +"Android版本:  " + getSDKVersion()+ LogUtils.LINE_END
                + "内部版本号: " + getInternalVersion()+ LogUtils.LINE_END;
    }
}
