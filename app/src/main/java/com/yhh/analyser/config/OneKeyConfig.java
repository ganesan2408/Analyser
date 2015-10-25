package com.yhh.analyser.config;


import com.yhh.androidutils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yuanhh1 on 2015/9/24.
 */
public class OneKeyConfig {
    private static ArrayList<String> whiteList = new ArrayList<>();


    public static ArrayList<String> getWhiteList() {

        whiteList.clear();

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

        ArrayList fileList = (ArrayList) FileUtils.readFile2List(AppConfig.ONE_KEY_WHITE_LIST);
        if (fileList != null) {
            whiteList.addAll(fileList);
        }
        return whiteList;
    }

    public static void addWhite(String pkgName) throws IOException {
        boolean b = FileUtils.createFile(AppConfig.ONE_KEY_WHITE_LIST);
        if (!b) {
            return;
        }
        ArrayList fileList = (ArrayList) FileUtils.readFile2List(AppConfig.ONE_KEY_WHITE_LIST);
        if (fileList != null && !fileList.contains(pkgName)) {
            FileUtils.writeFile(AppConfig.ONE_KEY_WHITE_LIST, FileUtils.NEW_LINE + pkgName, true);
        }
    }

    public static void deleteWhite(String pkgName) throws IOException {
        boolean b = FileUtils.createFile(AppConfig.ONE_KEY_WHITE_LIST);
        if (!b) {
            return;
        }
        ArrayList fileList = (ArrayList) FileUtils.readFile2List(AppConfig.ONE_KEY_WHITE_LIST);

        if (fileList != null && fileList.contains(pkgName)) {
            fileList.remove(pkgName);
            FileUtils.writeFile(AppConfig.ONE_KEY_WHITE_LIST, fileList, false);
        }
    }
}
