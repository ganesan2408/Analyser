/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.custom;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.yhh.analyser.R;
import com.yhh.chart.base.ChartTool;
import com.yhh.utils.NumberUtils;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private int mMode;
    
    public MyMarkerView(Context context, int layoutResource, int mode) {
        super(context, layoutResource);
        mMode = mode;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }
    
    public MyMarkerView(Context context, int layoutResource) {
        this(context, layoutResource,0);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, int dataSetIndex) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
            if(mMode == 1){
                tvContent.setText(ChartTool.getInstance().index2hhmm(ce.getXIndex())
                        +"("+NumberUtils.formatDecimal(ce.getHigh(),2,true)+")");
            }else{
                tvContent.setText(ChartTool.getInstance().index2hhmm(ce.getXIndex())
                        +"("+ce.getHigh()+")");
            }
        } else {
            if(mMode == 1){
                tvContent.setText(ChartTool.getInstance().index2hhmm(e.getXIndex())
                        +"("+NumberUtils.formatDecimal(e.getVal(),2,true)+")");
            }else{
                tvContent.setText(ChartTool.getInstance().index2hhmm(e.getXIndex())
                        +"("+e.getVal()+")");
            }
        }
    }

    @Override
    public int getXOffset() {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
