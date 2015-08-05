/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.fragment.performance;

import java.lang.reflect.Method;

import android.util.Log;

import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;
//import org.codeaurora.Performance;

public class MyPerf {
    private static String TAG= ConstUtils.DEBUG_TAG+"MyPerf";
    
    public static final int CPU_LITTER_FREQ_NUM = 9;
    public static final int CPU_BIG_FREQ_NUM = 13;
    public static final int CPU_LITTER_NUM = 4;
    public static final int CPU_BIG_NUM = 2;
    
    public static final int[] CPU_LITTER_FREQ = new int[]{
        384000,460800,600000,
        672000,787200,864000,
        960000,1248000,1440000
    };
    
    public static final int[] CPU_BIG_FREQ = new int[]{
        384000,480000,633600,
        768000,864000,960000,
        1248000,1344000,1440000,
        1536000,1632000,1689600,
        1824000
    };
    
    public static final int GPU_FREQ_NUM=6;
    public static final int[] GPU_FREQ = new int[]{
        600,490,450,367,300,180  
    };
    
    private static MyPerf mMyPerf;
    private int mSpecial;
    
    //////////////////////////////////////////////////
    private MyPerf(){
        mSpecial = execute(0xff00);
        Log.i(TAG,"perfLockAcquire return ====>"+mSpecial);
    }
    
    public static MyPerf getInstance(){
        if(mMyPerf ==null){
            mMyPerf = new MyPerf();
        }
        return mMyPerf;
    }
    
    public int getLittleCpuFreq(int maxFreq,int minFreq){
        int max = (int)(CPU_LITTER_FREQ[maxFreq]/10000);
        int min = (int)(CPU_LITTER_FREQ[minFreq]/10000);
        Log.i(TAG,"set little cpu frq ["+min+","+max+"]");
        int sources = (max << 24) | (min << 16) | 01 | (mSpecial<<8);
        return sources;
    }
    
    public int getBigCpuFreq(int maxFreq,int minFreq){
        int max = (int)(CPU_BIG_FREQ[maxFreq]/10000);
        int min = (int)(CPU_BIG_FREQ[minFreq]/10000);
        Log.i(TAG,"set big cpu frq ["+min+","+max+"]");
        int sources = (max << 24) | (min << 16) |02 | (mSpecial<<8);
        return sources;
    }
    
    /**
     * 0~7位： 
     *      0x03代表CPU开关和控制
     *      
     * 8~15位：
     *      扩展参数（mSpecial 通过底层返回获取）
     *      
     * 16~23位：
     *      16~19：小核开关
     *      20~23：大核开关
     *      
     * 24~31位：
     *      空
     * 
     * @param bigNum
     *      大核个数
     * @param littleNum
     *      小核个数
     * @return
     *      获取32位的二进制串
     */
    public int getCpuOn(int bigNum,int littleNum){
        Log.i(TAG,"bigNum="+bigNum+", littleNum="+littleNum);
        if(bigNum<1 && littleNum<1){
            littleNum = 1;
        }
        int max = (int) (Math.pow(2, bigNum)-1);
        int min = (int) (Math.pow(2, littleNum)-1);
        int sources = (max << 20) | (min << 16) | 03 | (mSpecial<<8);
        return sources;
    }
    
    public int getGpuFreq(int maxFreq,int minFreq){
        int sources = (1<<31) | (maxFreq << 24) | (1<<23) | (minFreq << 16) | 04 | (mSpecial<<8);
        return sources;
    }
    
    public int execute(int[] sources){
        Log.i(TAG,"execute ==>  sources[0]="+sources[0]);
        int rtn = -1;
//        Performance mPerf = new Performance();
//        rtn = mPerf.perfLockAcquire(0, sources);
        
        try {
            Class<?> clazz = Class.forName("org.codeaurora.Performance");
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod("perfLockAcquire", int.class, int[].class);
            rtn = (Integer) method.invoke(obj, 0, sources);
        } catch (Exception e) {
            Log.e(TAG,"perf acquire",e);
        }
        return rtn;
    }
    
    public int execute(int source){
        Log.i(TAG,"execute ==>  source="+source);
        int rtn = -1;
        
        try {
            Class<?> clazz = Class.forName("org.codeaurora.Performance");
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod("perfLockAcquire", int.class, int[].class);
            rtn = (Integer) method.invoke(obj, 0, new int[]{source});
        } catch (Exception e) {
            Log.e(TAG,"perf acquire",e);
        }
        return rtn;
    }
    
    ///////////////////////////////////
    private static int getNodeValue(String cmd){
        int value=1;
        String cmdStr = FileUtils.getCommandNodeValue(cmd);
        if(!cmdStr.equals("")){
            try{
                value = Integer.valueOf(cmdStr.trim());
            }catch(NumberFormatException e){
                Log.e(TAG,"getNodeValue NumberFormatException");
            }
        }
        return value;
    }
    
    public static int getBigCpuFreqLevel(String freqCmd){
        int bigFreq = getNodeValue(freqCmd);
        for(int i=0;i<CPU_BIG_FREQ_NUM;i++){
            if(CPU_BIG_FREQ[i]==bigFreq){
                return i;
            }
        }
        return 1;
    }
    
    public static int getLitterCpuFreqLevel(String freqCmd){
        int litterFreq = getNodeValue(freqCmd);
        for(int i=0;i<CPU_LITTER_FREQ_NUM;i++){
            if(CPU_LITTER_FREQ[i]==litterFreq){
                return i;
            }
        }
        return 1;
    }
    
    public static int[] getCpuOnline(String onlineCmd){
        int status[] = new int[2];
        String cmdStr = FileUtils.getCommandNodeValue(onlineCmd);
        if(cmdStr.equals("")){
            return status;
        }
        cmdStr = cmdStr.trim();
        if(cmdStr.contains(",")){
            String[] cmdArr = cmdStr.split(",");
            if(cmdArr[0].contains("-")){
                int index = cmdArr[0].indexOf("-");
                int tmp = Integer.valueOf(cmdArr[0].substring(index+1));
                status[0]= tmp+1;
            }else{
                status[0]=1;
            }
            
            if(cmdArr[1].contains("-")){
                int index = cmdArr[1].indexOf("-");
                int tmp = Integer.valueOf(cmdArr[1].substring(index+1));
                status[1]= tmp-3;
            }else{
                status[1]=1;
            }
        }else{ //不包含，
           if(cmdStr.contains("-")){ // 含有 - 的情况
               int index = cmdStr.indexOf("-");
               int end = Integer.valueOf(cmdStr.substring(index+1));
               int start = Integer.valueOf(cmdStr.substring(index-1,index));
               int num = end-start+1;
               if(start==0){
                   status[0]= num>4?4:num;
                   status[1]=num>4?num-4:0;
               }else{
                   status[0]= 0;
                   status[1]= num;
               }
           }else{ //只有一个数字的情况
               int tmp = Integer.valueOf(cmdStr);
               if(tmp==0){
                   status[0]=1;
                   status[1]=0;
               }else{
                   status[0]=0;
                   status[1]=1;
               }
           }
        }
        Log.i(TAG,"Litter core="+status[0]+",Big core="+status[1]);
        return status;
    }
    
    public static int getGpuFreqLevel(String freqCmd){
        int level = getNodeValue(freqCmd);
        return level;
    }
    
}
