/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.provider;

import android.content.Context;

import com.yhh.analyser.config.MonitorConst;
import com.yhh.analyser.core.Monitor;
import com.yhh.analyser.ui.settings.SettingShellActivity;
import com.yhh.analyser.utils.DebugLog;
import com.yhh.analyser.utils.ShellUtils;
import com.yhh.analyser.utils.StringUtils;

public class MonitorShell  extends Monitor {
    String[] commands;

    public MonitorShell(Context context){
        super(context);
        commands = parseCommand(SettingShellActivity.sCommand);
        for(String cmd:commands){
            DebugLog.d("----"+cmd);
        }
    }
    
    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorConst.SHELL
        };
    }

    @Override
    public String monitor() {
        if(commands ==null){
            return "";
        }
        return ShellUtils.execCommand(commands, false).successMsg;
    }

    private String[] parseCommand(String command){
        if(StringUtils.isBlank(command)){
            return null;
        }
        return command.trim().split("&");
    }
}
