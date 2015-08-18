/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.ShellUtils;
import com.yhh.analyser.utils.ShellUtils.CommandResult;
import com.yhh.analyser.widget.IndexableListView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatusDumpsysActivity extends Activity {
    private static final String TAG = ConstUtils.DEBUG_TAG+ "StatusDumpsysActivity";
	private List<String> mItems;
	
	private ExecutorService fixedThreadPool = Executors.newCachedThreadPool();
	
	private IndexableListView mListView;
	private LinearLayout mDumpsysIndexLayout;
	private LinearLayout mDumpsysContentLayout;
	private TextView mContentTv;
	private boolean isDumpsysVisiable =false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indexable_main);
        initUi();
        
        String[] params= getResources().getStringArray(R.array.dumpsys_params);
        mItems = Arrays.asList(params);
        IndexableListView.ContentAdapter adapter = mListView.new ContentAdapter(this,
                android.R.layout.simple_list_item_1, mItems);
        
        mListView.setAdapter(adapter);
        mListView.setFastScrollEnabled(true);
        
        mListView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                runDumpsys(mItems.get(position));
            }
            
        });
    }
    
    @Override
    public void onBackPressed() {
        if(isDumpsysVisiable){
            mDumpsysContentLayout.setVisibility(View.GONE);
            mDumpsysIndexLayout.setVisibility(View.VISIBLE);
            isDumpsysVisiable = false;
        }else{
            super.onBackPressed();
        }
        
    }
    
    
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String  info= (String) msg.obj;
                mDumpsysContentLayout.setVisibility(View.VISIBLE);
                mDumpsysIndexLayout.setVisibility(View.INVISIBLE);
                isDumpsysVisiable = true;
                
                mContentTv.setText(info);
            }
            
        };
    };
    
    private void runDumpsys(final String params){
        fixedThreadPool.execute(new Runnable(){
            @Override
            public void run() {
                CommandResult cr = ShellUtils.execCommand("dumpsys "+params, false);
                mHandler.sendMessage(mHandler.obtainMessage(1,cr.successMsg));
            }
        });
    }
    
    private void initUi(){
        mListView = (IndexableListView) findViewById(R.id.listview);
        
        mDumpsysContentLayout = (LinearLayout) findViewById(R.id.dumpsys_content_ll);
        mDumpsysIndexLayout = (LinearLayout) findViewById(R.id.dumpsys_index_ll);
        mContentTv = (TextView) findViewById(R.id.dumpsys_content);
    }
}