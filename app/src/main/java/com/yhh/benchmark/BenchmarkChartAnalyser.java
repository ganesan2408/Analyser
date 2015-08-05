/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.benchmark;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.yhh.analyser.R;
import com.yhh.analyser.ScreenShot;
import com.yhh.app.analyser.MonitorDataProvider;
import com.yhh.chart.base.ChartBaseActivity;
import com.yhh.chart.items.ChartItem;
import com.yhh.chart.items.LineChartItem;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.utils.LogUtils;
import com.yhh.widget.NoScrollListView;

public class BenchmarkChartAnalyser extends ChartBaseActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG + "BenchmarkChartAnalyser";
    private boolean DEBUG = true;
    
    private MonitorDataProvider mMonitorDataProvider;
    private NoScrollListView mChartLv;
    private ChartDataAdapter mChartDataAdapter;
    private boolean mIsNoScroll = false;
    private String mBenchmarkPath;
    private ArrayList<ChartItem> mChartItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chart_listview_chart);
        
        mChartLv = (NoScrollListView) findViewById(R.id.app_monitor_listview);
        
        mBenchmarkPath  = getIntent().getStringExtra(BenchmarkRunner.MONITOR_PATH_TAG);
        initData();
    }
    
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 0x1){
                mChartLv.setAdapter(mChartDataAdapter);
                DialogUtils.closeLoading();
            }
        };
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.chart_battery_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==R.id.chart_noscroll){
            mChartLv.setNoScroll(!mIsNoScroll);
            mIsNoScroll = !mIsNoScroll; 
        }else if(item.getItemId() ==R.id.menu_shoot){
            ScreenShot.shoot(this, mBenchmarkPath);
        }else if(item.getItemId() ==R.id.hide_statistic){
            updateStatistic();
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {
        
        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }
        
        @Override
        public int getItemViewType(int position) {           
            // return the views type
            return getItem(position).getItemType();
        }
        
        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }
    
    
    private void initData(){
        // add data
        DialogUtils.showLoading(this);
        new Thread(new Runnable(){

            @Override
            public void run() {
                mMonitorDataProvider = new MonitorDataProvider(BenchmarkChartAnalyser.this);
                if(mBenchmarkPath ==null || "".equals(mBenchmarkPath)){
                    mBenchmarkPath = LogUtils.getDateNewestLog(BenchmarkRunner.MONITOR_PARENT_PATH);
                }
                
                if(DEBUG){
                    Log.d(TAG,"mMonitorPath="+mBenchmarkPath);
                }
              //需要修改的
                mMonitorDataProvider.parse("/sdcard/systemAnalyzer/"+mBenchmarkPath);
                
                updateAdapter();
                mHandler.sendMessage(mHandler.obtainMessage(0x1));
            }
            
        }).start();
    }
    
    private void updateStatistic(){
        Log.i("BUG_","updateStatistic");
        String[] calcs;
        StringBuffer sb = new StringBuffer();
        sb.append(" 统计结果              (均值,  最小值,  最大值)\n");
        String[] titles = mMonitorDataProvider.getTitles();
        int len = titles.length;
        for(int i=0;i<2;i++){
            calcs =  mChartItems.get(i).getFormattedStatistic();
            sb.append(titles[i]+":    ");
            sb.append("("+calcs[0]+",  ");
            sb.append(calcs[1]+",  ");
            sb.append(calcs[2]+")");
            sb.append("\n");
        }
        for(int i=2;i<len;i++){
            calcs =  mChartItems.get(i).getFormattedStatistic(0);
            sb.append(titles[i]+":    ");
            sb.append("("+calcs[0]+",  ");
            sb.append(calcs[1]+",  ");
            sb.append(calcs[2]+")");
            sb.append("\n");
        }
        DialogUtils.showDialog(this, sb.toString());
    }
    
    private void updateAdapter(){
        mChartItems = new ArrayList<ChartItem>();
        
        ArrayList<String> xVals = mMonitorDataProvider.getXValues();
        ArrayList<ArrayList<Entry>> monitorData = mMonitorDataProvider.getMonitorData();
        String[] monitorTitle = mMonitorDataProvider.getTitles();
        int len = monitorTitle.length;
        if(DEBUG){
            Log.d(TAG,"monitor title length:"+ len);
            Log.d(TAG,"monitor data length:"+ monitorData.size());
        }
        for(int i=0;i<len;i++){
            mChartItems.add(new LineChartItem(generateDataLine(xVals, monitorData.get(i),
                    monitorTitle[i]), getApplicationContext(), false));
        }
        
        mChartDataAdapter = new ChartDataAdapter(getApplicationContext(), mChartItems);
    }
    
    
    /**
     * generates a random ChartData object with just one DataSet
     * 
     * @return
     */
     private LineData generateDataLine(ArrayList<String> xVals, ArrayList<Entry> yVals, String title) {
        LineDataSet lineDataSet = new LineDataSet(yVals,title);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleSize(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setFillAlpha(100);
        lineDataSet.setFillColor(Color.RED);
        
        lineDataSet.setDrawCubic(false);
        lineDataSet.setDrawValues(true);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setDrawCircles(false);
        // lineDataSet.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        return data;
    }
}
