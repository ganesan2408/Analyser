package com.yhh.afragment.status;
///**
// * @author yuanhh1
// * 
// * @email yuanhh1@lenovo.com
// * 
// */
//package com.yhh.status;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.RadioGroup;
//import android.widget.RadioGroup.OnCheckedChangeListener;
//
//import com.yhh.analyser.R;
//import com.yhh.utils.ScreenShot;
//import com.yhh.utils.TimeUtils;
//
//public class StatusAnalyser extends Activity implements
//        OnCheckedChangeListener {
//    
//    private RadioGroup main_tab_RadioGroup;
//    
//    private Fragment mTopProcessFragment;
//    private Fragment mTopThreadFragment;
//    private Fragment mPowerFragment;
//    private Fragment mBatteryFragment;
//    
//    private String chooseName;
//    
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.status_analyser_main);
//        InitView();
//        setDefaultFragment();
//    }
//    
//    public void InitView() {
//        main_tab_RadioGroup = (RadioGroup) findViewById(R.id.main_tab_RadioGroup);
//        main_tab_RadioGroup.setOnCheckedChangeListener(this);
//    }
//    
//    private void setDefaultFragment(){
//        FragmentManager fm = getFragmentManager();  
//        FragmentTransaction transaction = fm.beginTransaction();
//        mTopProcessFragment = new TopProcessFragment();
//        transaction.replace(R.id.main_Pager,  mTopProcessFragment);
//        transaction.commit();
//        chooseName = "top_process_";
//    }
//    
//    
//    @Override
//    public void onCheckedChanged(RadioGroup radioGroup, int CheckedId) {
//        FragmentManager fm = getFragmentManager();  
//        FragmentTransaction transaction = fm.beginTransaction();
//        
//        switch (CheckedId) {
//            case R.id.radio_top_process:
//                if(mTopProcessFragment == null){
//                    mTopProcessFragment = new TopProcessFragment();
//                }
//                transaction.replace(R.id.main_Pager,  mTopProcessFragment);
//                chooseName = "top_process_";
//                break;
//                
//            case R.id.radio_top_thread:
//                if(mTopThreadFragment == null){
//                    mTopThreadFragment = new TopThreadFragment();
//                }
//                transaction.replace(R.id.main_Pager,  mTopThreadFragment);
//                chooseName = "top_thread_";
//                break;
//                
//            case R.id.radio_power:
//                if(mPowerFragment == null){
//                    mPowerFragment = new PowerFragment();
//                }
//                transaction.replace(R.id.main_Pager,  mPowerFragment);
//                chooseName = "power_";
//                break;
//                
//            case R.id.radio_battery:
//                if(mBatteryFragment == null){
//                    mBatteryFragment = new BatteryFragment();
//                }
//                transaction.replace(R.id.main_Pager,  mBatteryFragment);
//                chooseName = "battery_";
//                break;
//        }
//        transaction.commit();
//    }
//    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.getMenuInflater().inflate(R.menu.status_analyser_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    
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
//    
//    public void showAlergDialog(Context context, String title,
//            String message) {
//        new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(R.string.yes_str,
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                    int which) {
//                            }
//                        }).show();
//    }
//    
//}