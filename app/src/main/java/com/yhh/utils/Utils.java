/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;

public class Utils {
	private static final String TAG= ConstUtils.DEBUG_TAG + "Utils";
	public static long firstTime;
	
	public static String getAppName(Context context, String packageName){
        String applicationName = packageName;
        PackageManager packageManager = context.getApplicationContext().getPackageManager(); 
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            applicationName =  (String) packageManager.getApplicationLabel(appInfo); 
            Log.i(TAG,"applicationName="+applicationName);
        } catch (NameNotFoundException e) {
        }
        return applicationName;
    }
	
	
	/**
	 * 开启拨号功能
	 * @param phoneNumber
	 * 		接听者的手机号码
	 * @return
	 * 
	 */
	public static boolean startCall(String phoneNumber){
		String command = "am start -a android.intent.action.CALL -d tel:"+phoneNumber;
		return runShell(command);
	}
	
	/**
	 * 启动短信
	 * @param phoneNumber
	 * 		收件人的手机号码
	 * @param msg
	 * 		短信内容
	 * @return
	 */
	public static boolean startMessage(String phoneNumber,String msg){
		String command = "am start -a android.intent.action.SENDTO -d sms:" +
				phoneNumber +" --es sms_body \""+msg+"\"";
		Log.i(TAG,command);
		return runShell(command);
	}
	
	/**
	 * 发送短信
	 */
	public static void sendMessage(){
		runShell("input keyevent 22");
		runShell("input keyevent 66");
	}
	
	/**
	 * 开启指定网站
	 * @param website
	 * 		网址
	 * @return
	 */
	public static boolean startWebsite(String website){
		String command = "am start -a android.intent.action.VIEW -d "+website;
		return runShell(command);
	}
	
	/**
	 * 启动指定app
	 * @param packageName
	 * 		package name
	 * @param activityName
	 * 		activity name
	 * @return
	 */
	public static boolean startActivity(String packageName, String activityName){		
		String command = "am start -n "+ packageName+ "/" + activityName;
		return runShell(command);
	}
	
	/**
	 * 重启指定app
	 * @param packageName
	 * 		package name
	 * @param activityName
	 * 		activity name
	 * @return
	 */
	public static boolean restartActivity(String packageName, String activityName){		
		String command = "am start -S -n "+ packageName+ "/" + activityName;
		return runShell(command);
	}
	
	/**
	 * 结束指定app
	 * @param packageName
	 * 		package name
	 * @return
	 */
	public static boolean stopActivity(String packageName){
		String command = "am force-stop "+ packageName;
		return runShell(command);
//		return runShell(command, false);
	}
	
	
	static boolean runShell(String command, boolean isWaitFor){
		if(!isWaitFor){
			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				Log.e(TAG,"runShell failure.",e);
				return false;
			}
		}else{
			return runShell(command);
		}
		return true;
	}
	
	
	/**
	 * 运行adb指令
	 * @param command
	 * 		指令
	 * @return
	 */
	public static boolean runShell(String command){
		boolean b = true;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command);
			b = showProcessErrorStream(process);
			process.waitFor(); //花费0.8s
		} catch (Exception e) {
			b = false;
			Log.e(TAG,"runShell failure.",e);
		}finally{
			if(process != null){
				process.destroy();
			}
		}
		return b;
	}
	

	
	
	/**
	 * 运行adb指令
	 * @param command
	 * 		指令
	 * @return
	 */
	static String runShellwithResult(String command){
		Process process = null;
		String str = null;
		try {
			process = Runtime.getRuntime().exec(command);
			str = readProcessInfoStream(process);
			process.waitFor();
		} catch (Exception e) {
			Log.e(TAG,"runShell failure.",e);
		}finally{
			if(process != null){
				process.destroy();
			}
		}
		return str;
	}
	
	private static boolean showProcessErrorStream(Process process){
		BufferedReader br = null;
		String line = null;
		boolean noError=true;
		try{
			br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while((line = br.readLine())!=null){
				if(line.contains("Error type 3")){
					noError = false;
				}
				Log.d(TAG,"error stream: "+line);
			}
		}catch(IOException e){
			Log.d(TAG, "showProcessErrorStream IOException");
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				Log.d(TAG, "close showProcessErrorStream IOException");
			}
		}
		return noError;
	}
	
	private static String readProcessInfoStream(Process process){
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line = null;
		try{
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((line = br.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				Log.d(TAG, "close showProcessInfoStream IOException");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 解析XML文件
	 * @param fileName
	 * 		xml文件名
	 * @param testCaseName
	 * @return
	 */
	public static HashMap<String,String> xmlParser(String fileName, String testCaseName){
		HashMap<String,String> hm = new LinkedHashMap<String,String>();
		try {
			File f = new File(fileName);
			DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			// testCase节点
			NodeList nl = doc.getElementsByTagName("testCase");
			
			String tcName = null;
			int len = nl.getLength();
			for(int i=0;i<len;i++){
				Element testCaseElement = (Element) nl.item(i);
				tcName = testCaseElement.getAttribute("name");
				// 判断 子节点的name 是否等于testCaseName
				if(testCaseName.equals(tcName.trim())){
					NodeList childNodes = testCaseElement.getChildNodes();
					
					String key=null;
					String value=null;
					int childLen = childNodes.getLength();
					// 遍历 testCaseName节点的子节点
					for(int j=0;j<childLen;j++){
						if(childNodes.item(j).getNodeType() == Node.ELEMENT_NODE){
						key = childNodes.item(j).getNodeName();
						try{
						value = childNodes.item(j).getFirstChild().getNodeValue();
						}catch(Exception e){
							Log.d(TAG,key+"'s value is null.");
						}
							if(key !=null){
								hm.put(key, value);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG,"xmlParser failure.",e);
		}
		return hm;
	}
	
	
	/**
	 * 解析XML文件
	 * @param fileName
	 * 		xml文件名
	 * @param testCaseName
	 * @return
	 */
	public static ArrayList<ArrayList<String>> xmlFullParser(String fileName, String testCaseName){
		ArrayList<ArrayList<String>> caseInfo = new ArrayList<ArrayList<String>>();
		ArrayList<String> caseName = new ArrayList<String>();
		ArrayList<String> caseTime = new ArrayList<String>();
		try {
			File f = new File(fileName);
			DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			// testCase节点
			NodeList nl = doc.getElementsByTagName("testCase");
			
			String tcName = null;
			int len = nl.getLength();
			for(int i=0;i<len;i++){
				Element testCaseElement = (Element) nl.item(i);
				tcName = testCaseElement.getAttribute("name");
				// 判断 子节点的name 是否等于testCaseName
				if(testCaseName.equals(tcName.trim())){
					NodeList childNodes = testCaseElement.getChildNodes();
					
					String key=null;
					String value=null;
					int childLen = childNodes.getLength();
					// 遍历 testCaseName节点的子节点
					for(int j=0;j<childLen;j++){
						if(childNodes.item(j).getNodeType() == Node.ELEMENT_NODE){
							key = childNodes.item(j).getNodeName();
							try{
								value = childNodes.item(j).getFirstChild().getNodeValue();
							}catch(Exception e){
								Log.d(TAG,key+"'s value is null.");
							}
							if(key !=null){
								caseName.add(key);
								caseTime.add(value);
//								hm.put(key, value);
							}
						}
					}
					caseInfo.add(caseName);
					caseInfo.add(caseTime);
				}
			}
		} catch (Exception e) {
			Log.e(TAG,"xmlParser failure.",e);
		}
		return caseInfo;
	}
	
//	//运行logcat
//	public static boolean runLogcat(String fileName){
//		String cmd = "logcat -v time -f /sdcard/smartPM/log/"+fileName+".txt";
//		
//		try {
//			Runtime.getRuntime().exec(cmd);
//		} catch (IOException e) {
//			Log.e(TAG,"runLogcat failure.",e);
//			return false;
//		}
//		return true;
//	}
	
	public static boolean saveLog2File(){
		String filePath;
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
             File sdcardDir =Environment.getExternalStorageDirectory();
             filePath=sdcardDir.getPath()+"/smartPower";
		}else{
			filePath = "/data/local/smartPower";
		}
		Log.d(TAG,"filePath= "+filePath);
		File fileDir = new File(filePath);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		String fileName = "log_"+getCurrentTime()+".txt";
		File file = new File(fileDir, fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e1) {
				Log.e(TAG,"create New File failure.",e1);
			}
		}
		
		String cmd = "logcat -v time -f "+filePath+"/"+fileName;
		try {
			Runtime.getRuntime().exec(cmd);
			
		} catch (IOException e) {
			Log.e(TAG,"log to file failure.",e);
		}
		return true;
	}
	
	public static boolean stopLog2File(){
//		String cmds ="pid=`ps | grep logcat | awk '{print $2}'` && kill $pid";
		try {
			String psCmd = "ps | grep logcat";
			Process p =Runtime.getRuntime().exec(psCmd);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String psString = br.readLine();
			while((psString = br.readLine()) !=null){
				String psNum = psString.split("\\s+")[1].trim();
				Log.i(TAG,"psNum="+psNum);
				Runtime.getRuntime().exec("kill "+psNum.trim());
			}
		} catch (IOException e) {
			Log.e(TAG,"stop log to file failure.",e);
			return false;
		}
		return true;
	}
	
	
	
	public static String getCurrentTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
	
	/**
	 * 根据 应用程序名，获取包名和activity名
	 */
	public static List<String> getAppInfoByName(Context context,String label){
		ArrayList<String> packageAndActivity = new ArrayList<String>();
		PackageManager manager = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		// 将获取到的APP的信息按名字进行排序
		for (ResolveInfo info : apps) {
			ComponentInfo ci = info.activityInfo;
			if(manager.getApplicationLabel(ci.applicationInfo).equals(label)){
				packageAndActivity.add(ci.packageName);
				packageAndActivity.add(ci.name);
				break;
			}
		}
		return packageAndActivity;
	}
}
