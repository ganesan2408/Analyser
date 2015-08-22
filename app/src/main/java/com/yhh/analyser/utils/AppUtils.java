package com.yhh.analyser.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class AppUtils {

    /**
     * 安装指定路径下的App
     *
     * @param context
     * @param apkPath
     */
    public static void installApp(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists())
            return;
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + apkFile.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 判断指定包名的app是否已经安装
     *
     * @param context
     * @param packageName
     * @return
     */
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

    /**
     * 启动app
     *
     * @param context
     * @param pkgName
     * @param ActivityName
     */
    public static void startApp(Context context, String pkgName, String ActivityName){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);           
        ComponentName cn = new ComponentName(pkgName, ActivityName);
        intent.setComponent(cn);
        context.startActivity(intent);
    }

    /**
     * 获取当前app的版本号
     *
     * @param context
     * @return
     */
    public static  String getAppVersionName(Context context){
        String versionName="";
        try {
            versionName =context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if(StringUtils.isNull(versionName)){
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 强制停止某个App
     *
     * @param context
     * @param pkgName
     * @throws Exception
     */
    public static void forceStopApp(Context context, String pkgName) throws Exception {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage",String.class);
        method.invoke(am, pkgName);
    }

    public static String getLableByPkgName(Context context, String pkgName){
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ApplicationInfo info:appList) {
            if(info.packageName.equalsIgnoreCase(pkgName)){
                return info.loadLabel(pm).toString();
            }
        }
        return  pkgName;
    }

}
