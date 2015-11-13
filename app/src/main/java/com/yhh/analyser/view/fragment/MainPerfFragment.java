/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.LogUtils;

public class MainPerfFragment extends Fragment implements
        OnCheckedChangeListener {
    
    private static final String TAG = LogUtils.DEBUG_TAG+ "MainPerf";
    
    private RadioGroup main_tab_RadioGroup;
    
    private Fragment mCpuFragment;
    private Fragment mGpuFragment;
    private Fragment mThermalFragment;
    private Fragment mIOFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_performance, null);
        main_tab_RadioGroup = (RadioGroup) v.findViewById(R.id.main_perf_RadioGroup);
        main_tab_RadioGroup.setOnCheckedChangeListener(this);
        return v;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        setDefaultFragment();
    }
    
    private void setDefaultFragment(){
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.perf_Pager,   
                getFragmentByCheckedId(main_tab_RadioGroup.getCheckedRadioButtonId()));
        transaction.commit();
    }
    
    private Fragment getFragmentByCheckedId(int CheckedId){
        Fragment fragment = null;
        switch (CheckedId) {
            case R.id.main_perf_cpu:
                mCpuFragment = new PerfCpuFragment();
                fragment = mCpuFragment;
                Log.i(TAG,"===CPU");
                break;
                
            case R.id.main_perf_gpu:
                mGpuFragment = new PerfGpuFragment();
                fragment = mGpuFragment;
                Log.i(TAG,"===GPU");
                break;
                
            case R.id.main_perf_io:
                mIOFragment = new PerfIoFragment();
                fragment = mIOFragment;
                Log.i(TAG,"===IO");
                break;

            case R.id.main_perf_thermal:
                mThermalFragment = new PerfThermalFragment();
                fragment = mThermalFragment;
                Log.i(TAG,"===THERMAL");
                break;
                
            default:
                Log.i(TAG,"===error: "+CheckedId);
                break;
        }
        return fragment;
    }
    
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int CheckedId) {
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();
        Log.i(TAG,"onCheckedChanged");
        switch (CheckedId) {
            case R.id.main_perf_cpu:
                if(mCpuFragment == null){
                    mCpuFragment = new PerfCpuFragment();
                }
                transaction.replace(R.id.perf_Pager,  mCpuFragment);
                break;
                
            case R.id.main_perf_gpu:
                if(mGpuFragment == null){
                    mGpuFragment = new PerfGpuFragment();
                }
                transaction.replace(R.id.perf_Pager,  mGpuFragment);
                break;
                
            case R.id.main_perf_io:
                if(mIOFragment == null){
                    mIOFragment = new PerfIoFragment();
                }
                transaction.replace(R.id.perf_Pager,  mIOFragment);
                break;

            case R.id.main_perf_thermal:
                    if(mThermalFragment == null){
                        mThermalFragment = new PerfThermalFragment();
                    }
                    transaction.replace(R.id.perf_Pager,  mThermalFragment);
                    break;
            }
        transaction.commit();
    }
    
}