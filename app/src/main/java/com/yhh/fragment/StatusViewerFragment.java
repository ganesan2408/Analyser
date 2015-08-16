/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yhh.analyser.R;
import com.yhh.fragment.status.BatteryFragment;
import com.yhh.fragment.status.PowerFragment;
import com.yhh.fragment.status.TopProcessFragment;
import com.yhh.fragment.status.TopThreadFragment;

public class StatusViewerFragment extends Fragment implements
        OnCheckedChangeListener {
    
    private Context mContext;
    private RadioGroup main_tab_RadioGroup;
    
    private Fragment mBatteryFragment;
    private Fragment mTopProcessFragment;
    private Fragment mTopThreadFragment;
    private Fragment mPowerFragment;
    
    
    private String chooseName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.status_analyser_main, null);
        main_tab_RadioGroup = (RadioGroup) v.findViewById(R.id.main_tab_RadioGroup);
        main_tab_RadioGroup.setOnCheckedChangeListener(this);
        return v;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        setDefaultFragment();
    }
    
    
    private void setDefaultFragment(){
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_Pager,   
                getFragmentByCheckedId(main_tab_RadioGroup.getCheckedRadioButtonId()));
        transaction.commit();
    }
    
    private Fragment getFragmentByCheckedId(int CheckedId){
        Fragment fragment = null;
        switch (CheckedId) {
            case R.id.radio_top_process:
                mTopProcessFragment = new TopProcessFragment();
                chooseName = "top_process_";
                fragment = mTopProcessFragment;
                break;
                
            case R.id.radio_top_thread:
                mTopThreadFragment = new TopThreadFragment();
                chooseName = "top_thread_";
                fragment = mTopThreadFragment;
                break;
                
            case R.id.radio_power:
                mPowerFragment = new PowerFragment();
                chooseName = "power_";
                fragment = mPowerFragment;
                break;
                
            case R.id.radio_battery:
                mBatteryFragment = new BatteryFragment();
                chooseName = "battery_";
                fragment = mBatteryFragment;
                break;
                
            default:
                break;
        }
        return fragment;
    }
    
    
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int CheckedId) {
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();
        
        switch (CheckedId) {
            case R.id.radio_top_process:
                if(mTopProcessFragment == null){
                    mTopProcessFragment = new TopProcessFragment();
                }
                transaction.replace(R.id.main_Pager,  mTopProcessFragment);
                chooseName = "top_process_";
                break;
                
            case R.id.radio_top_thread:
                if(mTopThreadFragment == null){
                    mTopThreadFragment = new TopThreadFragment();
                }
                transaction.replace(R.id.main_Pager,  mTopThreadFragment);
                chooseName = "top_thread_";
                break;
                
            case R.id.radio_power:
                if(mPowerFragment == null){
                    mPowerFragment = new PowerFragment();
                }
                transaction.replace(R.id.main_Pager,  mPowerFragment);
                chooseName = "power_";
                break;
                
            case R.id.radio_battery:
                if(mBatteryFragment == null){
                    mBatteryFragment = new BatteryFragment();
                }
                transaction.replace(R.id.main_Pager,  mBatteryFragment);
                chooseName = "battery_";
                break;
        }
        transaction.commit();
    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() ==R.id.menu_status_else){
//            Intent intent = new Intent(this, DumpsysStatusActivity.class);
//            startActivity(intent);
//            return true;
//        }else if(item.getItemId() ==R.id.menu_shoot){
//            ScreenShot.shoot(this,chooseName+TimeUtils.getTime());
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
}