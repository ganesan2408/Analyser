/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.widget.chart.items;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.yhh.analyser.R;

public class BarChartItem extends ChartItem {
    
    
    public BarChartItem(ChartData<?> cd, Context c, Boolean isPercentData) {
        super(cd, isPercentData);
    }

    @Override
    public int getItemType() {
        return TYPE_BARCHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.chart_list_item_barchart, null);
            holder.chart = (BarChart) convertView.findViewById(R.id.chart);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);
        holder.chart.setDrawBarShadow(false);
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        
        
        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setSpaceTop(20f);
       
        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setLabelCount(5);
        rightAxis.setSpaceTop(20f);

        // set data
        holder.chart.setData((BarData) mChartData);
        
        Legend l = holder.chart.getLegend();
        l.setPosition(LegendPosition.LEFT_OF_CHART_INSIDE);
        l.setTextColor(Color.MAGENTA);
        l.setXOffset(35f);
        
        holder.chart.invalidate();

        return convertView;
    }
    
    private static class ViewHolder {
        BarChart chart;
    }
}
