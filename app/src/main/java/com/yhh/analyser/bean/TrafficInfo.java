/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yhh.analyser.ui.settings.SettingsActivity;
import com.yhh.analyser.utils.ConstUtils;

/**
 * information of network traffic
 * 
 */
public class TrafficInfo {
    private static final String TAG = ConstUtils.DEBUG_TAG + "TrafficInfo";
    private boolean DEBUG = false;
    private float revKbs;
    private float sendKbs;
    private int timeSpan;
    
	public void init(Context context) {
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	    timeSpan = pref.getInt(SettingsActivity.KEY_INTERVAL, 1);
	    revKbs = (float) (TrafficStats.getTotalRxBytes()/1024.0);
	    sendKbs = (float) (TrafficStats.getTotalTxBytes()/1024.0);  
	    if(DEBUG){
	        Log.i(TAG, "timeSpan="+timeSpan+  ",revKbs="+revKbs+  ", sendKbs"+sendKbs);
	    }
	}
	
	public float getRevSpeed(){
	    float currentRevKbs = (float) (TrafficStats.getTotalRxBytes()/1024.0);
	    float speed = (currentRevKbs -revKbs)/timeSpan;
        if(DEBUG){
            Log.i(TAG, "Revspeed="+speed);
        }
        revKbs = currentRevKbs;
        return speed;
	}
	
	public float getSendSpeed(){
	    float currentSendKbs = (float) (TrafficStats.getTotalTxBytes()/1024.0);
	    float speed = (currentSendKbs -sendKbs)/timeSpan;
	    if(DEBUG){
            Log.i(TAG, "Sendspeed="+speed);
        }
	    sendKbs = currentSendKbs;
	    return speed;
	}
}
