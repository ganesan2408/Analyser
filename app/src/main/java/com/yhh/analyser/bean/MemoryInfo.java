/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.yhh.analyser.utils.ConstUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * operate memory information
 * 
 */
public class MemoryInfo {
	private static final String LOG_TAG =  ConstUtils.DEBUG_TAG+ "MemoryInfo";
	
    /** memory node*/
	private static final String MEM_INFO_PATH = "/proc/meminfo";
	private static final String MEM_TOTAL_KEY = "MemTotal";
	
	/**
	 * get total memory of certain device.
	 * 
	 * @return total memory of device
	 */
	public long getTotalMemory() {
		String readTemp = "";
		String memTotal = "";
		long memory = 0;
		try {
			FileReader fr = new FileReader(MEM_INFO_PATH);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((readTemp = localBufferedReader.readLine()) != null) {
				if (readTemp.contains(MEM_TOTAL_KEY)) {
					String[] total = readTemp.split(":");
					memTotal = total[1].trim();
				}
			}
			localBufferedReader.close();
			String[] memKb = memTotal.split(" ");
			memTotal = memKb[0].trim();
			memory = Long.parseLong(memTotal);
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException: " + e.getMessage());
		}
		return memory;
	}


	public long getFreeMemorySize(Context context) {
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.getMemoryInfo(outInfo);
		long avaliMem = outInfo.availMem;
		return avaliMem / 1024;
	}

	public int getPidMemorySize(int pid, Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int[] myMempid = new int[] { pid };
		Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
		memoryInfo[0].getTotalSharedDirty();
		int memSize = memoryInfo[0].getTotalPss();
		return memSize;
	}
}
