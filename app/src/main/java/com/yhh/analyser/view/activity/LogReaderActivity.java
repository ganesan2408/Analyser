/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.yhh.analyser.R;
import com.yhh.analyser.bean.LogInfo;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DialogUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LogReaderActivity extends BaseActivity implements TabListener{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "LogReader";
    private ViewFlipper mViewFlipper;
    private GestureDetector mDetector;
    /** 搜索的LOG*/
    private ListView mSLogLv;
    /** 完整的LOG*/
    private ListView mALogTv;
    
    private LinearLayout mSearchLayout;
    private int mMode;
    private LogInfo mALog;
    private LogInfo mSLog;
    private static final int MODE_ALOG = 0;
    private static final int MODE_SLOG = 1;
    
    private MyHandler mHandler;

    /** 页码内容*/
    private TextView mPageTv;
    /** 搜索关键字内容*/
    private AutoCompleteTextView mSearchedKeyTv;

    private String logPath;
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_reader);
        
        mHandler = new MyHandler();
        
        mDetector = new GestureDetector(this, new LogReaderGestureListener());
        
        Bundle bundle = this.getIntent().getExtras();
        logPath = bundle.getString("logPath");
        
        mALog = new LogInfo();
        mSLog = new LogInfo();
        
        initLogUI();
