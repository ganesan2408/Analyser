/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.widget.chart.custom;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class CurrentFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    
    public CurrentFormatter() {
        mFormat = new DecimalFormat("###0.#");
    }
    
    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value)+" mA";
    }

}
