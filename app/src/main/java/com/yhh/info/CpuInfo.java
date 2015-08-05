/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.info;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import android.os.Build;
import android.util.Log;

import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;

/**
 * CPU information
 * 
 */
class CpuInfo {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "CpuInfo";
	private boolean DEBUG = false;
	
	/** process ratio*/
    private String processCpuRatio = "";
	/** process cpu*/
	private long processCpu;
	private long processCpu2;
	
	/** tatol ratio*/
    private ArrayList<String> totalCpuRatio = new ArrayList<String>();
	/** idle cpu*/
	private ArrayList<Long> idleCpuList = new ArrayList<Long>();
	private ArrayList<Long> idleCpu2List = new ArrayList<Long>();
	/** tatol cpu*/
	private ArrayList<Long> totalCpuList = new ArrayList<Long>();
	private ArrayList<Long> totalCpu2List = new ArrayList<Long>();
	
	private DecimalFormat formart;
	
	private static final String INTEL_CPU_NAME = "model name";
    private static final String CPU_X86 = "x86";
    /** CPU freq*/
    public static final String CPU_DIR_PATH = "/sys/devices/system/cpu/";
    /** CPU node*/
    public static final String CPU_INFO_PATH = "/proc/cpuinfo";
    public static final String CPU_STAT = "/proc/stat";
    
	public CpuInfo() {
	    formart = new DecimalFormat();
	    formart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	    formart.setGroupingUsed(false);
	    formart.setMaximumFractionDigits(2);
	    formart.setMinimumFractionDigits(1);
	}

	private void readCpuStatByPid(int pid) {
	    if(pid <=0){
            return;
        }
		String processPid = Integer.toString(pid);
		String cpuStatPath = "/proc/" + processPid + "/stat";
		try {
			RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath, "r");
			String line = "";
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.setLength(0);
			while ((line = processCpuInfo.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}
			String[] tok = stringBuffer.toString().split(" ");
			processCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
			if(DEBUG){
                Log.d(TAG,"processCpu:" +processCpu);
            }
			processCpuInfo.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
		    Log.e(TAG, "IOException: " + e.getMessage());
		}
	}

	private void readTotalCpuStat() {
		try {
			// monitor total and idle cpu stat of certain process
			RandomAccessFile cpuInfo = new RandomAccessFile(CPU_STAT, "r");
			String line = "";
			while ((null != (line = cpuInfo.readLine())) && line.startsWith("cpu")) {
				String[] toks = line.split("\\s+");
				idleCpuList.add(Long.parseLong(toks[4]));
				totalCpuList.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
						+ Long.parseLong(toks[5]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]));
			}
			cpuInfo.close();
			if(DEBUG){
			    StringBuilder sb = new StringBuilder();
			    StringBuilder sb2 = new StringBuilder();
			    for(int i=0;i<idleCpuList.size();i++){
			        sb.append(idleCpuList.get(i)+",");
			        sb2.append(totalCpuList.get(i)+",");
			    }
			    Log.d(TAG,"total idleCpuList:" +sb.toString());
			    Log.d(TAG,"total CpuList:" +sb2.toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCpuStat(int pid){
	    idleCpuList.clear();
        totalCpuList.clear();
        totalCpuRatio.clear();
        
	    readCpuStatByPid(pid);
	    readTotalCpuStat();
	}
	
    public String getCpuName() {
        try {
            RandomAccessFile cpuStat = new RandomAccessFile(CPU_INFO_PATH, "r");
            if (Build.CPU_ABI.equalsIgnoreCase(CPU_X86)) {
                String line;
                while (null != (line = cpuStat.readLine())) {
                    String[] values = line.split(":");
                    if (values[0].contains(INTEL_CPU_NAME)) {
                        cpuStat.close();
                        return values[1];
                    }
                }
            } else {
                String[] cpu = cpuStat.readLine().split(":");
                cpuStat.close();
                return cpu[1];
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        return "";
    }
    

	public int getCpuNum() {
		try {
			File dir = new File(CPU_DIR_PATH);
			File[] files = dir.listFiles(new CpuFilter());
			return files.length;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	public ArrayList<String> getCpuListName() {
		ArrayList<String> cpuList = new ArrayList<String>();
		try {
			File dir = new File(CPU_DIR_PATH);
			File[] files = dir.listFiles(new CpuFilter());
			for (int i = 0; i < files.length; i++) {
				cpuList.add(files[i].getName());
			}
			return cpuList;
		} catch (Exception e) {
			e.printStackTrace();
			cpuList.add("cpu0");
			return cpuList;
		}
	}
	
	public String getCpuFreqList(){
       StringBuilder freqBuilder  = new StringBuilder();
        int len = getCpuNum();
        for(int i= 0;i<len;i++){
            Long curFreq = -1L;
            String node = CPU_DIR_PATH+"cpu"+i+"/cpufreq/scaling_cur_freq";
            
            String curFreqStr = FileUtils.getCommandNodeValue(node);
            if(curFreqStr !=null && !curFreqStr.trim().equals("")){
                curFreq = Long.valueOf(curFreqStr.trim())/1000;
            }
            
            if(i == len-1){
                freqBuilder.append(curFreq);
            }else{
                freqBuilder.append(curFreq+"/");
            }
        }
        return freqBuilder.toString();
    }
	
    public ArrayList<String> getTotalCpuRatio() {
		
        if (null != totalCpu2List && totalCpu2List.size() > 0) {
            for (int i = 0; i < (totalCpuList.size() > totalCpu2List.size() ? totalCpu2List.size() : totalCpuList.size()); i++) {
                String cpuRatio = "0";
                if (totalCpuList.get(i) - totalCpu2List.get(i) > 0) {
                    cpuRatio = formart.format(100 * ((double) ((totalCpuList.get(i) - idleCpuList.get(i)) 
                            - (totalCpu2List.get(i) - idleCpu2List.get(i))) 
                            / (double) (totalCpuList.get(i) - totalCpu2List.get(i))));
                }
                totalCpuRatio.add(cpuRatio);
            }
        } else {
            totalCpuRatio.add("0");
            totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
            idleCpu2List = (ArrayList<Long>) idleCpuList.clone();
        }
        if(DEBUG){
            Log.i(TAG,"totalCpuRatio length="+totalCpuRatio.size());
        }
        totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
        idleCpu2List = (ArrayList<Long>) idleCpuList.clone();
		return totalCpuRatio;
	}

    public String getProcessCpuRatio(int pid) {
        
        if (null != totalCpu2List && totalCpu2List.size() > 0) {
            processCpuRatio = formart.format(100 * ((double) (processCpu - processCpu2) 
                    / ((double) (totalCpuList.get(0) - totalCpu2List.get(0)))));
        } else {
            processCpuRatio = "0";
            processCpu2 = processCpu;
        }
        processCpu2 = processCpu;
        return processCpuRatio;
    }
    
    class CpuFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                return true;
            }
            return false;
        }
    }
}
