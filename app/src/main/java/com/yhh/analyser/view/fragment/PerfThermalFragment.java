/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.view.BaseFragment;
import com.yhh.analyser.widget.SwitchButton;
import com.yhh.androidutils.DebugLog;
import com.yhh.androidutils.ShellUtils;
import com.yhh.androidutils.StringUtils;

public class PerfThermalFragment extends BaseFragment {
    private static final String TAG = LogUtils.DEBUG_TAG +"PerfThermal";

    private SwitchButton mThermalControlBtn;
    private TextView mThermalStatus;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_thermal, null);
        mThermalControlBtn = (SwitchButton) view.findViewById(R.id.thermal_control_btn);
        mThermalStatus = (TextView) view.findViewById(R.id.tv_thermal_status);

        initListener();
        updateUI();

        return view;
    }
    

    
    private void initListener(){
        mThermalControlBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                RootUtils.getInstance().startThermalEngine(!isChecked);
                updateUI();
            }
        });
    }
    
    private void updateUI(){
        boolean isOn = isThermalOn();

        if(isOn){
            mThermalControlBtn.setChecked(false);
            DebugLog.i("mThermalControlBtn.setChecked(false);");
            mThermalStatus.setText("Thermal温控策略已开启");
        }else {
            mThermalControlBtn.setChecked(true);
            DebugLog.i("mThermalControlBtn.setChecked(true);");
            mThermalStatus.setText("Thermal温控策略已关闭");
        }
    }

    private boolean isThermalOn(){
        String cmd = "ps | grep thermal-engine";
        ShellUtils.CommandResult result = ShellUtils.execCommand(cmd);
        if(!StringUtils.isBlank(result.successMsg)){
            return true;
        }
        return false;
    }
}
