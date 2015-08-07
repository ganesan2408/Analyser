/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.log.analyser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.yhh.analyser.R;
import com.yhh.analyser.ScreenShot;
import com.yhh.chart.base.ChartBaseActivity;
import com.yhh.chart.base.ChartTool;
import com.yhh.chart.custom.MyValueFormatter;
import com.yhh.chart.items.BarChartItem;
import com.yhh.chart.items.ChartItem;
import com.yhh.chart.items.ExtendBarChartItem;
import com.yhh.chart.items.LineChartItem;
import com.yhh.chart.items.StackedBarChartItem;
import com.yhh.chart.other.SleepChart;
import com.yhh.log.parser.BatteryLogParser;
import com.yhh.log.parser.LogcatParser;
import com.yhh.log.parser.PmLogParser;
import com.yhh.log.parser.SleepLogParser;
import com.yhh.log.provider.LogDataProvider;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * draw log chart
 * 
 * @author yuanhh1
 *
 */
public class LogChart extends ChartBaseActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG + "LogChartAnalyser";
    private boolean DEBUG = true;
    
    private LogDataProvider mLogProvider;
    private NoScrollListView mChartLv;
    private TextView mforeAppTv;
    private ChartDataAdapter mChartDataAdapter;
    private boolean mIsNoScroll = false;
    private String mTargetLog;
    private ArrayList<ChartItem> mChartItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.log_analyser_list);
        Bundle b = this.getIntent().getExtras();
        
        mforeAppTv = (TextView) findViewById(R.id.app_fore_statistic);
        mforeAppTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        mTargetLog = b.getString(MainLogAnalyser.LOG_PATH);
        mChartLv = (NoScrollListView) findViewById(R.id.log_analyser_listview);
        
        mforeAppTv.setVisibility(View.GONE);
        initData();
    }
    
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what ==0x1){
                mChartLv.setAdapter(mChartDataAdapter);
                DialogUtils.closeLoading();
                if(mTargetLog.startsWith(LogcatParser.newFile)){
                    mforeAppTv.setText(mLogProvider.foreAppsToString());
                    mforeAppTv.setVisibility(View.VISIBLE);
                }
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
            ScreenShot.shoot(this, mTargetLog);
        }else if(item.getItemId() ==R.id.hide_statistic){
            if(mTargetLog.endsWith(SleepLogParser.newFile)){
                Intent intent = new Intent(this, SleepChart.class);
                intent.putExtra("fileName", mTargetLog);
                startActivity(intent);
            }else{
                Toast.makeText(this, "此功能暂未开放", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    
    private void initData(){
        // add data
        new Thread(new Runnable(){

            @Override
            public void run() {
                mLogProvider = new LogDataProvider(LogChart.this);
                mLogProvider.generateData(mTargetLog);
                updateAdapter();
                mHandler.sendMessage(mHandler.obtainMessage(0x1));
            }
            
        }).start();
    }
    
    private void updateAdapter(){
        mChartItems = new ArrayList<ChartItem>();
        
        String mTitleDay = mLogProvider.getTitleDay();
        ArrayList<String> xVals = ChartTool.getInstance().getXAxisValues();
        
        if(mTargetLog.endsWith(BatteryLogParser.newFile)){
            ArrayList<Entry> batteryLevel = mLogProvider.getLevelEntryList();
            ArrayList<Entry> batteryTemp = mLogProvider.getTemperatureEntryList();
            ArrayList<Entry> batteryVol = mLogProvider.getVoltageEntryList();
            ArrayList<BarEntry> batteryStatus = mLogProvider.getStatusEntryList();
            ArrayList<BarEntry> batterySHealth = mLogProvider.getHealthEntryList();
            
            String statusDescription = "2->Charging;  3->DisCharging;  5->Full";
            String healthDescription = "2->Good;  3->OverHeat;  4->Dead;  5->OverVoltage";

            mChartItems.add(new LineChartItem(generateLineData(xVals, batteryLevel,
                    "电池电量(" + mTitleDay+ ")"), getApplicationContext(), true));
            
            mChartItems.add(new LineChartItem(generateLineData(xVals, batteryTemp,
                    "电池温度(" + mTitleDay + ")"), getApplicationContext(), false));

            mChartItems.add(new LineChartItem(generateLineData(xVals, batteryVol,
                    "电池电压(" + mTitleDay + ")"), getApplicationContext(), false));

            mChartItems.add(new ExtendBarChartItem(generateBarData(xVals, batteryStatus,
                    "充电状态(" + mTitleDay + ")"), getApplicationContext(), false, statusDescription));

            mChartItems.add(new ExtendBarChartItem(generateBarData(xVals, batterySHealth,
                    "健康状态(" + mTitleDay + ")"), getApplicationContext(), false, healthDescription));

        }else if(mTargetLog.endsWith(PmLogParser.newFile)){
            ArrayList<Entry> currentEntry = mLogProvider.getCurrentEntryList();
            ArrayList<Entry> brightnessEntry = mLogProvider.getBrightnessEntryList();
            ArrayList<Entry> gpuClkEntry = mLogProvider.getGpuClkEntryList();
            ArrayList<BarEntry> cpuClkEntry = mLogProvider.getCpuClkEntryList();
            
            mChartItems.add(new LineChartItem(generateCurrentLineData(xVals,currentEntry,
                    "电流(" + mTitleDay+ ")"), getApplicationContext(), false));
            
            mChartItems.add(new LineChartItem(generateLineData(xVals,brightnessEntry,
                    "亮度(" + mTitleDay+ ")"), getApplicationContext(), false));
            
            mChartItems.add(new LineChartItem(generateLineData(xVals,gpuClkEntry,
                    "GPU频率(" + mTitleDay+ ")"), getApplicationContext(), false));
            
            mChartItems.add(new StackedBarChartItem(generateStackedBarData(xVals,cpuClkEntry,
                    "CPU频率(" + mTitleDay+ ")"), getApplicationContext(), false));
            
        }else if(mTargetLog.endsWith(SleepLogParser.newFile)){
            ArrayList<ArrayList<String>> hhmmssAxisGroup = ChartTool.getInstance().gethhmmssAxisGroup();
            ArrayList<ArrayList<BarEntry>> wakeupEntry = mLogProvider.getWakeupEntry();
            int len = 6;
            if(DEBUG){
                Log.d(TAG,"wakeupEntry size=" + wakeupEntry.size());
            }
            for(int i=0;i<len;i++){
                ArrayList<BarEntry> curEntry = wakeupEntry.get(i);
                if(curEntry !=null && curEntry.size()>0){
                    mChartItems.add(new BarChartItem(generateBarData(hhmmssAxisGroup.get(i),
                            curEntry,"休眠与唤醒(" + mTitleDay+ ") Part"+(i+1)),
                            getApplicationContext(), false));
                }
                if(DEBUG){
                    Log.d(TAG,"wakeupEntry.get(i) size="+wakeupEntry.get(i).size());
                }
            }
        }else{
            Log.e(TAG,"updateAdapter NULL.");
        }
        mChartDataAdapter = new ChartDataAdapter(getApplicationContext(), mChartItems);
    }
    
    private BarData generateBarData(ArrayList<String> xVals, ArrayList<BarEntry> yVals, String title) {

        BarDataSet set1 = new BarDataSet(yVals, title);
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
//        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);
        data.setDrawValues(false);
        data.setGroupSpace(1);
        return data;
    }
    
    private BarData generateStackedBarData(ArrayList<String> xVals, ArrayList<BarEntry> yVals, String title){
        BarDataSet set1 = new BarDataSet(yVals, title);
        set1.setBarSpacePercent(35f);
        set1.setColors(new int[]{
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), 
                Color.rgb(255, 208, 140), Color.rgb(140, 234, 255), 
                Color.rgb(255, 140, 157), Color.rgb(193, 37, 82), 
                Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
        });
        set1.setStackLabels(new String[] {
                "CPU0", "CPU1",
                "CPU2", "CPU3",
                "CPU4", "CPU5",
                "CPU6", "CPU7"
        });
        
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueFormatter(new MyValueFormatter());
       
        return data;
    }
    
     private LineData generateLineData(ArrayList<String> xVals, ArrayList<Entry> yVals, String title) {
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
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setDrawCircles(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(lineDataSet); // add the datasets

        LineData data = new LineData(xVals, dataSets);
        return data;
    }
     
     private LineData generateCurrentLineData(ArrayList<String> xVals, ArrayList<Entry> yVals, String title) {
         LineDataSet lineDataSet = new LineDataSet(yVals,title);
         lineDataSet.setColor(Color.RED);
         lineDataSet.setCircleColor(Color.RED);
         lineDataSet.setLineWidth(1f);
         lineDataSet.setCircleSize(2f);
         lineDataSet.setDrawCircleHole(false);
         lineDataSet.setValueTextSize(10f);
         lineDataSet.setFillAlpha(100);
         lineDataSet.setFillColor(Color.RED);
         
         lineDataSet.setDrawCubic(false);
         lineDataSet.setDrawValues(false);
         lineDataSet.setDrawFilled(true);
         lineDataSet.setDrawCircles(false);

         ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
         dataSets.add(lineDataSet); // add the datasets

         LineData data = new LineData(xVals, dataSets);
         return data;
     }
     
    
     
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
}