//        initActionBar();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_reader_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
                
            case R.id.menu_search:
                if(mSearchLayout.getVisibility() ==View.VISIBLE){
                    mSearchLayout.setVisibility(View.GONE);
                }else{
                    mSearchLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
//    @SuppressLint("NewApi")
//    private void initActionBar(){
//        mActionBar.setHomeButtonEnabled(true);
//        mActionBar.setIcon(R.drawable.nav_back);
        
//        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        mActionBar.addTab(mActionBar.newTab().setText("全部Log")
//                .setTabListener(this));
//        mActionBar.addTab(mActionBar.newTab().setText("搜索Log")
//                .setTabListener(this));
//    }
    
//    public void addTabs(ArrayList<String> tabNames){
//        if(null != mActionBar && null != tabNames){
//            for(String tabName: tabNames){
//                mActionBar.removeAllTabs();
//                mActionBar.addTab(mActionBar.newTab().setText(tabName)
//                        .setTabListener(this));
//            }
//        }
//    }
    
    public void showPrev(){
        showDisplayId(MODE_ALOG);
    }
    
    public void showNext(){
        showDisplayId(MODE_SLOG);
    }
    
    
    private void showDisplayId(int position){
        if(position == MODE_ALOG){
            mMode = MODE_ALOG;
            mPageTv.setText((mALog.getCurrentNum()+1) + "/" + mALog.getMaxNum());
        }else if(position == MODE_SLOG){
            mMode = MODE_SLOG;
            mPageTv.setText((mSLog.getCurrentNum()+1) + "/" + mSLog.getMaxNum());
        }
        
        int oldPosition = mViewFlipper.getDisplayedChild();
        Log.i(TAG,"oldPosition="+oldPosition+",position="+position);
        if (position != oldPosition) {
            mViewFlipper.setDisplayedChild(position);
        }
    }
    
    class MyHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x11) {        // 显示首页的log
                DialogUtils.closeLoading();
                updateLogUI();
                mPageTv.setTextColor(Color.RED);
            } else if (msg.what == 0x12) { // 显示log加载完成
                DialogUtils.closeLoading();
                updateLogUI();
                mPageTv.setTextColor(Color.BLACK);
                Log.d(TAG, "load completely.");
            }else if (msg.what == 0x99) {
                DialogUtils.closeLoading();
                Toast.makeText(LogReaderActivity.this, "权限不够", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void beginSearch(final String key){
        if (TextUtils.isEmpty(key)) {
            Log.i(TAG, "key is null");
            return;
        }
        
        hideSoftInput(mSearchedKeyTv);
        new Thread(new Runnable() {

            @Override
            public void run() {
                searchByPage(key);
            }
        }).start();
        Log.d(TAG, "Loading...");
        DialogUtils.showLoading(LogReaderActivity.this, "正在搜索中");
    }
    
    private void hideSoftInput(View v){
        InputMethodManager  imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void showSoftInput(View v){
        InputMethodManager  imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        if(imm.isActive()){
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        }
    }
    
    private void initLogUI() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.log_viewfliper);
        mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
        
        mSLogLv = (ListView) findViewById(R.id.search_log_info);
        mALogTv = (ListView) findViewById(R.id.log_info);
        mPageTv = (TextView) findViewById(R.id.pageNum);
        
        mSearchedKeyTv = (AutoCompleteTextView) findViewById(R.id.searchKey);
        mSearchedKeyTv.setThreshold(1);
        mSearchLayout.setVisibility(View.GONE);
        
        showDisplayId(0);
        
        String[] keyArray = null;
        for (String lname : ConstUtils.LOG_ALL) {
            Log.i(TAG,"logPath====="+logPath);
            int index = logPath.lastIndexOf("/");
            String  filename = logPath.substring(index+1);
            Log.i(TAG,"lname====="+lname);
            if (filename.startsWith(lname)) {
                keyArray = ConstUtils.KEY_TABLE.get(lname);
                if (keyArray != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            this, R.layout.auto_comlete_drop_down, keyArray);
                    mSearchedKeyTv.setAdapter(adapter);
                }
                break;
            }
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                readFileByPage(logPath);
            }
        }).start();
        Log.d(TAG, "Loading...");
        DialogUtils.showLoading(LogReaderActivity.this);
    }
    
    public void pageHandler(View v){
        switch(v.getId()){
            case R.id.firstPage:
                goFirstPage();
                break;
                
            case R.id.lastPage:
                goLastPage();
                break;
            
            case R.id.prePage:
                goPrePage();
                break;
            
            case R.id.nextPage:
                goNextPage();
                break;
            
            case R.id.pageNum:
                selectPageDialog();
                break;
                
            case R.id.search_btn:
                showNext();
                final String searchKey = mSearchedKeyTv.getText().toString();
                beginSearch(searchKey);
                mActionBar.setSelectedNavigationItem(1);
                mSearchLayout.setVisibility(View.GONE);
                break;
        }
    }
    
    private void readFileByPage(String filePath) {
        mMode = MODE_ALOG;
        BufferedReader br = null;
        String line;
        int lineNum = 0;
        boolean haveNotify = false;
        ArrayList<String> list = new ArrayList<String>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    filePath)));
            while ((line = br.readLine()) != null) {
                list.add("[" + (lineNum + 1) + "] " + line);
                lineNum++;
                if (lineNum % LogInfo.LINES_PRE_PAGE == 0) {
                    mALog.appendLog(list);
                    list = new ArrayList<String>();
                    
                    if (!haveNotify) {
                        mHandler.sendMessage(mHandler.obtainMessage(0x11));
                        haveNotify = true;
                    }
                }
            }
            if(list.size() >0){
                mALog.appendLog(list);
            }
            Log.i(TAG,"alog max:"+mALog.getMaxNum());
            mHandler.sendMessage(mHandler.obtainMessage(0x12));
        } catch (Exception e) {
            mHandler.sendMessage(mHandler.obtainMessage(0x99));
            Log.e(TAG, "read error" + e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void searchByPage(String key) {
//        Pattern pattern = null;
//        try{
//            pattern = Pattern.compile(key);
//        }catch(Exception e){
//            Toast.makeText(this, "Invalid regex expression.", Toast.LENGTH_SHORT).show();
//            Log.e(TAG,"pattern key wrong:"+key);
//            return;
//        }
       
        boolean haveNotify = false;
        int arrSub = 0;
        ArrayList<String> aList = new ArrayList<String>();
        mSLog.clearAll();
        for (int i = 0; i < mALog.getMaxNum(); i++) {
            ArrayList<String> tmpLog = mALog.getLog(i);
            int len = tmpLog.size();
            for (int j = 0; j < len; j++) {
                if (tmpLog.get(j).contains(key)) {
//                    if(pattern.matcher(tmpLog.get(j)).find()){
                    aList.add(tmpLog.get(j));
                    arrSub++;
                    if (arrSub > LogInfo.LINES_PRE_PAGE-1) {
                        mSLog.appendLog(aList);
                        aList = new ArrayList<String>();
                        arrSub =0;
                        if (!haveNotify) {
                            mHandler.sendMessage(mHandler.obtainMessage(0x11));
                            haveNotify = true;
                        }
                    } 
                }
            }
        }
        if(aList.size() >0){
            mSLog.appendLog(aList);
        }
        mHandler.sendMessage(mHandler.obtainMessage(0x12));
    }
    
    /**
     * 根据行号，跳转到相应的页面
     * @param lineNum
     *      log所在行
     */ 
    private void setALogCurrentPage(int lineNum){
        showPrev();
//        setSearchUIVisible(false);
        if(lineNum>0){
            int pageNum = (lineNum-1)/ LogInfo.LINES_PRE_PAGE;
            mALog.setCurrent(pageNum);
        }
    }
    
    private void showALog(int specLineNum){
        setALogCurrentPage(specLineNum);
        int lineNum = specLineNum % LogInfo.LINES_PRE_PAGE;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.search_log_item, mALog.getCurrentLog());
        mALogTv.setAdapter(adapter);
        mALogTv.setSelection(lineNum-1);
         
        mPageTv.setText((mALog.getCurrentNum()+1) + "/" + mALog.getMaxNum());
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
    /**
    * 
    * @param
    *
    */
   private void updateLogUI() {
       if(mMode == MODE_ALOG){
           ArrayList<String> curLog = mALog.getCurrentLog();
           if(curLog !=null && curLog.size()>0){
               ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                       R.layout.search_log_item, curLog);
               mALogTv.setAdapter(adapter);
               mPageTv.setText((mALog.getCurrentNum()+1) + "/" + mALog.getMaxNum());
           }else{
               mSLogLv.setAdapter(null);
           }
       }else if(mMode == MODE_SLOG){
           ArrayList<String> curLog = mSLog.getCurrentLog();
           if(curLog !=null && curLog.size()>0){
               ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                       R.layout.search_log_item, curLog);
               mSLogLv.setAdapter(adapter);
               mSLogLv.setOnItemClickListener(new OnItemClickListener(){
    
                   @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                        String line = mSLog.getCurrentLog().get(position);
                        int row = mSLog.getRowFromLine(line);
                        Log.i(TAG,"row:"+row);
                        showALog(row);
                        mActionBar.setSelectedNavigationItem(0);
                    }
               });
               mPageTv.setText((mSLog.getCurrentNum()+1) + "/" + mSLog.getMaxNum());
           }else{
               Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
               mSLogLv.setAdapter(null);
           }
       }
   }
    
    private void goFirstPage() {
        if(mMode == MODE_ALOG){
            mALog.setCurrent(0);
        }else if(mMode == MODE_SLOG){
            mSLog.setCurrent(0);
        }
        updateLogUI();
    }

    private void goLastPage() {
        if(mMode == MODE_ALOG){
            mALog.setCurrent(mALog.getMaxNum()-1);
        }else if(mMode == MODE_SLOG){
            mSLog.setCurrent(mSLog.getMaxNum()-1);
        }
        updateLogUI();
    }

    private void goPrePage() {
        if(mMode == MODE_ALOG){
            mALog.preCurrent();
        }else if(mMode == MODE_SLOG){
            mSLog.preCurrent();
        }
        updateLogUI();
    }

    private void goNextPage() {
        if(mMode == MODE_ALOG){
            mALog.nextCurrent();
        }else if(mMode == MODE_SLOG){
            mSLog.nextCurrent();
        }
        updateLogUI();
    }

    private void goToPage(int pageNum) {
        if(mMode == MODE_ALOG){
            mALog.setCurrent(pageNum);
        }else if(mMode == MODE_SLOG){
            mSLog.setCurrent(pageNum);
        }
        updateLogUI();
    }
    
    public void selectPageDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.log_reader_select_page, null);
        final EditText et = (EditText) view.findViewById(R.id.targetPageNum);
        new AlertDialog.Builder(this)
                .setTitle("请输入跳转页码")
                .setView(view)
                .setNegativeButton(R.string.no_str,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        })
                .setPositiveButton(R.string.yes_str,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                try {
                                    int pageNum = Integer.valueOf(et.getText()
                                            .toString());
                                    Log.i(TAG, "pageNum=" + pageNum);
                                    goToPage(pageNum - 1);
                                    hideSoftInput(view);
                                } catch (NumberFormatException e) {
                                    Toast.makeText(LogReaderActivity.this, "请输入数字",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.i(TAG,"mActionBar tab.getPosition()="+tab.getPosition());
       showDisplayId(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        
    }
    
    private class LogReaderGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);
            if (absVelocityX > Math.max(1000.0f, 2.0 * absVelocityY)) {
                if (velocityX > 0) {
                    showPrev();
                } else {
                    showNext();
                }
                return true;
            } else {
                return false;
            }
        }
    }
    
}
