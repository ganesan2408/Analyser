/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui.settings;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.bean.PerfBean;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.widget.SwitchButton;
import com.yhh.analyser.widget.rangebar.RangeBar;

public class BenchmarkSettingsActivity extends Activity {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "BenchmarkSettingsActivity";
    private boolean DEBUG = true;

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    
    private SwitchButton mThermalControlBtn;
    private EditText mStartTmpEt;
    private SwitchButton mLoopConfigBtn;
    
    private RangeBar mCpuBigNumBar;
    private RangeBar mCpuSmallNumBar;
    private RangeBar mCpuBigFreqBar;
    private RangeBar mCpuSmallFreqBar;
    private RangeBar mGpuFreqBar;
    
    private TextView mCpuBigNumTv;
    private TextView mCpuSmallNumTv;
    private TextView mCpuBigfreqTv;
    private TextView mCpuSmallfreqTv;
    private TextView mGpufreqTv;

    
    public static String THERMAL_CONTROL_KEY = "tc";
    public static String START_TEMP_KEY = "st";
    public static String LOOP_CONFIG_KEY = "lc";
    
    public static String CPU_BIG_NUM_UPPER_KEY = "cbnu";
    public static String CPU_SMALL_NUM_UPPER_KEY = "csnu";
    public static String CPU_BIG_FRQ_UPPER_KEY = "cbfu";
    public static String CPU_SMALL_FRQ_UPPER_KEY = "csfu";
    public static String GPU_FRQ_UPPER_KEY = "gfu";
    
    public static String CPU_BIG_NUM_LOWER_KEY = "cbnb";
    public static String CPU_SMALL_NUM_LOWER_KEY = "csnb";
    public static String CPU_BIG_FRQ_LOWER_KEY = "cbfb";
    public static String CPU_SMALL_FRQ_LOWER_KEY = "csfb";
    public static String GPU_FRQ_LOWER_KEY = "gfb";
    
    
    boolean isThermalControl;
    String startTmp;
    boolean isLoogConfig;
    int cpuBigNumUpper;
    int cpuBigNumLower;
    int cpuSmallNumUpper;
    int cpuSmallNumLower;
    int cpuBigFreqUpper;
    int cpuBigFreqLower;
    int cpuSmallFreqUpper;
    int cpuSmallFreqLower;
    int gpuFreqUpper;
    int gpuFreqLower;
    
    
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.benchmark_settings);
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mPref.edit();
        
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
        
        initUI();
        readRefs();
    }
    
    public void initUI(){
        mThermalControlBtn = (SwitchButton) findViewById(R.id.thermal_control_btn);
        mStartTmpEt =  (EditText) findViewById(R.id.start_temp_et);
        mLoopConfigBtn = (SwitchButton) findViewById(R.id.loop_config_btn);
        
        mCpuBigNumBar = (RangeBar) findViewById(R.id.cpu_big_num_bar);
        mCpuSmallNumBar = (RangeBar) findViewById(R.id.cpu_small_num_bar);
        mCpuBigFreqBar = (RangeBar) findViewById(R.id.cpu_big_freq_bar);
        mCpuSmallFreqBar = (RangeBar) findViewById(R.id.cpu_small_freq_bar);
        mGpuFreqBar = (RangeBar) findViewById(R.id.gpu_freq_bar);

        mCpuBigNumTv = (TextView) findViewById(R.id.cpu_big_num_tv);
        mCpuSmallNumTv = (TextView) findViewById(R.id.cpu_small_num_tv);
        mCpuBigfreqTv = (TextView) findViewById(R.id.cpu_big_freq_tv);
        mCpuSmallfreqTv =  (TextView) findViewById(R.id.cpu_small_freq_tv);
        mGpufreqTv = (TextView) findViewById(R.id.gpu_freq_tv);
        
        
        mThermalControlBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mEditor.putBoolean(THERMAL_CONTROL_KEY, !isChecked);
                mEditor.commit();
                RootUtils.getInstance().startThermalEngine(!isChecked);
            }
        });
