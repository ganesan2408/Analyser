package com.yhh.analyser.ui;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.utils.ShellUtils;

public class AdbWirelessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adb_wireless);
        initView();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private void initView(){
        ToggleButton adbToggle = (ToggleButton)findViewById(R.id.cb_adb_controller);
        final TextView statusTxt = (TextView) findViewById(R.id.txt_adb_status);
        adbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    startAdbWireless();
                    statusTxt.setText("CMD:  adb connect " + getWifiIp());
                }else {
                    stopAdbWireless();
                    statusTxt.setText("已关闭");
                }
            }
        });
    }

    private void startAdbWireless(){
        RootUtils.getInstance().setSystemProperty("service.adb.tcp.port","5555");
        ShellUtils.execCommand(new String[]{"stop adbd", "start adbd"}, false);
    }

    private void stopAdbWireless(){
        RootUtils.getInstance().setSystemProperty("service.adb.tcp.port", "0");
    }

    private String getWifiIp(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.getConnectionInfo() != null){
            int ip = wifiManager.getConnectionInfo().getIpAddress();
            return (ip & 0xFF ) + "."
                    +  ((ip >> 8 ) & 0xFF) + "."
                    +  ((ip >> 16 ) & 0xFF) + "."
                    +  ((ip >> 24 ) & 0xFF);
        }else{
            return "";
        }

    }

}
