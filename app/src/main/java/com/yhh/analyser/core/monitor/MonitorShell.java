/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.utils.DebugLog;
import com.yhh.analyser.utils.ShellUtils;
import com.yhh.analyser.utils.StringUtils;

public class MonitorShell  extends Monitor {
    String[] commands;

    public MonitorShell(Context context,String cmds){
        super(context);
        commands = parseCommand(cmds);
        for(String cmd:commands){
            DebugLog.d("----"+cmd);
        }
    }
    
    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorFactory.TYPE_SHELL
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
