/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.model;

import android.util.Log;

import com.yhh.analyser.utils.LogUtils;
import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * CPU information
 *
 */
public class CpuInfo {
    private static final String TAG = LogUtils.DEBUG_TAG + "CpuInfo";
    private boolean DEBUG = false;

    /** process ratio*/
    private String processCpuRatio = "";
    /** process cpu*/
    private long processCpu;
    private long processCpu2;

    /** total ratio*/
    private ArrayList<String> totalCpuRatio = new ArrayList<>();
    /** idle cpu*/
    private ArrayList<Long> idleCpuList = new ArrayList<>();
    private ArrayList<Long> idleCpu2List = new ArrayList<>();
    /** total cpu*/
    private ArrayList<Long> totalCpuList = new ArrayList<>();
    private ArrayList<Long> totalCpu2List = new ArrayList<>();

    private DecimalFormat formart;

    private static final String INTEL_CPU_NAME = "model name";
    private static final String CPU_X86 = "x86";
    /** CPU freq*/
    public static final String CPU_DIR_PATH = "/sys/devices/system/cpu/";
    /** CPU node*/
    public static final String CPU_INFO_PATH = "/proc/cpuinfo";
    public static final String CPU_STAT = "/proc/stat";

    private int mCpuNum;

    public CpuInfo() {
        formart = new DecimalFormat();
        formart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        formart.setGroupingUsed(false);
        formart.setMaximumFractionDigits(2);
        formart.setMinimumFractionDigits(1);
        mCpuNum = getCpuNums();
    }

