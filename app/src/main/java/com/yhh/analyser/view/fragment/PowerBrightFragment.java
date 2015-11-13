/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.model.BrightnessInfo;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.view.BaseFragment;
import com.yhh.analyser.widget.SwitchButton;

public class PowerBrightFragment extends BaseFragment {
    private static final String TAG = LogUtils.DEBUG_TAG + "PowerBright";

    private SwitchButton mCabcBtn;
    private SwitchButton mAutoBrightnessBtn;
    private TextView mBrightnessTv;
    private SeekBar mBrightnessBar;

    BrightnessInfo mBrightnessInfo;

    private static final String CABC_ON = "echo 1 > /sys/device/virtual/graphics/fb0/cabc_onoff";
    private static final String CABC_OFF = "echo 0 > /sys/device/virtual/graphics/fb0/cabc_onoff";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBrightnessInfo = new BrightnessInfo(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.power_lcd, null);
        mCabcBtn = (SwitchButton) view.findViewById(R.id.power_lcd_btn);
        mAutoBrightnessBtn = (SwitchButton) view.findViewById(R.id.power_lcd_auto_btn);

        mBrightnessBar = (SeekBar) view.findViewById(R.id.power_brightness_bar);
        mBrightnessTv = (TextView) view.findViewById(R.id.power_brightness_tv);

        initListener();
        updateUI();

        return view;
    }


    private void initListener() {
        mCabcBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked) {
                    execCommand(CABC_ON);
                } else {
                    execCommand(CABC_OFF);
                }
                updateUI();
            }
        });

        mAutoBrightnessBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mBrightnessInfo.setAutoBrightness(!isChecked);
                updateUI();
            }
        });


        mBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                mBrightnessInfo.setBrightness(arg1 + 1);  //写入值
                mBrightnessTv.setText(Integer.toString(arg1 + 1));  //改变UI
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                mBrightnessInfo.updateInfo();  //更新值
            }
        });
    }

    private void updateUI() {
        mBrightnessInfo.updateInfo();

        int brightness = mBrightnessInfo.getBrightness();
        mBrightnessTv.setText(brightness + "");
        mBrightnessBar.setProgress(brightness - 1);

        if(mBrightnessInfo.isAutoBacklight()){
            mBrightnessBar.setEnabled(false);
        }else{
            mBrightnessBar.setEnabled(true);
        }
    }

    private boolean execCommand(String command) {
        return RootUtils.getInstance().execRootCommand(command);
    }
}
