/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.view.BaseFragment;
import com.yhh.analyser.widget.SwitchButton;
import com.yhh.androidutils.PreferencesUtils;

public class PerfIoFragment extends BaseFragment {
    private static final String TAG = LogUtils.DEBUG_TAG +"PerfIO";

    private SwitchButton mIoMonitorBtn;
    private SwitchButton mIoPrintBtn;
    private TextView mIoStatusTv;
    private SeekBar mIoStatusBar;

    private static final int DEFAULT_IO_PERCENT = 20;

    private static final String IO_PRINT_OPEN  = "echo -n 'file msm_performance.c +p' > /sys/kernel/debug/dynamic_debug/control";
    private static final String IO_PRINT_CLOSE = "echo -n 'file msm_performance.c -p' > /sys/kernel/debug/dynamic_debug/control";
    private static final String IO_MONITOR_OPEN  = "echo 1 > /sys/module/msm_performance/iowait/io_ctl_enable";
    private static final String IO_MONITOR_CLOSE = "echo 0 > /sys/module/msm_performance/iowait/io_ctl_enable";

    private static final String IO_PERCENT_NODE = "/sys/module/msm_performance/iowait/iowait_precent";




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_io, null);
        mIoMonitorBtn = (SwitchButton) view.findViewById(R.id.thermal_io_monitor_btn);
        mIoPrintBtn = (SwitchButton) view.findViewById(R.id.thermal_io_print_btn);

        mIoStatusBar = (SeekBar) view.findViewById(R.id.thermal_io_status_bar);
        mIoStatusTv = (TextView) view.findViewById(R.id.thermal_io_status_tv);

        initListener();
        updateUI();

        return view;
    }
    

    
    private void initListener(){
        mIoMonitorBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(!isChecked){
                    execCommand(IO_MONITOR_OPEN);
                }else{
                    execCommand(IO_MONITOR_CLOSE);
                }
                updateUI();
            }
        });

        mIoPrintBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(!isChecked){
                    execCommand(IO_PRINT_OPEN);
                }else{
                    execCommand(IO_PRINT_CLOSE);
                }
                updateUI();
            }
        });

        mIoStatusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                mIoStatusTv.setText(Integer.toString(arg1 + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                int interval = arg0.getProgress() + 1;
                PreferencesUtils.getInstance(mContext).put("io_percent", interval);
                execCommand( "echo  " + interval + " > "+ IO_PERCENT_NODE );
            }
        });
    }
    
    private void updateUI(){
        int percent = PreferencesUtils.getInstance(mContext).get("io_percent", DEFAULT_IO_PERCENT);

        mIoStatusTv.setText(percent + "");
        mIoStatusBar.setProgress(percent - 1);

    }

    private boolean execCommand(String command){
        Log.i(TAG, "execRootCommand: "+ command);
        return RootUtils.getInstance().execRootCommand(command);
    }
}
