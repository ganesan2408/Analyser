/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.fragment.toolbox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.yhh.info.app.PhoneInfo;
import com.yhh.robot.Automatic;
import com.yhh.robot.CaseSettings;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;

public class AutomaticFragment extends Fragment {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "AutomaticActivity";

	private Context mContext;
	private ListView mListProgramLv;
	private Button mRobotBtn;
	private SharedPreferences preferences;
	private Automatic mRobot;
	private boolean isStart;
	private String appName;
	private String[] mShowCases;
	
	public static final String KEY_AUTOMATIC = "automatic";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.app_monitor_mainpage);
		mContext = this.getActivity();
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.app_monitor_mainpage, null) ;
        mListProgramLv = (ListView) v.findViewById(R.id.processList);
        mRobotBtn = (Button) v.findViewById(R.id.app_monitor_btn);
        mRobotBtn.setText(getString(R.string.start_automatic));
        return v ;
    }
	
	public void onStart(){
	    super.onStart();
	    mRobot = new Automatic(mContext);
        mRobot.setOnAutomaticListener(new Automatic.OnAutomaticListener() {
                
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
                            Toast.makeText(mContext, "请选择Case", Toast.LENGTH_SHORT).show();
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
                CaseSettings cs = new CaseSettings(mContext, position);
                return false;
            }
            
        });
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
                convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.common_list_items, parent, false);
            
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
                        RadioButton tempButton = (RadioButton) ((Activity) mContext).findViewById(lastCheckedPosition);
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
	
}
