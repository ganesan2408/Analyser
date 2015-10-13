package com.yhh.analyser.config;

import com.yhh.analyser.utils.MyFileUtils;

import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/9/24.
 */
public class OneKeyConfig {
    private static ArrayList<String> whiteList = null;


    public static ArrayList<String> getWhiteList(){
        if(null == whiteList){
            synchronized (OneKeyConfig.class){
                if(null == whiteList){
                    whiteList = new ArrayList<>();
//                    whiteList.add("分析中心");
//                    whiteList.add("乐桌面");
//                    whiteList.add("安全中心");
//                    whiteList.add("联想通信录");
//                    whiteList.add("电话");
//                    whiteList.add("通知中心");
//                    whiteList.add("VIBEUI服务");
//                    whiteList.add("VIBE锁屏");
//                    whiteList.add("NFC服务");
//                    whiteList.add("WLAN");
//                    whiteList.add("FrameworksApp");

                    whiteList.add("com.lenovo.calendar");  //日历
                    whiteList.add("com.lenovo.frameworks");  //FrameworksApp
                    whiteList.add("com.android.nfc");  //NFC服务
                    whiteList.add("com.lenovo.updateassist");  //VIBEUI服务
                    whiteList.add("com.lenovo.security");  //安全中心
                    whiteList.add("com.lenovo.coverapp.simpletime2");  //VIBE锁屏
                    whiteList.add("com.android.incallui");  //InCallUI
                    whiteList.add(" com.android.wifi");  //WLAN
                    whiteList.add("com.yhh.analyser");  //分析中心
                    whiteList.add("com.android.server.telecom");  //电话
                    whiteList.add("com.android.inputmethod.latin");  //Android 键盘 (AOSP)
                    whiteList.add("com.lenovo.wifiApc");  //WLAN信号增强
                    whiteList.add("com.lenovo.launcher"); //乐桌面
                    whiteList.add("com.android.phone"); //电话
                    whiteList.add("com.android.systemui"); //通知中心

                    ArrayList fileList = (ArrayList) MyFileUtils.readFile2List(AppConfig.ONE_KEY_WHITE_LIST);
                    if(fileList !=null) {
                        whiteList.addAll(fileList);
                    }
                }
            }
        }
        return whiteList;
    }
}
