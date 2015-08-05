/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.custom;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class TempFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    
    public TempFormatter() {
        mFormat = new DecimalFormat("#0.#");
    }
    
    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value)+" ℃";
    }

}
