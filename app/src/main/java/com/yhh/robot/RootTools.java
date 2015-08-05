/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.robot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class RootTools {
    private static final String KEY_AUTOMATIC = "sys.testmode.analyser";
    private static final String KEY_THERMAL_ENGINE = "sys.testmode.thermal";
//    private static final String KEY_VIEW_NODE = "sys.testmode.viewer";
    private static final String PATH_SHELL = "/data/system_analyser.sh";
    
    private static RootTools mMyRoot;
    private RootTools(){};
    
    public static RootTools getInstance(){
        if(null == mMyRoot){
            mMyRoot = new RootTools();
        }
        return mMyRoot;
    }
    
    private boolean generateShell(String cmd){
        if(cmd == null){
            return false;
        }
        
        byte[] bCommand = cmd.getBytes();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(PATH_SHELL);
            fos.write(bCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
                if(fos !=null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    public boolean setSystemProperty(String key, String value){
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("set", String.class, String.class);
            method.invoke(null, key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private String getSystemProperty(String key){
        String value = "";
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            value = (String) method.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return value;
    }
    
//    public void setCommand(){
//        StringBuilder sb = new StringBuilder();
//        sb.append("mkdir /data/local/tmp/dalvik-cache \n");
//        mCommmand = sb.toString();
//    }
    
    
//    public boolean rootExec(String cmd){
//        if(generateShell(cmd)){
//            if(setSystemProperty(KEY_VIEW_NODE, "1")){
//                return true;
//            }
//        }
//        return false;
//    }
    
    public boolean rootPrepareRobot(){
        if(setSystemProperty(KEY_AUTOMATIC, "1")){
            return true;
        }
        return false;
    }
    
    public boolean startThermalEngine(boolean isOn){
        boolean rtn = false;
        if(isOn){
            rtn = setSystemProperty(KEY_THERMAL_ENGINE, "1");
        }else{
            rtn = setSystemProperty(KEY_THERMAL_ENGINE, "0");
        }
        return rtn;
    }
}
