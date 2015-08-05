/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.log.viewer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.info.app.PhoneInfo;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.utils.FileUtils;
import com.yhh.utils.TimeUtils;
import com.yhh.widget.slidingmenu.SlidingMenu;

public class LogViewerActivity extends Activity {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "LogViewerActivity";
    private static final int DELETE_CURRENT_LOG = 1;
    private static final int DELETE_HISTORY_LOG = 2;
    
    private ListView mLogLv;
    private SlidingMenu logMenu;
    
    private FragmentManager fm;
    private FragmentTransaction ft;
    
    @SuppressLint({ "NewApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_analyser_main);
        
        initActionBar();
        initMenu();
        
        fm =  this.getFragmentManager();
        setDaultFragment();
        if(!PhoneInfo.getBrand().contains(ConstUtils.BRAND_LENOVO)){
            Log.e(TAG,PhoneInfo.getBrand()+" Not support!");
            Toast.makeText(this, PhoneInfo.getBrand()+" Not support!", Toast.LENGTH_LONG).show();
        }
    }
    
    private void setDaultFragment(){
        ft = fm.beginTransaction();
        CurrentLogListFragment currentFragment = new CurrentLogListFragment();
        ft.replace(R.id.log_fragment_content,currentFragment);
        ft.commit();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            logMenu.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.introduction_doc){
            DialogUtils.showAlergDialog(this,getString(R.string.introduction_title),
                    getString(R.string.introduction_log_viewer));
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.introduction, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case 0x11: //保存log结束
                    DialogUtils.closeLoading();
                    showHistoryLog();
                    break;
            }
        };
    };
    
    
    private void openOptionsDialog(final int option) {
        int msg = 0;
        if(option == DELETE_CURRENT_LOG){
            msg= R.string.delete_current_log;
        }else if(option == DELETE_HISTORY_LOG){
            msg= R.string.delete_history_log;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if(option == DELETE_CURRENT_LOG){
                            deleteCurrentLog();
//                            filterLog(FileUtils.PATH_APLOG);
                        }else if(option == DELETE_HISTORY_LOG){
                            deleteHistoryLog();
                        }
                        showCurrentLog();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                            }
                        }).show();
    }

    /**
     * 保存log
     */
    public void saveAllLog(){
        Log.d(TAG,"save all Log");
        new Thread(new Runnable(){

            @Override
            public void run() {
                String srcDir = ConstUtils.LOG_DIR;
                String desDir = FileUtils.PATH_SD_LOG +"/aplog_"+ TimeUtils.getCurrentTime();
                FileUtils.copyFolder(srcDir, desDir);
                mHandler.sendEmptyMessage(0x11);
            }
            
        }).start();
    }
    
    /**
     * 删除实时 Log
     */
    public void deleteCurrentLog(){
        Log.d(TAG,"delete all Log");
        FileUtils.deleteFile(ConstUtils.LOG_DIR);
    }
    
    /**
     * 删除 History Log
     */
    public void deleteHistoryLog(){
        Log.d(TAG,"deleteHistoryLog");
        FileUtils.deleteFile(FileUtils.PATH_SD_LOG);
    }
    
    /**
     * 初始化 功能菜单
     */
    private void initMenu(){
        logMenu = new SlidingMenu(LogViewerActivity.this);
        logMenu.setMode(SlidingMenu.LEFT);
        logMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置触摸屏幕的模式 
        logMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 设置滑动菜单视图的宽度
        logMenu.setFadeDegree(0.35f);  // 设置渐入渐出效果的值 
        logMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        logMenu.setMenu(R.layout.log_analyser_menu);
        logMenu.setBackgroundColor(0xe7ebf1);
    }
    
    private void showCurrentLog(){
        ft = fm.beginTransaction();
        CurrentLogListFragment currentFragment = new CurrentLogListFragment();
        ft.replace(R.id.log_fragment_content,currentFragment);
        ft.commit();
        logMenu.toggle();
    }
    
    private void showHistoryLog(){
        ft = fm.beginTransaction();
        HistoryLogListFragment historyFragment = new HistoryLogListFragment();
        ft.replace(R.id.log_fragment_content,historyFragment);
        ft.commit();
        logMenu.toggle();
    }
    
    public void menuHandler(View v){
        switch(v.getId()){
            case R.id.view_current_log_tv:
                showCurrentLog();
                break;
                
            case R.id.view_history_log_tv:
                showHistoryLog();
                break;
                
            case R.id.delete_current_log_tv:
                openOptionsDialog(DELETE_CURRENT_LOG);
                break;
                
            case R.id.delete_history_log_tv:
                openOptionsDialog(DELETE_HISTORY_LOG);
                break;
                
            case R.id.save_log_tv:
                DialogUtils.showLoading(this,"正在保存中...");
                saveAllLog();
                break;
        }
    }
    
    @SuppressLint("NewApi")
    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }
}
