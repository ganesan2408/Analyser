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

public class MainPowerFragment extends Fragment implements
        OnCheckedChangeListener {
    
    private static final String TAG = LogUtils.DEBUG_TAG+ "MainAnalysis";
    
    private RadioGroup mAnalysisRGroup;
    
    private Fragment mPowerBrightFragment;
    private Fragment mPowerWakeupFragment;
    private Fragment mPowerLockFragment;
    private Fragment mPowerElseFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_analysis, null);
        mAnalysisRGroup = (RadioGroup) v.findViewById(R.id.group_analysis);
        mAnalysisRGroup.setOnCheckedChangeListener(this);
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
        transaction.replace(R.id.frame_pager,
                getFragmentByCheckedId(mAnalysisRGroup.getCheckedRadioButtonId()));
        transaction.commit();
    }
    
    private Fragment getFragmentByCheckedId(int CheckedId){
        Fragment fragment = null;
        switch (CheckedId) {
            case R.id.rb_power_lcd:
                mPowerBrightFragment = new PowerBrightFragment();
                fragment = mPowerBrightFragment;
                break;
                
            case R.id.rb_power_wakeup:
                mPowerWakeupFragment = new PowerWakeupFragment();
                fragment = mPowerWakeupFragment;
                break;

            case R.id.rb_power_lock:
                mPowerLockFragment = new PowerLockFragment();
                fragment = mPowerLockFragment;
                break;

            case R.id.rb_power_else:
                mPowerElseFragment = new PowerElseFragment();
                fragment = mPowerElseFragment;
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
        switch (CheckedId) {
            case R.id.rb_power_lcd:
                if(mPowerBrightFragment == null){
                    mPowerBrightFragment = new PowerBrightFragment();
                }
                transaction.replace(R.id.frame_pager, mPowerBrightFragment);
                break;
                
            case R.id.rb_power_wakeup:
                if(mPowerWakeupFragment == null){
                    mPowerWakeupFragment = new PowerWakeupFragment();
                }
                transaction.replace(R.id.frame_pager, mPowerWakeupFragment);
                break;

            case R.id.rb_power_lock:
                if(mPowerLockFragment == null){
                    mPowerLockFragment = new PowerLockFragment();
                }
                transaction.replace(R.id.frame_pager, mPowerLockFragment);
                break;

            case R.id.rb_power_else:
                if(mPowerElseFragment == null){
                    mPowerElseFragment = new PowerElseFragment();
                }
                transaction.replace(R.id.frame_pager, mPowerElseFragment);
                break;

        }
        transaction.commit();
    }
    
}