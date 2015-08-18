/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.provider.AutoWorker;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DialogUtils;

public class AutoActivity extends Activity {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "AutoActivity";

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
		initActionBar();
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
                            Toast.makeText(AutoActivity.this, "请选择Case", Toast.LENGTH_SHORT).show();
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
		mListProgramLv.setAdapter(new ListAdapter());
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
                //
                AutoCaseSettings cs = new AutoCaseSettings(AutoActivity.this, position);
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
	
	private class ListAdapter extends BaseAdapter {
        int checkedPosition = -1;
        int lastCheckedPosition = -1;

        public ListAdapter() {
            
        }
        
        @Override
        public int getCount() {
            return mShowCases.length;
        }

        @Override
        public Object getItem(int position) {
            return mShowCases[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.common_list_items, parent, false);
            
            Viewholder holder = (Viewholder) convertView.getTag();
            if (holder == null) {
                holder = new Viewholder();
                convertView.setTag(holder);
                holder.caseNameTv = (TextView) convertView.findViewById(R.id.commom_item);
                holder.choiceButton = (RadioButton) convertView.findViewById(R.id.commom_rb);
                holder.choiceButton.setFocusable(false);
                holder.choiceButton.setOnCheckedChangeListener(checkedChangeListener);
            }
            holder.caseNameTv.setText(mShowCases[position]);
            holder.choiceButton.setId(position);
            holder.choiceButton.setChecked(checkedPosition == position);
            return convertView;
        }

        OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG,"isChecked="+isChecked);
                if (isChecked) {
                    checkedPosition = buttonView.getId();
                    if (lastCheckedPosition != -1) {
                        RadioButton tempButton = (RadioButton) findViewById(lastCheckedPosition);
                        if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
                            Log.d(TAG,"$$$tempButton.setChecked(false)");
                            tempButton.setChecked(false);
                        }
                    }
                    lastCheckedPosition = checkedPosition;
                }
            }
        };
    }

    static class Viewholder {
        TextView caseNameTv;
        RadioButton choiceButton;
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
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @SuppressLint("NewApi")
    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }
	
}
