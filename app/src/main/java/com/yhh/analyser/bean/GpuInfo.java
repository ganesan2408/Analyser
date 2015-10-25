/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.bean;

import android.util.Log;

import com.yhh.analyser.utils.ConstUtils;
import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.StringUtils;

public class GpuInfo {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "GpuInfo";
    private boolean DEBUG =false;

   public static final String GPU_CLK =  "/sys/class/kgsl/kgsl-3d0/gpuclk";
   public static final String GPU_BUSY = "/sys/class/kgsl/kgsl-3d0/gpubusy";

    /**
     * get cpu clock
     * 
     * @return
     *      unit: MHz
     */
    public double getGpuClock(){
        double clk = -1;
        String gpuStr = FileUtils.readFile(GPU_CLK);
        if(!StringUtils.isBlank(gpuStr)){
            try{
                clk = Integer.valueOf(gpuStr.trim())/1000.0/1000.0;
            }catch(NumberFormatException e){
                Log.e(TAG,"getGpuClock NumberFormatException");
            }
        }
        return clk;
    }
    
    public double getGpuRate(){
        double rate = -1;
        String gpuStr = FileUtils.readFile(GPU_BUSY);
        if(StringUtils.isBlank(gpuStr)){
            return rate;
        }
        String[] arr = gpuStr.trim().split("\\s+");
        if(arr !=null && arr.length >=2){
            try{
                int usedCpu = Integer.valueOf(arr[0].trim());
                int totalGpu = Integer.valueOf(arr[1].trim());
                if(DEBUG){
                    Log.i(TAG,"usedCpu="+usedCpu+",totalGpu="+totalGpu);
                }
                if(totalGpu > 0){
                    rate = (double)usedCpu /totalGpu *100.0;
                }else if(totalGpu == 0){
                    rate = 0;
                }
                if(DEBUG){
                    Log.i(TAG,"rate="+rate);
                }
            }catch(Exception e){
                Log.e(TAG,"getGpuRate exception");
            }
        }
        return rate;
    }

//  public double getBimcClock(){
//  double clk = -1;
//  String gpuStr = FileUtils.getCommandNodeValue(CommandUtils.BIMC_CLOCK);
//  if(!gpuStr.trim().equals("")){
//      try{
//          clk = (double) (Integer.valueOf(gpuStr.trim())/1000.0/1000.0);
//      }catch(NumberFormatException e){
//          Log.e(TAG,"getBimcClock NumberFormatException");
//      }
//  }
//  return clk;
//}

//public double getAfabCock(){
//  double clk = -1;
//  String gpuStr = FileUtils.getCommandNodeValue(CommandUtils.AFAB_CLOCK);
//  if(!gpuStr.trim().equals("")){
//      try{
//          clk = (double) (Integer.valueOf(gpuStr.trim())/1000.0/1000.0);
//      }catch(NumberFormatException e){
//          Log.e(TAG,"getAfabCock NumberFormatException");
//      }
//  }
//  return clk;
//}

}
