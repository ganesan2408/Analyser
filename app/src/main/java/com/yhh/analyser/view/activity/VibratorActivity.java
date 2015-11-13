/**
 * @author yuanhh1
 * 
 * 
 * @email yuanhh1@lenovo.com
 * 
 * 
 */
package com.yhh.analyser.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.androidutils.ShellUtils;

public class VibratorActivity extends BaseActivity {
    private static final String TAG = "sa_Vibrator";
    
    private EditText mVmaxEt;
    private CheckBox mIsBrakeCb;
    private EditText mVibrateTimeEt;
    private EditText[] mWfEt = new EditText[8];
    private Button mUpdateBtn;
    
    private String mVmax;
    private String mIsBrake;
    private String mVibrateTime;
    private String[] mWf = new String[8];
    
    private static final String VMAX = "/sys/class/timed_output/vibrator/vmax";
    private static final String VIBRATE_TIME = "/sys/class/timed_output/vibrator/enable";
    private static final String VIBRATE_BRAKE = "/sys/class/timed_output/vibrator/brake_enable";
    private static final String VIBRATE_WF = "/sys/class/timed_output/vibrator/wf_s";
    private static final String VIBRATE_WF_UPDATE = "/sys/class/timed_output/vibrator/wf_update";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrator);
        initData();
        initView();
        updateUI();
    }
    
    
    private void initView(){
        mVmaxEt =  (EditText) findViewById(R.id.vmax_et);
        mIsBrakeCb = (CheckBox) findViewById(R.id.is_brake_cb);
        mVibrateTimeEt = (EditText) findViewById(R.id.vibrate_time_et);
        mWfEt[0] = (EditText) findViewById(R.id.wf0_et);
        mWfEt[1] =(EditText) findViewById(R.id.wf1_et);
        mWfEt[2] = (EditText) findViewById(R.id.wf2_et);
        mWfEt[3] = (EditText) findViewById(R.id.wf3_et);
        mWfEt[4] = (EditText) findViewById(R.id.wf4_et);
        mWfEt[5] =  (EditText) findViewById(R.id.wf5_et);
        mWfEt[6] = (EditText) findViewById(R.id.wf6_et);
        mWfEt[7] = (EditText) findViewById(R.id.wf7_et);
        mUpdateBtn = (Button) findViewById(R.id.update_btn);
        
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "更行中", Toast.LENGTH_SHORT).show();
                updateData();
                updateUI();
            }
        });
    }
    
    private void initData(){
        try{
            mVmax = getCmd(VMAX).trim();
            mVibrateTime =getCmd(VIBRATE_TIME).trim();
            mIsBrake = getCmd(VIBRATE_BRAKE).trim();
            
            mWf = new String[8];
            for(int i=0;i<8;i++){
                mWf[i] = getCmd(VIBRATE_WF+i).trim();
            }
        }catch(Exception e){
            Toast.makeText(this, "获取节点数据失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"initData error",e);
        }
    }
    
    private void updateUI(){
        mVmaxEt.setText(mVmax);
        mVibrateTimeEt.setText(mVibrateTime);
        
        if(mIsBrake.equals("1")){
            mIsBrakeCb.setChecked(true);
        }else{
            mIsBrakeCb.setChecked(false);
        }
        for(int i=0;i<8;i++){
            mWfEt[i].setText(mWf[i]);
        }
    }
    
    private void updateData(){
        mVmax = mVmaxEt.getText().toString();
        mVibrateTime = mVibrateTimeEt.getText().toString();
        
        if(mIsBrakeCb.isChecked()){
            mIsBrake = "1";
        }else{
            mIsBrake = "0";
        }
        setStringCmd(VMAX,mVmax);
        setStringCmd(VIBRATE_BRAKE,mIsBrake);

        for(int i=0;i<8;i++){
            mWf[i] = mWfEt[i].getText().toString();
            setStringCmd(VIBRATE_WF+i,mWf[i]);
        }
        setStringCmd(VIBRATE_WF_UPDATE,"1");
        setStringCmd(VIBRATE_TIME, mVibrateTime);
    }
    
    private String getCmd(String cmd){
        String result = ShellUtils.execCommand("cat " + cmd, false).successMsg;
        Log.i(TAG,"getCmd==>  "+cmd+"="+result);
        return result;
    }
    

    private void setStringCmd(String cmd,String value){
        String cmdStr = "echo \""+value+"\">"+cmd;
        ShellUtils.CommandResult cr = ShellUtils.execCommand(cmdStr, false);
        Log.i(TAG,"setCmd==>  "+cmdStr);
    }
}
