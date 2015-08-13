/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.aafragment.performance;

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
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;
import com.yhh.widget.rangebar.RangeBar;

public class GpuGovernor extends Fragment {
    private static final String TAG = ConstUtils.DEBUG_TAG +"GpuGovernor";
    
    private Context mContext;
    
    private RangeBar mGpuFreqBar;
    private TextView mGpufreqTv;
    
    private int mLowerFrq;
    private int mUpperFreq;
    
    private Button mReflashBtn;
    private boolean mReflashing;
    
    private static String GPU_FREQ_MAX = "/sys/class/kgsl/kgsl-3d0/max_pwrlevel";
    private static String GPU_FREQ_MIN = "/sys/class/kgsl/kgsl-3d0/min_pwrlevel";
    
    private MyPerf mMyPerf;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mMyPerf = MyPerf.getInstance();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_gpu, container, false);  
        mGpuFreqBar = (RangeBar) view.findViewById(R.id.gpu_freq_bar);
        mGpufreqTv = (TextView) view.findViewById(R.id.gpu_freq_tv);
        
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
                mReflashing = false;
            }
        };
     };
    
    private void initListener(){
        mGpuFreqBar.setTickCount(MyPerf.GPU_FREQ_NUM);
        
        mGpuFreqBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                Log.i(TAG,"leftThumbIndex="+leftThumbIndex+", rightThumbIndex="+rightThumbIndex);
                mLowerFrq = MyPerf.GPU_FREQ_NUM - leftThumbIndex -1;
                mUpperFreq = MyPerf.GPU_FREQ_NUM - rightThumbIndex - 1;
                mGpufreqTv.setText("[  "+ MyPerf.GPU_FREQ[mLowerFrq] 
                        +",  " + MyPerf.GPU_FREQ[mUpperFreq] +"  ]");
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
        mGpuFreqBar.setThumbIndices(MyPerf.GPU_FREQ_NUM - 1- mLowerFrq, MyPerf.GPU_FREQ_NUM -1 -mUpperFreq);
    }
    
    /**
     * get cpu info from kernel
     */
    private void getData(){
        mLowerFrq = MyPerf.getGpuFreqLevel(GPU_FREQ_MIN);
        mUpperFreq = MyPerf.getGpuFreqLevel(GPU_FREQ_MAX);
        Log.i(TAG,"get gpu frq ["+mUpperFreq+","+mLowerFrq+"]");
    }
    
    /**
     *  updata gpu data into kernel
     */
    private void updateData(){
        Log.i(TAG,"set gpu frq ["+mUpperFreq+","+mLowerFrq+"]");
        int[] sources = new int[1];
        sources[0] = mMyPerf.getGpuFreq(mUpperFreq, mLowerFrq);
        mMyPerf.execute(sources);
    }
}
