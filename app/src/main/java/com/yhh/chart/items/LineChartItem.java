/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.items;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.yhh.analyser.R;
import com.yhh.chart.custom.MyValueFormatter;

public class LineChartItem extends ChartItem {
    
    public LineChartItem(ChartData<?> cd, Context c, Boolean isPercentData) {
        super(cd, isPercentData);
    }

    @Override
    public int getItemType() {
        return TYPE_LINECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.chart_list_item_linechart, null);
            holder.chart = (LineChart) convertView.findViewById(R.id.chart);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.setDrawGridBackground(true);

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        ValueFormatter custom = new MyValueFormatter();
        
        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setLabelCount(8);
        leftAxis.setStartAtZero(false);
        leftAxis.setValueFormatter(custom);
        
        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setLabelCount(8);
        rightAxis.setStartAtZero(false);
        rightAxis.setValueFormatter(custom);
        
        // set data
        holder.chart.setData((LineData) mChartData);
        
        Legend l = holder.chart.getLegend();
        l.setPosition(LegendPosition.LEFT_OF_CHART_INSIDE);
        l.setTextColor(Color.MAGENTA);
        l.setXOffset(35f);

         holder.chart.invalidate();
//        holder.chart.animateX(1000);
        holder.chart.setMaxVisibleValueCount(6);
        holder.chart.setDoubleTapToZoomEnabled(true);
        
        String[] mCalcValues = getFormattedStatistic();
        
        holder.chart.setDescription("Avg="+ mCalcValues[0]
                + ", Min=" + mCalcValues[1]
                + ", Max=" + mCalcValues[2]);

        return convertView;
    }
    
    private static class ViewHolder {
        LineChart chart;
    }
}
