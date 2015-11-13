package com.yhh.androidutils;

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

public class AppUtils {

    private AppUtils(){
        throw new RuntimeException("init");
    }


    /**
     *获取当前app的名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context){
        return getAppName(context, null);
    }

    /**
     * 获取指定包名的app的名称
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppName(Context context, String packageName){
        String appName = packageName;
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            appName =  (String) packageManager.getApplicationLabel(appInfo);
        } catch (Exception e) {

        }
        return appName;
    }

    /**
     * 获取当前app的版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context){
        return getAppVersionName(context, null);
    }

    /**
     * 获取指定包名的app的版本号
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppVersionName(Context context, String packageName) {
        String versionName;
        if(StringUtils.isNull(packageName)){
            packageName = context.getPackageName();
        }

        PackageManager pm = context.getPackageManager();
        try {
            versionName = pm.getPackageInfo(packageName, 0).versionName;

        } catch (NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }




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
    public static boolean isAppInstalled(Context context, String packageName) {
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
     * 获取当前应用程序 是否为系统应用程序
     *
     * @param context
     * @return
     */
    public static boolean isSystemApp(Context context){
        if(context == null){
            return false;
        }

        return isSystemApp(context, context.getPackageName());
    }

    /**
     * 判断app是否为系统应用程序
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isSystemApp(Context context, String packageName) {
        if (context == null || StringUtils.isNull(packageName)) {
            return false;
        }

        PackageManager packageManager = context.getPackageManager();

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 启动app
     *
     * @param context
     * @param pkgName
     * @param ActivityName
     */
    public static void startApp(Context context, String pkgName, String ActivityName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(pkgName, ActivityName);
        intent.setComponent(cn);
        context.startActivity(intent);
    }


    /**
     * 强制彻底停止 某个App
     *
     * @param context
     * @param pkgName
     * @throws Exception
     */
    public static void forceStopApp(Context context, String pkgName) throws Exception {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
        method.invoke(am, pkgName);
    }



    /**
     * 启动指定app
     *
     * @param packageName  package name
     * @param activityName activity name
     * @return
     */
    public static void startActivity(String packageName, String activityName) {
        String command = "am start -n " + packageName + "/" + activityName;
        ShellUtils.execCommand(command);
    }


    /**
     * 重启指定app
     * @param packageName
     * 		package name
     * @param activityName
     * 		activity name
     * @return
     */
    public static void restartActivity(String packageName, String activityName){
        String command = "am start -S -n "+ packageName+ "/" + activityName;
        ShellUtils.execCommand(command);
    }


    /**
     * 结束指定app
     *
     * @param packageName package name
     * @return
     */
    public static void stopActivity(String packageName) {
        String command = "am force-stop " + packageName;
        ShellUtils.execCommand(command);
    }

}