//      mLoopConfigBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//          @Override
//          public void onCheckedChanged(CompoundButton buttonView,
//                  boolean isChecked) {
//              mEditor.putBoolean(LOOP_CONFIG_KEY, !isChecked);
//              mEditor.commit();
//          }
//      });
        //设置
        mCpuBigNumBar.setTickCount(PerfBean.CPU_BIG_NUM+1);
        mCpuSmallNumBar.setTickCount(PerfBean.CPU_LITTER_NUM+1);
        mCpuBigFreqBar.setTickCount(PerfBean.CPU_BIG_FREQ_NUM);
        mCpuSmallFreqBar.setTickCount(PerfBean.CPU_LITTER_FREQ_NUM);
        mGpuFreqBar.setTickCount(PerfBean.GPU_FREQ_NUM);
        
        mCpuBigNumBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mCpuBigNumTv.setText("[  "+leftThumbIndex +",  " + rightThumbIndex+"  ]");
            }
        });
        mCpuSmallNumBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mCpuSmallNumTv.setText("[  "+leftThumbIndex +",  " + rightThumbIndex+"  ]");
            }
        });
        
        mCpuBigFreqBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mCpuBigfreqTv.setText("[  "+ PerfBean.CPU_BIG_FREQ[leftThumbIndex]/1000
                        +",  " + PerfBean.CPU_BIG_FREQ[rightThumbIndex]/1000 +"  ]");
            }
        });
        
        mCpuSmallFreqBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mCpuSmallfreqTv.setText("[  "+ PerfBean.CPU_LITTER_FREQ[leftThumbIndex]/1000
                        +",  " + PerfBean.CPU_LITTER_FREQ[rightThumbIndex]/1000+"  ]");
            }
        });
        
        mGpuFreqBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mGpufreqTv.setText("[  "+ PerfBean.GPU_FREQ[PerfBean.GPU_FREQ_NUM - leftThumbIndex -1]
                        +",  " + PerfBean.GPU_FREQ[PerfBean.GPU_FREQ_NUM - rightThumbIndex - 1] +"  ]");
            }
        });
        
    }
    
    public void readRefs(){
        isThermalControl = mPref.getBoolean(THERMAL_CONTROL_KEY, true);
        startTmp = mPref.getString(START_TEMP_KEY, "");
        isLoogConfig = mPref.getBoolean(LOOP_CONFIG_KEY, false);
        cpuBigNumUpper = mPref.getInt(CPU_BIG_NUM_UPPER_KEY, 2);
        cpuBigNumLower = mPref.getInt(CPU_BIG_NUM_LOWER_KEY, 0);
        cpuSmallNumUpper = mPref.getInt(CPU_SMALL_NUM_UPPER_KEY, 4);
        cpuSmallNumLower = mPref.getInt(CPU_SMALL_NUM_LOWER_KEY, 0);
        cpuBigFreqUpper = mPref.getInt(CPU_BIG_FRQ_UPPER_KEY, 2);
        cpuBigFreqLower = mPref.getInt(CPU_BIG_FRQ_LOWER_KEY, 0);
        cpuSmallFreqUpper = mPref.getInt(CPU_SMALL_FRQ_UPPER_KEY, 4);
        cpuSmallFreqLower = mPref.getInt(CPU_SMALL_FRQ_LOWER_KEY, 0);
        gpuFreqUpper = mPref.getInt(GPU_FRQ_UPPER_KEY, 4);
        gpuFreqLower = mPref.getInt(GPU_FRQ_LOWER_KEY, 0);
        
        mThermalControlBtn.setChecked(!isThermalControl);
        mStartTmpEt.setText(startTmp);
        mLoopConfigBtn.setChecked(!isLoogConfig);
        
        mCpuBigNumBar.setThumbIndices(cpuBigNumLower, cpuBigNumUpper);
        mCpuSmallNumBar.setThumbIndices(cpuSmallNumLower, cpuSmallNumUpper);
        mCpuBigFreqBar.setThumbIndices(cpuBigFreqLower, cpuBigFreqUpper);
        mCpuSmallFreqBar.setThumbIndices(cpuSmallFreqLower, cpuSmallFreqUpper);
        mGpuFreqBar.setThumbIndices(gpuFreqLower, gpuFreqUpper);
        
        mCpuBigNumTv.setText("[  "+cpuBigNumLower +",  " + cpuBigNumUpper+"  ]");
        mCpuSmallNumTv.setText("[  "+cpuSmallNumLower +",  " + cpuSmallNumUpper+"  ]");
        mCpuBigfreqTv.setText("[  "+ PerfBean.CPU_BIG_FREQ[cpuBigFreqLower]/1000
                +",  " + PerfBean.CPU_BIG_FREQ[cpuBigFreqUpper]/1000 +"  ]");
        mCpuSmallfreqTv.setText("[  "+ PerfBean.CPU_LITTER_FREQ[cpuSmallFreqLower]/1000
                +",  " + PerfBean.CPU_LITTER_FREQ[cpuSmallFreqUpper]/1000+"  ]");
        mGpufreqTv.setText("[  "+ PerfBean.GPU_FREQ[PerfBean.GPU_FREQ_NUM - gpuFreqLower -1]
                +",  " + PerfBean.GPU_FREQ[PerfBean.GPU_FREQ_NUM - gpuFreqUpper - 1] +"  ]");
    }
    
    private void writeRefs(){
        mEditor.putBoolean(THERMAL_CONTROL_KEY, !mThermalControlBtn.isChecked());
        mEditor.putString(START_TEMP_KEY, mStartTmpEt.getText().toString());
        mEditor.putBoolean(LOOP_CONFIG_KEY, !mLoopConfigBtn.isChecked());
        
        mEditor.putInt(CPU_BIG_NUM_UPPER_KEY, mCpuBigNumBar.getRightIndex());
        mEditor.putInt(CPU_BIG_NUM_LOWER_KEY, mCpuBigNumBar.getLeftIndex());
        mEditor.putInt(CPU_SMALL_NUM_UPPER_KEY, mCpuSmallNumBar.getRightIndex());
        mEditor.putInt(CPU_SMALL_NUM_LOWER_KEY, mCpuSmallNumBar.getLeftIndex());
        mEditor.putInt(CPU_BIG_FRQ_UPPER_KEY, mCpuBigFreqBar.getRightIndex());
        mEditor.putInt(CPU_BIG_FRQ_LOWER_KEY, mCpuBigFreqBar.getLeftIndex());
        mEditor.putInt(CPU_SMALL_FRQ_UPPER_KEY, mCpuSmallFreqBar.getRightIndex());
        mEditor.putInt(CPU_SMALL_FRQ_LOWER_KEY, mCpuSmallFreqBar.getLeftIndex());
        mEditor.putInt(GPU_FRQ_UPPER_KEY, mGpuFreqBar.getRightIndex());
        mEditor.putInt(GPU_FRQ_LOWER_KEY, mGpuFreqBar.getLeftIndex());
        mEditor.commit();
    }
    
    @Override
    protected void onPause() {
        Log.i(TAG,"Benchmark settings onPause");
        writeRefs();
        super.onPause();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
