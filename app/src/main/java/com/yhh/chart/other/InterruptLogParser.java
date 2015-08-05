/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.other;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.yhh.utils.ShellUtils;
import com.yhh.utils.ShellUtils.CommandResult;

public class InterruptLogParser{
    private HashMap<Integer,String> mIrqMap;
    private static final String CMD_INTERRUPTS = "cat /proc/interrupts";
    
    public InterruptLogParser(String logPath){
        mIrqMap = new HashMap<Integer,String>();
        init(logPath);
    }
    
    public InterruptLogParser(){
        mIrqMap = new HashMap<Integer,String>();
        initLocal();
    }
    
    private void initLocal(){
        CommandResult cr = ShellUtils.execCommand(CMD_INTERRUPTS, false);
        String interuptInfo = cr.successMsg;
        String[] interuptArray = interuptInfo.split("\n");
        int len = interuptArray.length;
        
        int key;
        String value;
        for(int i=1;i<len;i++){
            String[] tmp = interuptArray[i].split(":");
            if(tmp !=null && tmp.length>1){
                try{
                    key = Integer.valueOf(tmp[0].trim());
                    String[] valueArr = tmp[1].split("\\s{2,}");
                    value = valueArr[valueArr.length-1];
                    mIrqMap.put(key, value.trim());        
                }catch(NumberFormatException e){
                    // don't we need.
                }
            }
        }
    }
    
    private void init(String logPath){
        BufferedReader br = null;
        String line;
        int key;
        String value;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(logPath)));
            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(":");
                if(tmp !=null && tmp.length>1){
                    try{
                        key = Integer.valueOf(tmp[0].trim());
                        String[] valueArr = tmp[1].split("\\s{2,}");
                        value = valueArr[valueArr.length-1];
                        mIrqMap.put(key, value.trim());        
                    }catch(NumberFormatException e){
                        // don't we need.
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public String getInterruptName(int irq){
        String irqName = null;
        irqName = mIrqMap.get(irq);
        return irqName;
    }
}
