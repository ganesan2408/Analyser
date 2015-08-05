/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.base;


public class Parser {
    protected boolean mIsParse;
    
    public void parse(){
        mIsParse=true;
    }
    
    public void stop(){
        mIsParse = false;
    }

}
