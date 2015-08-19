/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.fragment;

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
import com.yhh.analyser.utils.ConstUtils;

public class MainAnalysisFragment extends Fragment implements
        OnCheckedChangeListener {
    
    private static final String TAG = ConstUtils.DEBUG_TAG+ "MainAnalysis";
    
    private RadioGroup mAnalysisRGroup;
    
    private Fragment mAnalysisMonitorFragment;
    private Fragment mAnalysisWakeupFragment;

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
            case R.id.rb_analysis_monitor:
                mAnalysisMonitorFragment = new AnalysisMonitorFragment();
                fragment = mAnalysisMonitorFragment;
                break;
                
            case R.id.rb_analysis_wakeup:
                mAnalysisWakeupFragment = new AnalysisWakeupFragment();
                fragment = mAnalysisWakeupFragment;
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
            case R.id.rb_analysis_monitor:
                if(mAnalysisMonitorFragment == null){
                    mAnalysisMonitorFragment = new AnalysisMonitorFragment();
                }
                transaction.replace(R.id.frame_pager,  mAnalysisMonitorFragment);
                break;
                
            case R.id.rb_analysis_wakeup:
                if(mAnalysisWakeupFragment == null){
                    mAnalysisWakeupFragment = new AnalysisWakeupFragment();
                }
                transaction.replace(R.id.frame_pager,  mAnalysisWakeupFragment);
                break;

        }
        transaction.commit();
    }
    
}