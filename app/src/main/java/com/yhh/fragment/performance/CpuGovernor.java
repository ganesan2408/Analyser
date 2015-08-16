/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.fragment.performance;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;
import com.yhh.widget.rangebar.RangeBar;

public class CpuGovernor extends Fragment {
    private static final String TAG = ConstUtils.DEBUG_TAG +"CpuGovernor";
    
    private Context mContext;
    
    private RangeBar mCpuBigFreqBar;
    private RangeBar mCpuSmallFreqBar;
    private RatingBar mCpuBigNumBar;
    private RatingBar mCpuSmallNumBar;
    
    private TextView mCpuBigfreqTv;
    private TextView mCpuSmallfreqTv;
    private TextView mCpuBigNumTv;
    private TextView mCpuSmallNumTv;
    
    private Button mReflashBtn;
    private boolean mReflashing;
    
    private int mBigCoreNum;
    private int mBigLowerFrq;
    private int mBigUpperFreq;
    
    private int mSmallCoreNum;
    private int mSmallLowerFrq;
    private int mSmallUpperFreq;
    
    private static String CPU_ONLINE = "/sys/devices/system/cpu/online";
    private static String CPU_LITTLE_FREQ_MAX = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    private static String CPU_LITTLE_FREQ_MIN = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    private static String CPU_BIG_FREQ_MAX =    "/sys/devices/system/cpu/cpu4/cpufreq/scaling_max_freq";
    private static String CPU_BIG_FREQ_MIN =    "/sys/devices/system/cpu/cpu4/cpufreq/scaling_min_freq";
    
    private MyPerf mMyPerf;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mMyPerf = MyPerf.getInstance();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_cpu, container, false);  
        mCpuBigNumBar = (RatingBar) view.findViewById(R.id.cpu_big_number_bar);
        mCpuBigNumTv = (TextView) view.findViewById(R.id.cpu_big_number_tv);
        mCpuBigFreqBar = (RangeBar) view.findViewById(R.id.cpu_big_freq_bar);
        mCpuBigfreqTv = (TextView) view.findViewById(R.id.cpu_big_freq_tv);
        
        mCpuSmallNumBar = (RatingBar) view.findViewById(R.id.cpu_small_number_bar);
        mCpuSmallNumTv = (TextView) view.findViewById(R.id.cpu_small_number_tv);
        mCpuSmallFreqBar = (RangeBar) view.findViewById(R.id.cpu_small_freq_bar);
        mCpuSmallfreqTv = (TextView) view.findViewById(R.id.cpu_small_freq_tv);
        
        mReflashBtn = (Button) view.findViewById(R.id.reflash_btn);
        
        initListener();
        getData();
        updateUI();
        return view;
    }
    
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                getData();
                updateUI();
                Toast.makeText(mContext, R.string.reflash_comlepte, Toast.LENGTH_SHORT).show();
                Log.i(TAG,"reflash compeletely.");
                mReflashing = false;
            }
        };
     };
    
    private void initListener(){
        mCpuBigNumBar.setMax(MyPerf.CPU_BIG_NUM);
        mCpuBigFreqBar.setTickCount(MyPerf.CPU_BIG_FREQ_NUM);
        
        mCpuSmallNumBar.setMax(MyPerf.CPU_LITTER_NUM);
        mCpuSmallFreqBar.setTickCount(MyPerf.CPU_LITTER_FREQ_NUM);
        
        mCpuBigFreqBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mBigLowerFrq = leftThumbIndex;
                mBigUpperFreq = rightThumbIndex;
                mCpuBigfreqTv.setText("[  "+ MyPerf.CPU_BIG_FREQ[leftThumbIndex]/1000 
                        +",  " + MyPerf.CPU_BIG_FREQ[rightThumbIndex]/1000 +"  ]");
            }
        });
        
        mCpuBigNumBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                    boolean fromUser) {
                mBigCoreNum = (int)rating;
                mCpuBigNumTv.setText(""+mBigCoreNum);
            }
        });
        
        mCpuSmallFreqBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                mSmallLowerFrq = leftThumbIndex;
                mSmallUpperFreq = rightThumbIndex;
                mCpuSmallfreqTv.setText("[  "+ MyPerf.CPU_LITTER_FREQ[leftThumbIndex]/1000 
                        +",  " + MyPerf.CPU_LITTER_FREQ[rightThumbIndex]/1000+"  ]");
            }
        });
        
        mCpuSmallNumBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){
            
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                    boolean fromUser) {
                mSmallCoreNum = (int)rating;
                mCpuSmallNumTv.setText(""+mSmallCoreNum);
            }
        });
        
        mReflashBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(mReflashing){
                    return;
                }
                mReflashing = true;
                updateData();
                Toast.makeText(mContext, R.string.reflash_working, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            
                        }
                        mHandler.sendMessage(mHandler.obtainMessage(1));
                    }
                    
                }).start();
            }
        });
    }
    
    private void updateUI(){
        mCpuBigNumBar.setRating(mBigCoreNum);
        mCpuSmallNumBar.setRating(mSmallCoreNum);
        
        mCpuSmallFreqBar.setThumbIndices(mSmallLowerFrq, mSmallUpperFreq);
        mCpuBigFreqBar.setThumbIndices(mBigLowerFrq, mBigUpperFreq);
    }

    /**
     *  updata cpu data into kernel
     */
    private void updateData(){
        int[] sources = new int[3];
        sources[0] = mMyPerf.getCpuOn(mBigCoreNum, mSmallCoreNum);
        sources[1] = mMyPerf.getBigCpuFreq(mBigUpperFreq, mBigLowerFrq);
        sources[2] = mMyPerf.getLittleCpuFreq(mSmallUpperFreq, mSmallLowerFrq);
        mMyPerf.execute(sources);
    }
    
    
    /**
     * get cpu info from kernel
     */
    private void getData(){
        int[] cores = MyPerf.getCpuOnline(CPU_ONLINE);
        mSmallCoreNum =cores[0];
        mBigCoreNum =cores[1];
        
        mSmallLowerFrq = MyPerf.getLitterCpuFreqLevel(CPU_LITTLE_FREQ_MIN);
        mSmallUpperFreq =MyPerf.getLitterCpuFreqLevel(CPU_LITTLE_FREQ_MAX);
        
        mBigLowerFrq = MyPerf.getBigCpuFreqLevel(CPU_BIG_FREQ_MIN);
        mBigUpperFreq =MyPerf.getBigCpuFreqLevel(CPU_BIG_FREQ_MAX);
    }
    
}
