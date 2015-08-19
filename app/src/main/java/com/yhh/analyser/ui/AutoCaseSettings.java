/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.ConstUtils;

public class AutoCaseSettings {
	private static final String TAG =  ConstUtils.DEBUG_TAG+ "AutoCaseSettings";
	private boolean DEBUG = true;

	private Context mContext;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	private ListView mCaseParamLv;
	
	
	private static final int[] PARAM_NAME =  {
	    R.array.qq_param_name,
	    R.array.weixin_param_name,
	    R.array.Weibo_param_name,
	    R.array.Browser_param_name,
	    R.array.Launcher_param_name,
	    R.array.Read_param_name,
	    R.array.Email_param_name,
	    R.array.Camera_param_name,
	    R.array.MusicPlayer_param_name,
	    R.array.Call_param_name,
	    R.array.Workflow_param_name,
	    R.array.Workflow_param_name
    };
	
	private static final int[] PARAM_KEY =  {
	    R.array.qq_param_key,
	    R.array.weixin_param_key,
	    R.array.Weibo_param_key,
	    R.array.Browser_param_key,
	    R.array.Launcher_param_key,
	    R.array.Read_param_key,
	    R.array.Email_param_key,
	    R.array.Camera_param_key,
	    R.array.MusicPlayer_param_key,
	    R.array.Call_param_key,
	    R.array.Workflow_param_key,
	    R.array.Workflow_param_key
	};
	
    private String mCaseName;
    private String[] mParamNames;
    private String[] mParamValues;
    private String[] mPrefKeys;
    private int mParamCount;
	
    public AutoCaseSettings(Context context, int caseIndex){
        mContext =context;
        
        if(init(caseIndex)){
            openDialog();
        }else{
            Log.e(TAG, "case index too much.");
        }
    }
    
	public boolean init(int caseIndex){
	    String[] caseNames = mContext.getResources().getStringArray(R.array.case_backup_names);
	    if(caseIndex >= caseNames.length){
	        return false;
	    }
	    mCaseName = caseNames[caseIndex];
	    
        mParamNames = mContext.getResources().getStringArray(PARAM_NAME[caseIndex]);
        mPrefKeys = mContext.getResources().getStringArray(PARAM_KEY[caseIndex]);
        
        mParamCount = mParamNames.length;
        mParamValues = new String[mParamCount];
        return true;
	}
    
	
	public void readData(){
        for(int i=0; i<mParamCount;i++){
            mParamValues[i] = mPreferences.getString(mCaseName + "#" + mPrefKeys[i], "");
        }
    }
	
	public void save(){
	    for(int i=0; i<mParamCount;i++){
	        View view = mCaseParamLv.getChildAt(i);
            EditText et = (EditText) view.findViewById(R.id.case_param_value);
            mEditor.putString(mCaseName + "#" + mPrefKeys[i], et.getText().toString());
        }
	    mEditor.commit();
	}
	
	public void openDialog(){
	    
	    LayoutInflater inflater = LayoutInflater.from(mContext); 
	    View view = inflater.inflate(R.layout.case_settings, null);
	    mPreferences = mContext.getSharedPreferences("robot_config", 0);
        mEditor = mPreferences.edit();
        
        mCaseParamLv = (ListView) view.findViewById(R.id.case_settings_lv);
        readData();
        mCaseParamLv.setAdapter(new ListAdapter());
        
	    AlertDialog.Builder builder = new  AlertDialog.Builder(mContext);
        builder.setTitle(mCaseName)
               .setView(view) 
               .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        save();
                    }
                })
                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
	
	private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mParamCount;
        }

        @Override
        public Object getItem(int position) {
            return mPrefKeys[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.case_setting_item, parent, false);
                Viewholder holder = (Viewholder) convertView.getTag();
            if (holder == null) {
                holder = new Viewholder();
                convertView.setTag(holder);
                holder.paramNameTv = (TextView) convertView.findViewById(R.id.case_param_name);
                holder.paramValueEt = (EditText) convertView.findViewById(R.id.case_param_value);
            }
            holder.paramNameTv.setText(mParamNames[position]);
            holder.paramValueEt.setText(mParamValues[position]);
            
            return convertView;
        }
    }

    static class Viewholder {
        TextView paramNameTv;
        EditText paramValueEt;
    }
}
