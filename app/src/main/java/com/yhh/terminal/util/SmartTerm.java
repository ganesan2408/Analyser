package com.yhh.terminal.util;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.yhh.analyser.utils.ConstUtils;

public class SmartTerm{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "SmartTerm";
    private Context mContext;
    private String input;
    
    public SmartTerm(Context context){
        this.mContext = context;
    }
    
    public void smartPrompt(char c){
        
    }


    /**
     * 获取文件的前缀
     * 
     * @param prefix
     * @return
     */
    private String getPrefixPath(String prefix) {
        String prefixPath = "";
        String pre[] = prefix.split(" ");
        Log.i(TAG, "pre array length:" + pre.length);
        if (pre != null && pre.length > 1) {
            prefixPath = pre[pre.length - 1].trim();
        }
        return prefixPath;
    }

    /**
     * 获取智能显示的列表
     * 
     * @param prefix
     * @return
     */
    private String[] getAutoAdapterArray(String prefix) {
        String[] adapterArray = null;
        String prefixPath = getPrefixPath(prefix);
        Log.i(TAG, "prefixPath=" + prefixPath);
        try {
            File file = new File(prefixPath);
            File[] subFiles = null;
            if (file.exists() && file.isDirectory()) {
                subFiles = file.listFiles();
                if (subFiles != null) {
                    adapterArray = new String[subFiles.length];
                    int i = 0;
                    for (File f : subFiles) {
                        if (f.isDirectory()) {
                            adapterArray[i++] = prefix + f.getName();
                        } else {
                            adapterArray[i++] = prefix + f.getName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "getAutoAdapterArray error", e);
        }
        return adapterArray;
    }

}
