/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.chart.other;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.yhh.analyser.R;
import com.yhh.chart.base.ChartBaseActivity;
import com.yhh.log.analyser.MainLogAnalyser;
import com.yhh.log.model.WakeupInfo;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;

import java.util.ArrayList;

public class SleepChart extends ChartBaseActivity implements  OnChartValueSelectedListener {
    private static final String TAG = ConstUtils.DEBUG_TAG + "SleepChart";
    private boolean DEBUG = true;
    
    private PieChart mChart;
    private SleepDataLogParser mSleepLogParser;
    private String mWakeupContent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chart_piechart);

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);

        // change the color of the center-hole
        mChart.setHoleColorTransparent(true);

        mChart.setHoleRadius(60f);
        mChart.setDescription("");
        mChart.setDrawCenterText(false);
        mChart.setDrawHoleEnabled(false);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

        mChart.setCenterText("休眠与唤醒");
        initData();

        mChart.animateXY(1000, 1000);
        // mChart.spin(2000, 0, 360);
        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);

        findViewById(R.id.sleep_detail_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showAlergDialog(SleepChart.this, "休眠与唤醒统计", mWakeupContent);
            }
        });
    }


    private void initData(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        final String parentDir = pref.getString("log_dir", "");
        final String parentDir = MainLogAnalyser.mParseDir;

        // add data
        new Thread(new Runnable(){

            @Override
            public void run() {
//                mSleepLogParser = new SleepDataLogParser(MainLogAnalyser.sParseDir);
                if(DEBUG){
                    Log.d(TAG,"sleep log parser: " + parentDir);
                }
                mSleepLogParser = new SleepDataLogParser(parentDir);
                mSleepLogParser.parse(Parser.DATA_NEWEST);
                mWakeupContent = mSleepLogParser.statistic();
                mHandler.sendMessage(mHandler.obtainMessage(0x1));
            }
            
        }).start();
    }
    
    public Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                setData();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        };
    };
    
    private void setData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        int index =0;
        ArrayList<WakeupInfo> infoList = mSleepLogParser.getWakeupList().getInterruptList();
        for(WakeupInfo info:infoList){
//            info.setAvgWakeupTime();
//            info.setName(mParser.getInterruptName(info.getIdentifier()));
//            float intervalTime = (float)getTotalTime()/info.getTotalWakeupCount();
            xVals.add(info.getIdentifier()+"("+ info.getTotalWakeupCount() +"次)");
            yVals1.add(new Entry((float)info.getTotalWakeupCount(), index++));
//            sb.append(info.getIdentifier()+":  ");
//            sb.append(info.getTotalWakeupCount()+",  ");
//            sb.append(df.format(intervalTime)+"s,  ");
//            sb.append(df.format(info.getAvgWakeupTime())+"s,  ");
//            sb.append("\n    "+info.getName()+" ");
//            sb.append("\n");
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "休眠与唤醒");
        dataSet.setSliceSpace(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}
