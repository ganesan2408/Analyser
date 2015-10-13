package com.yhh.analyser.core.monitor;

import android.content.Context;

import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.utils.ShellUtils;

/**
 * Created by yuanhh1 on 2015/8/19.
 */
public class MonitorTop extends Monitor {

    public MonitorTop(Context context){
        super(context);

    }

    @Override
    public Integer[] getItems() {
        return new Integer[]{
                MonitorFactory.TYPE_TOP
        };
    }

    @Override
    public String getFileType() {
        return "_Top";
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public String monitor() {

        return execTop(10);
    }


    String execTop(int topNum){
        String topCmd = "top -m "+topNum+" -n 1 -d 1";
        String rawRtn = ShellUtils.execCommand(topCmd, false).successMsg;
        return parseTopResult(rawRtn.trim());
    }

    private String parseTopResult(String result){
        StringBuffer topRtn =new StringBuffer();
        String[] lines = result.split("\n");
        int len = lines.length;
        String[] words;
        int wordsLen;
        for(int i=4;i<len;i++){
            words = lines[i].trim().split("\\s+");
            wordsLen = words.length;
            topRtn.append(words[2]).append("  ");
            topRtn.append(words[wordsLen -1]).append("\n");
        }
        return topRtn.toString();
    }


}
