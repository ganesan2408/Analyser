/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.app.analyser;

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
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yhh.analyser.R;
import com.yhh.config.AppConfig;
import com.yhh.utils.ScreenShot;
import com.yhh.app.monitor.ExceptionMonitor;
import com.yhh.app.monitor.ExceptionStat;
import com.yhh.activity.AppMonitorActivity;
import com.yhh.chart.base.ChartBaseActivity;
import com.yhh.chart.custom.MyValueFormatter;
import com.yhh.chart.items.ChartItem;
import com.yhh.chart.items.LineChartItem;
import com.yhh.chart.items.PieChartItem;
import com.yhh.chart.items.StackedBarChartItem;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.utils.LogUtils;
import com.yhh.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppChartAnalyser extends ChartBaseActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG + "AppChartAnalyser";
    private boolean DEBUG = true;

    private MonitorDataProvider mMonitorDataProvider;
    private NoScrollListView mChartLv;
    //    private TextView mStatisticTv;
    private ChartDataAdapter mChartDataAdapter;
    private boolean mIsNoScroll = false;
    private String mMonitorPath;
    private ArrayList<ChartItem> mChartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.chart_listview_chart);

        mChartLv = (NoScrollListView) findViewById(R.id.app_monitor_listview);
//        mStatisticTv = (TextView) findViewById(R.id.app_monitor_statistic);

        mMonitorPath = getIntent().getStringExtra(AppMonitorActivity.MONITOR_PATH);
//        mStatisticTv.setVisibility(View.GONE);
        initData();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x1) {
                mChartLv.setAdapter(mChartDataAdapter);
                DialogUtils.closeLoading();
            }
        }

        ;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.chart_battery_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.chart_noscroll) {
            mChartLv.setNoScroll(!mIsNoScroll);
            mIsNoScroll = !mIsNoScroll;
        } else if (item.getItemId() == R.id.menu_shoot) {
            ScreenShot.shoot(this, mMonitorPath);
        } else if (item.getItemId() == R.id.hide_statistic) {
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


    private void initData() {
        // add data
        DialogUtils.showLoading(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                mMonitorDataProvider = new MonitorDataProvider(AppChartAnalyser.this);
                if (mMonitorPath == null || "".equals(mMonitorPath)) {
                    mMonitorPath = LogUtils.getDateNewestLog(AppConfig.MONITOR_DIR);
                }
                if (DEBUG) {
                    Log.d(TAG, "mMonitorPath=" + mMonitorPath);
                }
                mMonitorDataProvider.parse(AppConfig.MONITOR_DIR + "/" + mMonitorPath);

                updateAdapter();
                mHandler.sendMessage(mHandler.obtainMessage(0x1));
            }

        }).start();
    }

    private void updateStatistic() {
        Log.i("BUG_", "updateStatistic");
        String[] calcs;
        StringBuffer sb = new StringBuffer();
        String[] tmp = mMonitorPath.split("_");
        if (tmp.length == 3) {
            sb.append(tmp[2] + "  统计结果              (均值,  最小值,  最大值)\n");
        } else {
            sb.append(" 统计结果              (均值,  最小值,  最大值)\n");
        }
        String[] titles = mMonitorDataProvider.getTitles();
        int len = titles.length;
        for (int i = 0; i < len; i++) {
            calcs = mChartItems.get(i).getFormattedStatistic();
            sb.append(titles[i] + ":    ");
            sb.append("(" + calcs[0] + ",  ");
            sb.append(calcs[1] + ",  ");
            sb.append(calcs[2] + ")");
            sb.append("\n");
        }
        DialogUtils.showDialog(this, sb.toString());
    }

    private void updateAdapter() {
        mChartItems = new ArrayList<ChartItem>();

        Boolean[] isPercentData = {false, true, true, false, true,
                true, false, true, false, false, false, false, false, false};
        ArrayList<String> xVals = mMonitorDataProvider.getXValues();
        ArrayList<ArrayList<Entry>> monitorData = mMonitorDataProvider.getMonitorData();
        String[] monitorTitle = mMonitorDataProvider.getTitles();
        int len = monitorTitle.length;
        if (DEBUG) {
            Log.d(TAG, "monitor title length:" + len);
            Log.d(TAG, "monitor data length:" + monitorData.size());
        }
        for (int i = 0; i < len; i++) {
            if (ConstUtils.CPU_FREQ_TITLE.equals(monitorTitle[i])) {
                if (DEBUG) {
                    Log.d(TAG, "CPU_FREQ_TITLE:" + i);
                }
                mChartItems.add(new StackedBarChartItem(generateStackedBarData(xVals,
                        mMonitorDataProvider.getMulCpuData(),
                        monitorTitle[i]), getApplicationContext(), isPercentData[i]));
            } else {
                mChartItems.add(new LineChartItem(generateDataLine(xVals, monitorData.get(i),
                        monitorTitle[i]), getApplicationContext(), isPercentData[i]));
            }
        }

        ExceptionMonitor exceptionMonitor = new ExceptionMonitor(this);
        if (exceptionMonitor.isEnabled()) {
            String topTitle = "异常监控";
            HashMap<String, Float> topApps = ExceptionStat.getInstance().getTopCpuApps();
            mChartItems.add(new PieChartItem(generatePieData(topApps, topTitle), getApplicationContext(), false));
        }

        mChartDataAdapter = new ChartDataAdapter(getApplicationContext(), mChartItems);
    }


    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine(ArrayList<String> xVals, ArrayList<Entry> yVals, String title) {
        LineDataSet lineDataSet = new LineDataSet(yVals, title);
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

    private BarData generateStackedBarData(ArrayList<String> xVals, ArrayList<BarEntry> yVals, String title) {
        BarDataSet set1 = new BarDataSet(yVals, title);
        set1.setBarSpacePercent(35f);
        set1.setColors(new int[]{
                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140),
                Color.rgb(255, 208, 140), Color.rgb(140, 234, 255),
                Color.rgb(255, 140, 157), Color.rgb(193, 37, 82),
                Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
        });
        set1.setStackLabels(new String[]{
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

    private BarData generateBarData(HashMap<String, Float> topApps, String title) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        int count = ExceptionStat.getInstance().getTopCount();
        if (DEBUG) {
            Log.d(TAG, "Top app count:" + count);
        }

        int index = 0;
        for (java.util.Map.Entry<String, Float> entry : topApps.entrySet()) {
            xVals.add(entry.getKey());
            yVals.add(new BarEntry((float) entry.getValue() / count, index++));
        }

        BarDataSet set1 = new BarDataSet(yVals, title);
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
//             data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);

        return data;
    }


    private PieData generatePieData(HashMap<String, Float> topApps, String title) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        int count = ExceptionStat.getInstance().getTopCount();
        if (DEBUG) {
            Log.d(TAG, "Top app count:" + count);
        }
        int index = 0;
        for (java.util.Map.Entry<String, Float> entry : topApps.entrySet()) {

            if (entry.getValue() < 0.3 * count) {
                continue;
            }
            xVals.add(entry.getKey());
            yVals.add(new Entry((float) entry.getValue() / count, index++));
        }

        PieDataSet dataSet = new PieDataSet(yVals, title);
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
        data.setValueFormatter(new com.github.mikephil.charting.utils.PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        return data;
    }
}
