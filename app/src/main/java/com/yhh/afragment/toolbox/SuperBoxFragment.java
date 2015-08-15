/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.afragment.toolbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.robot.RootTools;
import com.yhh.toolbox.BrightnessInfo;
import com.yhh.utils.ConstUtils;

public class SuperBoxFragment extends Fragment {
	private static String TAG= ConstUtils.DEBUG_TAG+"SuperBox";
	
	private Context mContext;
	private TextView mWifiStatusTv;
	private TextView mSimStatusTv;
	
	private TextView mBrightUpTv;
	private TextView mBrightDownTv;
	private EditText mBrightValueTv;
	private CheckBox mBrightIsAutoCb;
    
	private Button mRefreshBtn;
    
	private int mSimAsu;
	
	WifiManager  mWifiManager;
	TelephonyManager  Tel;
	MyPhoneStateListener  MyListener;
	BrightnessInfo  mBrightnessInfo;
	
	//FOR kernel
	private CheckBox mPanicCb;
	private TextView mPanicTv;
	private Button mKernelCrashBtn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.tool_box);
		mContext = this.getActivity();
		
		mBrightnessInfo = new BrightnessInfo(mContext);
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

		MyListener   = new MyPhoneStateListener();
        Tel= (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tool_box, container, false);  
        mWifiStatusTv = (TextView) v.findViewById(R.id.wifi_status);
        mSimStatusTv = (TextView) v.findViewById(R.id.sim_status);
        
        mBrightDownTv = (TextView) v.findViewById(R.id.bright_down);
        mBrightUpTv = (TextView) v.findViewById(R.id.bright_up);
        
        mBrightIsAutoCb = (CheckBox) v.findViewById(R.id.bright_is_auto);
        mBrightValueTv = (EditText) v.findViewById(R.id.bright_setting_value);
        
        mRefreshBtn = (Button) v.findViewById(R.id.refresh);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                updateData();
                updateUI();
            }
        });
        mBrightIsAutoCb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mBrightValueTv.setEnabled(!isChecked);
                updateData();
            }
            
        });
        
        //for kernel
        mPanicCb = (CheckBox) v.findViewById(R.id.kernel_cb);
        mPanicTv = (TextView) v.findViewById(R.id.kernel_tv);
        mKernelCrashBtn = (Button) v.findViewById(R.id.kernel_crash);
        
        mPanicCb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if(arg1){
                    mPanicTv.setText("panic is reboot and ssr is reboot");
                    RootTools.getInstance().setSystemProperty("sys.dloadmode.ssr", "SYSTEM");
                }else{
                    mPanicTv.setText("panic is download and ssr is related");
                    RootTools.getInstance().setSystemProperty("sys.dloadmode.ssr", "RELATED");
                }
            }
            
        });
        
        mKernelCrashBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                openCrashDialog();
            }
        });
        return v;
    }

	
	@Override
    public void onResume() {
	    Log.i(TAG,"onResume");
	    autoRefresh();
	    Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		super.onResume();
	}
	
	@Override
    public void onPause() {
	    stopRefresh();
	    Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
		super.onPause();
	}
	
	private String asu2dbm(int asu){
	    return -113 + 2*asu +"dBm";
	}
	
	private class MyPhoneStateListener extends PhoneStateListener
    {
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         mSimAsu = signalStrength.getGsmSignalStrength();
         if(mSimAsu == 99){
             mSimStatusTv.setText(" ");
         }else{
             mSimStatusTv.setText(asu2dbm(mSimAsu)+" / " + mSimAsu+"asu");
         }
      }
    };
    
    public void autoRefresh(){
        mHandler.sendEmptyMessage(1);
    }
    
    public void stopRefresh(){
        mHandler.removeMessages(1);
    }
    
    private void updateData(){
        
        if(mBrightIsAutoCb.isChecked()){
            mBrightnessInfo.setAutoBrightness(true);
        }else{
            mBrightnessInfo.setAutoBrightness(false);
            
          int b = -1;
          try{
              b = Integer.valueOf(mBrightValueTv.getText().toString());
          }catch(Exception e){
              Log.w(TAG,"bright value wrong");
          }
          if(b>=0 && b<=255){
              mBrightnessInfo.setBrightness(b);
          }
        }
    }
    
	private void updateUI(){
		if(mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
			mWifiStatusTv.setText("Not Connected");
		}else{
			int rssi = mWifiManager.getConnectionInfo().getRssi();
			mWifiStatusTv.setText(rssi+"dBm");
		}
		
		mBrightnessInfo.updateInfo();
		mBrightDownTv.setText(mBrightnessInfo.getAdbBrightness());
		mBrightUpTv.setText(mBrightnessInfo.getBrightness()+"");
		mBrightIsAutoCb.setChecked(mBrightnessInfo.isAutoBacklight());
		
		if(mBrightIsAutoCb.isChecked()){
		    mBrightValueTv.setEnabled(false);
		}else{
		    mBrightValueTv.setEnabled(true);
		}
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0x1){
			    updateUI();
			    mHandler.sendEmptyMessageDelayed(1, 1000);
			}
		}
	};
	
	
	
	/*
	 *  set media volume
	 */
	public boolean setVolume(int volume){
		if(volume<=10 && volume >=0){
			AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*volume/10), 0);
			Log.d(TAG,"set media volume: "+volume);
			return true;
		}else{
			Log.e(TAG,"set media volume error");
			return false;
		}
	}
	
	/*
	 *  set Ring volume
	 */
	public boolean setRingVolume(int volume){
		if(volume<=10 && volume >=0){
			AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_RING, (int)(am.getStreamMaxVolume(AudioManager.STREAM_RING)*volume/10), 0);
			Log.d(TAG,"set Ring volume: "+volume);
			return true;
		}else{
			Log.e(TAG,"set Ring volume error");
			return false;
		}
	}
	
    public  void openCrashDialog() {
        AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setTitle("Warning")
                .setMessage("Are you sure to simulate a kernel crash?")
                .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        RootTools.getInstance().setSystemProperty("sys.lenovo.simulate.ke", "TRUE");
                    }
                })
                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

}
