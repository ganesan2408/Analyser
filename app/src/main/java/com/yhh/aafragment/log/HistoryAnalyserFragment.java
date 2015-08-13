/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.aafragment.log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.log.analyser.LogChart;
import com.yhh.log.parser.BatteryLogParser;
import com.yhh.log.parser.LogcatParser;
import com.yhh.log.parser.PmLogParser;
import com.yhh.log.parser.SleepLogParser;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.utils.FileUtils;
import com.yhh.widget.numberprogressbar.NumberProgressBar;

public class HistoryAnalyserFragment extends Fragment{
    public static final String TAG = ConstUtils.DEBUG_TAG + "HistoryAnalyserFragment";
    private boolean DEBUG = true;
    
    private Context mContext;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    public static final String LOG_PATH="log path";
    
    public static final String sMylogDir =  Environment.getExternalStorageDirectory().getPath()+ "/myLog";
    public static String sLogCacheDir;
    
    public String mParseDir = ConstUtils.LOG_DIR;
    
    private NumberProgressBar mNumberBar;
    private ListView mLv;
    private Button parseBtn;
    
    private int mTargetNum = 1;
    
    private String[] newLog;
    private int selectedFileIndex;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        
        sLogCacheDir = mContext.getFilesDir().getAbsolutePath() + "/log_cache";
        FileUtils.createFolder(sLogCacheDir);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.log_history_parser, container, false);  
        mNumberBar = (NumberProgressBar) v.findViewById(R.id.log_loading);
        parseBtn = (Button) v.findViewById(R.id.log_common_parser);
        parseBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showChooseLogDialog();
            }
        });
        mLv = (ListView) v.findViewById(R.id.newLogList);
        return v;
    }
    
    public void onStart(){
        super.onStart();
        mHandler.sendEmptyMessage(1);
    }
    
    
    private void drawLog(String path){
        Intent i = new Intent(mContext, LogChart.class);
        i.putExtra(LOG_PATH, path);
        Log.i(TAG,"draw log name="+ path);
        startActivity(i);
    }
    
    /**
     *  解析指定路径下的Log
     *  
     * @param Dir
     *      需要解析的Log所在文件夹路径
     */
    private int parseLog(String Dir){
        FileUtils.deleteFile(HistoryAnalyserFragment.sLogCacheDir);
        FileUtils.createFolder(HistoryAnalyserFragment.sLogCacheDir);
        
        mTargetNum = getLogNum(Dir);
        Log.i(TAG,"mTargetNum="+mTargetNum);
        if(mTargetNum<=0){
            return 0;
        }
        
        Log.d(TAG,"delete log cache succuss.");
        BatteryLogParser mBatteryLogParser = new BatteryLogParser(Dir);
        mBatteryLogParser.parse(mHandler);
        
        SleepLogParser mSleepLogParser = new SleepLogParser(Dir);
        mSleepLogParser.parse(mHandler);
        
        PmLogParser mPmLogParser = new PmLogParser(Dir);
        mPmLogParser.parse(mHandler);
        
        LogcatParser mLogcatParser = new LogcatParser(Dir);
        mLogcatParser.parse(mHandler);
        return 1;
    }
    
    protected int getLogNum(String dir){
        int num = 0;
        
        File parentDir = new File(dir);  
        if (parentDir.exists()) {  
           File[] files = parentDir.listFiles();
           for(File f:files){
               if(f.getName().contains(ConstUtils.LOG_BATTERY) ||
                  f.getName().contains(ConstUtils.LOG_PMLOG) ||
                  f.getName().contains(ConstUtils.LOG_LOGCAT) ||
                  f.getName().contains(ConstUtils.LOG_SLEEP)){
                   num++;
               }
           }
        }
        return num;
    }
    
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what ==1){
                mNumberBar.setVisibility(View.GONE);
                mNumberBar.setProgress(0);
                
                newLog = FileUtils.listFiles(sLogCacheDir);
                if(newLog==null || newLog.length <=0){
                    DialogUtils.closeLoading();
                    return;
                }
                mLv.setAdapter(new ListAdapter());
                mLv.setOnItemClickListener(new OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        RadioButton rdBtn = (RadioButton) ((LinearLayout) view).getChildAt(0);
                        rdBtn.setChecked(true);
                        drawLog(newLog[position]);
                    }
                    
                });
                DialogUtils.closeLoading();
            }else if(msg.what ==2){
                mHandler.sendMessage(mHandler.obtainMessage(3, (int)100/mTargetNum));
            }else if(msg.what == 3){
                mNumberBar.incrementProgressBy(1);
                if((Integer)msg.obj>1){
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(3, (Integer)msg.obj -1), 100);
                }
            }else{
                mNumberBar.setVisibility(View.GONE);
                mNumberBar.setProgress(0);
                Toast.makeText(mContext, "未发现任何Log", Toast.LENGTH_SHORT).show();
            }
        };
    };
    
    
    @Override
    public void onPause() {
        super.onPause();
        if(mParseDir ==null || mParseDir.equals("")){
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Editor editor = pref.edit();
        editor.putString("log_dir", mParseDir);
        editor.commit();
    }
    
    public void showChooseLogDialog(){
        final String[] files = FileUtils.listFolders(sMylogDir);
        Log.i(TAG,"files.LEN="+ (files==null?0:files.length));
        
        new AlertDialog.Builder(mContext)
        .setTitle("选择目标Log (/sdcard/myLog)")
        .setSingleChoiceItems(files, -1, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedFileIndex = which;
            }
        })
        .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        })
        .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"selectedFileIndex="+selectedFileIndex);
                if(files==null || files.length <1){
                    return;
                }
                mParseDir = FileUtils.PATH_SD_LOG + "/"+files[selectedFileIndex];
                
                if(FileUtils.checkPath(mParseDir+"/aplog")){
                    mParseDir += "/aplog";
                }else if(FileUtils.checkPath(mParseDir+"/curlog")){
                    mParseDir += "/curlog";
                }
                
                Log.d(TAG,"mParseDir:"+ mParseDir);
                runParse(mParseDir);
            }
        }).show();
    }
    
    private void runParse(String logDir){
        mNumberBar.setVisibility(View.VISIBLE);
        final String dir = logDir;
        mExecutorService.execute(new Runnable(){

            @Override
            public void run() {
                int rnt = parseLog(dir);
                if(rnt==0){
                    mHandler.sendEmptyMessage(4);
                }else{
                    mHandler.sendEmptyMessage(1);
                }
            }
        });
    }
    
    
    private class ListAdapter extends BaseAdapter {
        int checkedPosition = -1;
        int lastCheckedPosition = -1;

        public ListAdapter() {
            
        }
        
        @Override
        public int getCount() {
            return newLog.length;
        }

        @Override
        public Object getItem(int position) {
            return newLog[position];
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
                holder.logNameTv = (TextView) convertView.findViewById(R.id.commom_item);
                holder.choiceButton = (RadioButton) convertView.findViewById(R.id.commom_rb);
                holder.choiceButton.setFocusable(false);
                holder.choiceButton.setOnCheckedChangeListener(checkedChangeListener);
            }
            holder.logNameTv.setText(newLog[position]);
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
                            tempButton.setChecked(false);
                        }
                    }
                    lastCheckedPosition = checkedPosition;
                }
            }
        };
    }

    static class Viewholder {
        TextView logNameTv;
        RadioButton choiceButton;
    }
}
