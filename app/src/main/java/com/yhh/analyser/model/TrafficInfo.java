/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.model;

import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.view.fragment.MainMonitorFragment;
import com.yhh.androidutils.PreferencesUtils;

/**
 * information of network traffic
 * 
 */
public class TrafficInfo {
    private static final String TAG = LogUtils.DEBUG_TAG + "TrafficInfo";
    private boolean DEBUG = false;
    private float revKbs;
    private float sendKbs;
    private int timeSpan;
    
	public void init(Context context) {
		timeSpan= PreferencesUtils.getInstance(context.getApplicationContext()).get(
				MainMonitorFragment.KEY_INTERVAL, MainMonitorFragment.DEFAULT_FREQ);

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