    private void readCpuStatByPid(int pid) {
        if (pid <= 0) {
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
            processCpuInfo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readTotalCpuStat() {
        try {
            RandomAccessFile cpuFile = new RandomAccessFile(CPU_STAT, "r");
            String line;
            while ((null != (line = cpuFile.readLine())) && line.startsWith("cpu")) {
                String[] toks = line.split("\\s+");
                idleCpuList.add(Long.parseLong(toks[4]));
                totalCpuList.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2])
                        + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                        + Long.parseLong(toks[5]) + Long.parseLong(toks[6])
                        + Long.parseLong(toks[7]));
            }
            cpuFile.close();
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                for (int i = 0; i < idleCpuList.size(); i++) {
                    sb.append(idleCpuList.get(i) + ",");
                    sb2.append(totalCpuList.get(i) + ",");
                }
                Log.d(TAG, "total idleCpuList:" + sb.toString());
                Log.d(TAG, "total CpuList:" + sb2.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateCpu(int pid) {
        idleCpuList.clear();
        totalCpuList.clear();
        totalCpuRatio.clear();
        readCpuStatByPid(pid);
        readTotalCpuStat();
    }

    public void updateAllCpu() {
        idleCpuList.clear();
        totalCpuList.clear();
        totalCpuRatio.clear();
        readTotalCpuStat();
    }

//    public String getCpuName() {
//        try {
//            RandomAccessFile cpuStat = new RandomAccessFile(CPU_INFO_PATH, "r");
//            if (Build.CPU_ABI.equalsIgnoreCase(CPU_X86)) {
//                String line;
//                while (null != (line = cpuStat.readLine())) {
//                    String[] values = line.split(":");
//                    if (values[0].contains(INTEL_CPU_NAME)) {
//                        cpuStat.close();
//                        return values[1];
//                    }
//                }
//            } else {
//                String[] cpu = cpuStat.readLine().split(":");
//                cpuStat.close();
//                return cpu[1];
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "IOException: " + e.getMessage());
//        }
//        return "";
//    }


    public int getCpuNums() {
        try {
            File dir = new File(CPU_DIR_PATH);
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

//	public ArrayList<String> getCpuListName() {
//		ArrayList<String> cpuList = new ArrayList<String>();
//		try {
//			File dir = new File(CPU_DIR_PATH);
//			File[] files = dir.listFiles(new CpuFilter());
//			for (int i = 0; i < files.length; i++) {
//				cpuList.add(files[i].getName());
//			}
//			return cpuList;
//		} catch (Exception e) {
//			e.printStackTrace();
//			cpuList.add("cpu0");
//			return cpuList;
//		}
//	}

    /**
     * 获取Cpu频率列表
     *
     * @return
     */
    public String getCpuFreqs() {
        StringBuilder freqBuilder = new StringBuilder();
        for (int i = 0; i < mCpuNum; i++) {
            Long curFreq = -1L;
            String node = CPU_DIR_PATH + "cpu" + i + "/cpufreq/scaling_cur_freq";

            String curFreqStr = FileUtils.readFile(node);
            if (!StringUtils.isBlank(curFreqStr)) {
                curFreq = Long.valueOf(curFreqStr.trim()) / 1000;
            }

            if (i == mCpuNum - 1) {
                freqBuilder.append(curFreq);
            } else {
                freqBuilder.append(curFreq + "/");
            }
        }
        return freqBuilder.toString();
    }

    /**
     * 获取所有使用率
     *
     * @return
     */
    public ArrayList<String> getCpuRatioList() {
        int len = (null == totalCpu2List ? 0 : totalCpu2List.size());
        int loops = totalCpuList.size() > totalCpu2List.size() ? totalCpu2List.size() : totalCpuList.size();
        String cpuRatio = "0";
        if (len > 0) {
            for (int i = 0; i < loops; i++) {
                if (totalCpuList.get(i) - totalCpu2List.get(i) > 0) {
                    cpuRatio = formart.format(100 * ((double) ((totalCpuList.get(i) - idleCpuList.get(i))
                            - (totalCpu2List.get(i) - idleCpu2List.get(i)))
                            / (double) (totalCpuList.get(i) - totalCpu2List.get(i))));
                }
                totalCpuRatio.add(cpuRatio);
            }
        } else {
            totalCpuRatio.add(cpuRatio);
            totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
            idleCpu2List = (ArrayList<Long>) idleCpuList.clone();
        }
        if (DEBUG) {
            Log.i(TAG, "totalCpuRatio length=" + totalCpuRatio.size());
        }
        totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
        idleCpu2List = (ArrayList<Long>) idleCpuList.clone();
        return totalCpuRatio;
    }

//    /**
//     * 获取使用率
//     *
//     * @return
//     */
//    public ArrayList<String> getTotalCpuRatio() {
//        if (null != totalCpu2List && totalCpu2List.size() > 0) {
//            for (int i = 0; i < (totalCpuList.size() > totalCpu2List.size() ? totalCpu2List.size() : totalCpuList.size()); i++) {
//                String cpuRatio = "0";
//                if (totalCpuList.get(i) - totalCpu2List.get(i) > 0) {
//                    cpuRatio = formart.format(100 * ((double) ((totalCpuList.get(i) - idleCpuList.get(i))
//                            - (totalCpu2List.get(i) - idleCpu2List.get(i)))
//                            / (double) (totalCpuList.get(i) - totalCpu2List.get(i))));
//                }
//                totalCpuRatio.add(cpuRatio);
//            }
//        } else {
//            totalCpuRatio.add("0");
//            totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
//            idleCpu2List = (ArrayList<Long>) idleCpuList.clone();
//        }
//        if (DEBUG) {
//            Log.i(TAG, "totalCpuRatio length=" + totalCpuRatio.size());
//        }
//        totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
//        idleCpu2List = (ArrayList<Long>) idleCpuList.clone();
//        return totalCpuRatio;
//    }

    /**
     *该方法用于与getCpuTotalUsedRatio配合使用
     *
     * @return
     */
    public String getCpuRatio() {

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

    /**
     * 该方法是单独使用
     *
     * @return
     */
    public String getCpuRatioComplete() {

        if (null != totalCpu2List && totalCpu2List.size() > 0) {
            processCpuRatio = formart.format(100 * ((double) (processCpu - processCpu2)
                    / ((double) (totalCpuList.get(0) - totalCpu2List.get(0)))));
        } else {
            processCpuRatio = "0";
            processCpu2 = processCpu;
        }
        processCpu2 = processCpu;
        totalCpu2List = (ArrayList<Long>) totalCpuList.clone();
        return processCpuRatio;
    }

    class CpuFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return Pattern.matches("cpu[0-9]", pathname.getName());
        }
    }
}
