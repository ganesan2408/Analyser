/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.ConstUtils;

public class StatusBatteryFragment extends Fragment
{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "Battery";
    private boolean DEBUG = false;
    
    private TextView mBatteryTv;
    private Context mContext;
    
    private static final String[] BATTERY_STATUS = {"Unknown", "Charging",
                "DisCharging", "Not Charging", "Full"};
    
    private static final String[]  BATTERY_HEALTH = {"Unknown", "Good","OverHeat",
                "Dead", "Over Voltage", "Unspecified Failure", "Cold"};
    
    private static final String[] BATTERY_PLUGGED = {"AC charger","Usb connected","None"};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.status_battery_fragment, null) ;
		mBatteryTv = (TextView) v.findViewById(R.id.status_battery_tv);
		return v ;
	}
	
	@Override
    public void onStart() {
	    if(DEBUG){
            Log.i(TAG,"battery onRegister BatteryReceiver");
        }
	    registerBatteryReceiver();
        super.onStart();
    }
	
	@Override
	public void onStop() {
	    if(DEBUG){
            Log.i(TAG,"battery onUnregister BatteryReceiver");
        }
	    mContext.unregisterReceiver(mIntentReceiver);
	    super.onStop();
	}

	
	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Log.i(TAG,"battery recevie");
                mBatteryTv.setText((String)msg.obj);
            }
        }
    };
	
	private void registerBatteryReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mIntentReceiver, mIntentFilter);
    }
	
	
	/**
     * 接收电池状态改变的广播
     */
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG){
                Log.i(TAG,"on receive battery");
            }
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                StringBuilder sb = new StringBuilder();
                int status = intent.getIntExtra("status", 0);
                int health = intent.getIntExtra("health", 0);
                boolean present = intent.getBooleanExtra("present", false);
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                String technology = intent.getStringExtra("technology");
                int plugged = intent.getIntExtra("plugged", 0);

                sb.append("battery level:   " + ((level * 100) / scale) + "%"
                        + "\n");
                sb.append("temperature:   " + temperature / 10 + "."
                        + temperature % 10 + "℃ " + "\n");
                sb.append("voltage:   " + voltage / 1000.0 + "V" + "\n");
                sb.append("status:   " + BATTERY_STATUS[status-1] + "\n");
                sb.append("health:   " + BATTERY_HEALTH[health-1] + "\n");
                if(present){
                    sb.append("present:   " + "OK" + "\n");
                }else{
                    sb.append("present:   " + "Failure" + "\n");
                }
                sb.append("technology:   " + technology + "\n");

                if (plugged == 1) {
                    sb.append("plugged:   " + BATTERY_PLUGGED[0] + "\n");
                } else if (plugged == 2) {
                    sb.append("plugged:   " + BATTERY_PLUGGED[1] + "\n");
                } else {
                    sb.append("plugged:   " + BATTERY_PLUGGED[2] + "\n");
                }
                mHandler.sendMessage(mHandler.obtainMessage(1,sb.toString()));
            }
        }
    };
}