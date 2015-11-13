package com.yhh.analyser.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yhh.analyser.service.RebootService;
import com.yhh.analyser.utils.RootUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(RootUtils.getInstance().getSystemProperty(RootUtils.REBOOT_KEY).equals("repeat")) {
            Log.v("TAG", "on reboot service running ..... ");
            Intent rebootIntent = new Intent(context, RebootService.class);
            context.startService(rebootIntent);
        }
    }

}