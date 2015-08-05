package com.yhh.utils;

import java.io.File;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class AppUtils {
    
    public static void installApp(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists())
            return;
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + apkFile.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    
    public static boolean isAppInstalled(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }
    
    public static void startApp(Context context, String pkgName, String ActivityName){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);           
        ComponentName cn = new ComponentName(pkgName, ActivityName);           
        intent.setComponent(cn);
        context.startActivity(intent);
    }
}
