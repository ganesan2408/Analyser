/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.adapter.AutoCaseAdapter;
import com.yhh.analyser.provider.AutoCaseSettings;
import com.yhh.analyser.provider.AutoWorker;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DialogUtils;

public class AutomaticActivity extends BaseActivity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "AutomaticActivity";

	private ListView mListProgramLv;
	private Button mRobotBtn;
	private SharedPreferences preferences;
	private AutoWorker mRobot;
	private boolean isStart;
	private String appName;
	private String[] mShowCases;
	
	public static final String KEY_AUTOMATIC = "automatic";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor_mainpage);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		initUI();
		mRobot = new AutoWorker(this);
		mRobot.setOnAutomaticListener(new AutoWorker.OnAutomaticListener() {
	            
	            @Override
	            public void onRunComplete() {
	                Log.i(TAG,"onRunComplete");
	            }
	            
	            @Override
	            public void onPrepareComplete() {
	                Log.i(TAG,"onPrepareComplete");
	                DialogUtils.closeLoading();
	            }
	        });
		mRobot.copyRobotResouce();
		
		isStart = preferences.getBoolean(KEY_AUTOMATIC, false);
		if(isStart){
            mRobotBtn.setText(getString(R.string.stop_automatic));
        }else{
            mRobotBtn.setText(getString(R.string.start_automatic));
        }
		
		mRobotBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        if (getString(R.string.start_automatic).equals(mRobotBtn.getText().toString())) {
                        if(appName ==null){
                            Toast.makeText(AutomaticActivity.this, "请选择Case", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i(TAG,"appName:"+appName);
		                mRobot.autoRun(appName);
                        mRobotBtn.setText(getString(R.string.stop_automatic));
                        preferences.edit().putBoolean(KEY_AUTOMATIC, true).commit();
		        }else{
		            mRobot.autoStop();
		            mRobotBtn.setText(getString(R.string.start_automatic));
		            preferences.edit().putBoolean(KEY_AUTOMATIC, false).commit();
		        }
			}
		});
		
		mShowCases = this.getResources().getStringArray(R.array.case_show_names);
		final String[] backupCases = this.getResources().getStringArray(R.array.case_backup_names);
		mListProgramLv.setAdapter(new AutoCaseAdapter(this, mShowCases));
		mListProgramLv.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                RadioButton rdBtn = (RadioButton) ((LinearLayout) view).getChildAt(0);
                rdBtn.setChecked(true);
                appName = backupCases[position];
            }
		    
		});
		
		mListProgramLv.setOnItemLongClickListener(new OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                AutoCaseSettings cs = new AutoCaseSettings(AutomaticActivity.this, position);
                return false;
            }
		    
		});
	}
	
	private void initUI(){
	       mListProgramLv = (ListView) findViewById(R.id.processList);
	       mRobotBtn = (Button) findViewById(R.id.app_monitor_btn);
	       mRobotBtn.setText(getString(R.string.start_automatic));
	}
	

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		
		isStart = preferences.getBoolean(KEY_AUTOMATIC, false);
		if(isStart){
		    mRobotBtn.setText(getString(R.string.stop_automatic));
		}else{
		    mRobotBtn.setText(getString(R.string.start_automatic));
		}
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.introduction, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.introduction_doc){
            DialogUtils.showAlergDialog(this,getString(R.string.introduction_title),
                    getString(R.string.introduction_automatic));
        }
        return super.onOptionsItemSelected(item);
    }
	
}
