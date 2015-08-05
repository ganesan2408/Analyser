/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.robot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.utils.FileUtils;
import com.yhh.utils.ShellUtils;
import com.yhh.utils.ShellUtils.CommandResult;

public class Automatic{
	public static final String TAG =  ConstUtils.DEBUG_TAG+ "Automatic";
	private boolean DEBUG = true;
	
	private static final String ROBOT_JAR_NAME = "/myrobot.jar";
	private static final String ROBOT_CONFIG_NAME = "/robot_config.xml";
	private static final String CASE_PACKAGE_NAME = "com.automator.autocase.";
	private static final String USER_CONFIG = "/sdcard/robot_config.xml";
	
	private Context mContext;
	private Thread autoThread;
	
	private String ROBOT_ROOT_PATH;
    private String mConfigPath;
    
    private OnAutomaticListener mOnAutomaticListener;
	
	public Automatic(Context context){
	    mContext = context;
	    ROBOT_ROOT_PATH = mContext.getApplicationContext().getFilesDir().getAbsolutePath()
	            +"/myrobot";
	    
	    mConfigPath = "/data/data/"+ConstUtils.MY_PACKAGE_NAME 
                + "/shared_prefs" + ROBOT_CONFIG_NAME;
	}
	
	/**
	 * 拷贝自动化jar至本机
	 * 
	 * @return
	 */
	public boolean copyRobotResouce() {
	    DialogUtils.showLoading(mContext, "初始化资源中...");
	    if(!FileUtils.createFolder(ROBOT_ROOT_PATH)){
	        Log.e(TAG,"create ROBOT_ROOT_PATH failure");
	        return false;
	    }
	    
        if(RootTools.getInstance().rootPrepareRobot()){
            Log.i(TAG,"root exec success.");
        }else{
            Log.e(TAG,"root exec failure.");
        }
	    
	    String jarPath = ROBOT_ROOT_PATH + ROBOT_JAR_NAME;
        if(!FileUtils.copyRaw2Local(mContext,R.raw.myrobot,jarPath)){
            return false;
        }
        if(!FileUtils.copyRaw2Local(mContext,R.raw.robot_config, mConfigPath)){
            return false;
        }
        if(mOnAutomaticListener !=null){
            mOnAutomaticListener.onPrepareComplete();
        }
	    return true;
	}
	
	private void autoRun(String caseName, String configPath){
	    if(DEBUG){
	        Log.d(TAG,"configPath="+configPath);
	    }
        StringBuilder sb = new StringBuilder();
        sb.append("uiautomator runtest ");
        sb.append(ROBOT_ROOT_PATH + ROBOT_JAR_NAME);
        sb.append(" -s -e config ");
        sb.append(configPath);
        sb.append(" --nohup -c ");
        sb.append(CASE_PACKAGE_NAME);
        sb.append(caseName);
        sb.append("Case#autoRun");
        
        final String cmd = sb.toString();
        if(DEBUG){
            Log.d(TAG,"autoRun CMD: "+cmd);
        }
        autoThread = new Thread(new Runnable(){

            @Override
            public void run() {
                ShellUtils.runShell(cmd);
                if(null !=mOnAutomaticListener){
                    mOnAutomaticListener.onRunComplete();
                }
            }
        });
        autoThread.start();
    }
	
	public String getAutoCmd(String caseName){
	    StringBuilder sb = new StringBuilder();
        sb.append("uiautomator runtest ");
        sb.append(ROBOT_ROOT_PATH + ROBOT_JAR_NAME);
        sb.append(" -s -e config ");
        sb.append(mConfigPath);
        sb.append(" --nohup -c ");
        sb.append(CASE_PACKAGE_NAME);
        sb.append(caseName);
        sb.append("Case#autoRun");
        
        return  sb.toString();
	}
	
	public void autoRun(String caseName){
	    Log.i(TAG,"ROBOT_ROOT_PATH:" + ROBOT_ROOT_PATH);
	    File configFile = new File(USER_CONFIG);
	    if(configFile.exists()){
	        autoRun(caseName, USER_CONFIG);
	    }else{
	        autoRun(caseName, mConfigPath);
	    }
	}
	
	public void autoStop(){
        Log.i(TAG,"autoStop");
        String pid = getUiautomatorPid();
        if(pid.equals("")){
            Log.i(TAG,"robot not exist.");
            return;
        }
        String cmd = "kill "+ pid;
        Log.i(TAG,"cmd:"+cmd);
        if(autoThread !=null){
            autoThread.interrupt();
        }
        ShellUtils.runShell(cmd);
    }
	
	private String apkName2CaseName(String apkName){
	    String caseName=null;
	    
	    return caseName;
	}
	
	private String getUiautomatorPid(){
	    String cmd = "ps | grep uiautomator";
	    CommandResult cr = ShellUtils.execCommand(cmd, false);
	    String[] pidStr = cr.successMsg.split("\\s+");
	    if(pidStr !=null && pidStr.length>2){
	        return pidStr[1].trim();
	    }else{
	        return "";
	    }
	}
	
	public void setOnAutomaticListener(OnAutomaticListener onAutomaticListener){
	    this.mOnAutomaticListener = onAutomaticListener;
	}
	
	public interface OnAutomaticListener{
	    public void onPrepareComplete();
	    public void onRunComplete();
	}
}
