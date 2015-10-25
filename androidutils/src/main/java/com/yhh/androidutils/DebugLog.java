/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.androidutils;

import android.util.Log;

public class DebugLog {

	private static String className;
	private static String methodName;
	private static int lineNumber;
	private static final String TAG="sa_";
	private static boolean isDebug = true;
	
    private DebugLog(){
    }

	public static void setDebuggable(boolean b) {
		isDebug = b;
	}

	private static String createLog( String log ) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]");
		buffer.append(log);

		return buffer.toString();
	}
	
	private static void getMethodNames(StackTraceElement[] sElements){
		className =  TAG + sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void e(String message){
		if (!isDebug)
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	public static void i(String message){
		if (!isDebug)
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}
	
	public static void d(String message){
		if (!isDebug)
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}
	
	public static void v(String message){
		if (!isDebug)
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}
	
	public static void w(String message){
		if (!isDebug)
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}
	
	public static void wtf(String message){
		if (!isDebug)
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}

}
