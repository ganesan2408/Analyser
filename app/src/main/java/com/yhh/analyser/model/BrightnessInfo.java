/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.model;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.yhh.analyser.utils.LogUtils;

public class BrightnessInfo {
    private static String TAG= LogUtils.DEBUG_TAG + "BrightnessInfo";
    
    private boolean mIsAutoBacklight;
    private int mBrightness;
//    private String mAdbBrightness;
    
    private Context mContext;
    
    public BrightnessInfo(Context context){
        mContext = context;
    }
    
    /**
     * toggle auto backlight
     * 
     * @param isOn
     */
    public void setAutoBrightness(boolean isOn){
        if(isOn){
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }else{
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
    }

    public void setBrightness(int brightness){
        if(brightness<=255 && brightness >=0){
            Settings.System.putInt(mContext.getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
            Log.d(TAG,"set brightness: "+brightness);
        }else{
            Log.e(TAG,"set brightness error");
        }
    }
    
    public void updateInfo(){
        ContentResolver mResolver = mContext.getContentResolver();
        
        try {
            mIsAutoBacklight = Settings.System.getInt(mResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) 
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            
            mBrightness = android.provider.Settings.System.getInt(mResolver,
                    Settings.System.SCREEN_BRIGHTNESS);
            
//            mAdbBrightness = InfoFactory.getInstance().getScreenBrightness();
            
        } catch (Exception e) {
            Log.e(TAG,"updateInfo error",e);
        }
    }
    
    
    public boolean isAutoBacklight(){
        return mIsAutoBacklight;
    }
    
    public int getBrightness(){
        return mBrightness;
    }
    
//    public String getAdbBrightness(){
//        return mAdbBrightness;
//    }
}
