/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.utils;

import java.text.DecimalFormat;

public class NumberUtils {
    
    public static String formatDecimal(float number, int digits, boolean isPercent) {

        StringBuffer a = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                a.append(".");
            a.append("0");
        }
        
//        if(isPercent)
//            a.append("%");
        
        DecimalFormat nf = new DecimalFormat("###,###,###,##0" + a.toString());
        String formatted = nf.format(number);

        return formatted;
    }
}
