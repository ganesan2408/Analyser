/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info.app;

import java.util.ArrayList;
import java.util.List;

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
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;

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
		
		for (ApplicationInfo ainfo : appList) {
		    if(appNames.contains(ainfo.loadLabel(pm).toString())){
		        AppInfo appInfo = new AppInfo();
	            appInfo.setName(ainfo.loadLabel(pm).toString());
	            appInfo.setLogo(ainfo.loadIcon(pm));
	            appInfo.setPackageName(ainfo.packageName);
	            progressList.add(appInfo);
		    }
		}
		return progressList;
	}
	
	/**
	 * 获取指定包名的apk的详细信息
	 * 
	 * @param context
	 * @param appName
	 * @return
	 */
	public AppInfo getRunningApp(Context context, String packageName){
	  ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	  List<RunningAppProcessInfo> runingApps = am.getRunningAppProcesses();
	  
	  AppInfo appInfo = new AppInfo();
      for (RunningAppProcessInfo running : runingApps) {
          if ((running.processName != null) && running.processName.equals(packageName)) {
              appInfo.setPackageName(packageName);
              appInfo.setPid(running.pid);
              appInfo.setUid(running.uid);
              break;
          }
      }
      return appInfo;
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
