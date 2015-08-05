/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.items;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.data.ChartData;
import com.yhh.utils.NumberUtils;

/**
 * baseclass of the chart-listview items
 * @author philipp
 *
 */
public abstract class ChartItem {
    
    protected static final int TYPE_BARCHART = 0;
    protected static final int TYPE_LINECHART = 1;
    protected static final int TYPE_PIECHART = 2;
    protected ChartData<?> mChartData;
    
    private Float[] mStatisticValues;
    protected Boolean mIsPercentData;
    
    
    public ChartItem(ChartData<?> cd, boolean isPercentData) {
        this.mChartData = cd;   
        mIsPercentData = isPercentData;
        initStatistic();
    }
    
    private void initStatistic(){
        mStatisticValues = new Float[3];
        mStatisticValues[0]= mChartData.getYValueSum()/mChartData.getYValCount();
        mStatisticValues[1]= mChartData.getYMin();
        mStatisticValues[2]= mChartData.getYMax();
    }
    
    public Float[] getStatisticValues() {
        return mStatisticValues;
    }
    
    public String[] getFormattedStatistic(){
        String[] formattedValues = new String[3];
        for(int i=0;i<3;i++){
            formattedValues[i] = NumberUtils.formatDecimal(mStatisticValues[i], 2, mIsPercentData);
        }
        return formattedValues;
    }
    
    public String[] getFormattedStatistic(int digit){
        String[] formattedValues = new String[3];
        for(int i=0;i<3;i++){
            formattedValues[i] = NumberUtils.formatDecimal(mStatisticValues[i], digit, mIsPercentData);
        }
        return formattedValues;
    }
    
    public abstract int getItemType();
    
    public abstract View getView(int position, View convertView, Context c);
}
