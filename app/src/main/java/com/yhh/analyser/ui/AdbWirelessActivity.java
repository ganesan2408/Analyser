package com.yhh.analyser.ui;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.utils.ShellUtils;
import com.yhh.analyser.widget.SwitchButton;

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
        final SwitchButton adbSBtn = (SwitchButton)findViewById(R.id.cb_adb_controller);
//        final TextView statusTxt = (TextView) findViewById(R.id.txt_adb_status);
        adbSBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    startAdbWireless();
                    adbSBtn.setText("adb connect " + getWifiIp());
                }else {
                    stopAdbWireless();
                    adbSBtn.setText("已关闭");

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
        ShellUtils.execCommand(new String[]{"stop adbd", "start adbd"}, false);
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
