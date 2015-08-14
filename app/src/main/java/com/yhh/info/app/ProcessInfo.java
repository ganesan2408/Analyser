/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.yhh.utils.ConstUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * get information of processes
 * 
 */
public class ProcessInfo {

	private static final String TAG =  ConstUtils.DEBUG_TAG+ "ProcessInfo";
	/**
	 * 获取所有能够启动的APP集合
	 * 
	 * @param context
	 * @return
	 */
	public List<AppInfo> getLaunchApps(Context context) {
		List<AppInfo> progressList = new ArrayList<AppInfo>();
		PackageInfo packageInfo = new PackageInfo();
		//获取所有已安装的apk
		PackageManager pm = context.getApplicationContext().getPackageManager();
		List<ApplicationInfo> appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		//获取能够启动的apk
		List<String> appNames = getLauncherAppName(pm); 

        String tmpName;
		for (ApplicationInfo ainfo : appList) {
            tmpName = ainfo.loadLabel(pm).toString();
		    if(appNames.contains(tmpName) && !tmpName.equals("分析中心")){
		        AppInfo appInfo = new AppInfo();
	            appInfo.setName(tmpName);
	            appInfo.setLogo(ainfo.loadIcon(pm));
	            appInfo.setPackageName(ainfo.packageName);
                getRunningApp(context, appInfo);

	            progressList.add(appInfo);
		    }
		}
		return progressList;
	}
	
	/**
	 * 获取指定包名的apk的详细信息
	 * 
	 * @param context
	 * @param appInfo
	 * @return
	 */
	public void getRunningApp(Context context, AppInfo appInfo){
	  ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	  List<RunningAppProcessInfo> runingApps = am.getRunningAppProcesses();

      for (RunningAppProcessInfo running : runingApps) {
          if ((running.processName != null) && running.processName.startsWith(appInfo.getPackageName())) {
              appInfo.setPid(running.pid);
              appInfo.setUid(running.uid);
              break;
          }
      }
	}

    /**
     * 获取指定包名的apk的详细信息
     *
     * @param context
     * @param appInfo
     * @return
     */
    public void getRunningPackage(Context context, AppInfo appInfo){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runingApps = am.getRunningAppProcesses();

        for (RunningAppProcessInfo running : runingApps) {
            if ((running.processName != null) && running.processName.equals(appInfo.getPackageName())) {
                appInfo.setPid(running.pid);
                appInfo.setUid(running.uid);
                break;
            }
        }
    }
	
	public AppInfo getPackageInfo(Context context,String pkgName){
	    AppInfo appInfo = new AppInfo();
	    PackageManager pm = context.getPackageManager();    
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(pkgName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if(info != null){    
            ApplicationInfo pInfo = info.applicationInfo;    
            appInfo.setVersionName(info.versionName);
            appInfo.setLogo(pm.getApplicationIcon(pInfo));
        }    
        return appInfo;
	}
	
	
	
	
	private List<String> getLauncherAppName(PackageManager pm){
	    List<String> appNames = new ArrayList<String>();
        
        Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, PackageManager.GET_ACTIVITIES);
        for(ResolveInfo r:apps){
            appNames.add((String) r.activityInfo.applicationInfo.loadLabel(pm));
        }
        return appNames;
	}
	
    public String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return null;
    }
}
