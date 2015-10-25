/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.provider.LogBatteryParser;
import com.yhh.analyser.provider.LogPmParser;
import com.yhh.analyser.provider.LogSleepParser;
import com.yhh.analyser.provider.LogcatParser;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DialogUtils;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.widget.numberprogressbar.NumberProgressBar;
import com.yhh.androidutils.FileUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogAnalyActivity extends BaseActivity {
    public static final String TAG = ConstUtils.DEBUG_TAG + "LogAnalyActivity";
    private boolean DEBUG = true;

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    public static final String LOG_PATH = "log path";

    public static String sLogCacheDir;

    public static String mParseDir = ConstUtils.LOG_DIR;

    private NumberProgressBar mNumberBar;
    private Button mParserBtn;
    private ListView mLv;
    private RadioButton mRadioButton;

    private int mTargetNum = 1;

    private String[] newLog;
    private int selectedFileIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_analyser_mainpage);


        sLogCacheDir = getApplicationContext().getFilesDir().getAbsolutePath() + "/log_cache";
        FileUtils.createFolder(sLogCacheDir);

        initUI();
        mHandler.sendEmptyMessage(1);
    }

    private void initUI() {
        mNumberBar = (NumberProgressBar) findViewById(R.id.log_loading);

        mParserBtn = (Button) findViewById(R.id.log_parser_btn);
        mParserBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooseLogDialog();
            }

        });
        mLv = (ListView) findViewById(R.id.newLogList);
    }


    private void drawLog(String path) {
        Intent i = new Intent(this, ChartLogActivity.class);
        i.putExtra(LOG_PATH, path);
        Log.i(TAG, "draw log name=" + path);
        startActivity(i);
    }

    /**
     * 解析指定路径下的Log
     *
     * @param Dir 需要解析的Log所在文件夹路径
     */
    private int parseLog(String Dir) {
        FileUtils.deleteFile(LogAnalyActivity.sLogCacheDir);
        FileUtils.createFolder(LogAnalyActivity.sLogCacheDir);

        mTargetNum = getLogNum(Dir);
        Log.i(TAG, "mTargetNum=" + mTargetNum);
        if (mTargetNum <= 0) {
            return 0;
        }

        Log.d(TAG, "delete log cache succuss.");
        LogBatteryParser mLogBatteryParser = new LogBatteryParser(Dir);
        mLogBatteryParser.parse(mHandler);

        LogSleepParser mLogSleepParser = new LogSleepParser(Dir);
        mLogSleepParser.parse(mHandler);

        LogPmParser mLogPmParser = new LogPmParser(Dir);
        mLogPmParser.parse(mHandler);

        LogcatParser mLogcatParser = new LogcatParser(Dir);
        mLogcatParser.parse(mHandler);
        return 1;
    }

    protected int getLogNum(String dir) {
        int num = 0;

        File parentDir = new File(dir);
        if (parentDir.exists()) {
            File[] files = parentDir.listFiles();
            for (File f : files) {
                if (f.getName().contains(ConstUtils.LOG_BATTERY) ||
                        f.getName().contains(ConstUtils.LOG_PMLOG) ||
                        f.getName().contains(ConstUtils.LOG_LOGCAT) ||
                        f.getName().contains(ConstUtils.LOG_SLEEP)) {
                    num++;
                }
            }
        }
        return num;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                mNumberBar.setVisibility(View.GONE);
                mNumberBar.setProgress(0);

                newLog = FileUtils.listFiles(sLogCacheDir);
                if (newLog == null || newLog.length <= 0) {
                    DialogUtils.closeLoading();
                    return;
                }
                mLv.setAdapter(new ListAdapter());
                mLv.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        RadioButton rdBtn = (RadioButton) ((LinearLayout) view).getChildAt(0);
                        rdBtn.setChecked(true);
                        drawLog(newLog[position]);
                    }

                });
                DialogUtils.closeLoading();
            } else if (msg.what == 2) {
                mHandler.sendMessage(mHandler.obtainMessage(3, 100 / mTargetNum));
            } else if (msg.what == 3) {
                mNumberBar.incrementProgressBy(1);
                if ((Integer) msg.obj > 1) {
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(3, (Integer) msg.obj - 1), 100);
                }
            } else {
                mNumberBar.setVisibility(View.GONE);
                mNumberBar.setProgress(0);
                Toast.makeText(LogAnalyActivity.this, "未发现任何Log", Toast.LENGTH_SHORT).show();
            }
        }

    };

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.introduction_doc){
//            DialogUtils.showAlergDialog(this, getString(R.string.introduction_title),
//                    getString(R.string.introduction_log_analyser));
//        }else if(item.getItemId() == R.id.log_viewer){
//            Intent intent = new Intent(this, LogViewActivity.class);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mParseDir == null || mParseDir.equals("")) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = pref.edit();
        editor.putString("log_dir", mParseDir);
        editor.commit();
    }

    public void showChooseLogDialog() {
        final String[] files = FileUtils.listFolders(AppConfig.PATH_SD_LOG);
        Log.i(TAG, "files.LEN=" + (files == null ? 0 : files.length));

        new AlertDialog.Builder(this)
                .setTitle("选择目标Log (/sdcard/Log)")
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
                        Log.i(TAG, "selectedFileIndex=" + selectedFileIndex);
                        if (files == null || files.length < 1) {
                            return;
                        }
                        mParseDir = AppConfig.PATH_SD_LOG + "/" + files[selectedFileIndex];

                        if (FileUtils.checkPath(mParseDir + "/aplog")) {
                            mParseDir += "/aplog";
                        } else if (FileUtils.checkPath(mParseDir + "/curlog")) {
                            mParseDir += "/curlog";
                        }

                        Log.d(TAG, "mParseDir:" + mParseDir);
                        runParse(mParseDir);
                    }
                }).show();
    }

    private void runParse(String logDir) {
        mNumberBar.setVisibility(View.VISIBLE);
        final String dir = logDir;
        mExecutorService.execute(new Runnable() {

            @Override
            public void run() {
                int rnt = parseLog(dir);
                if (rnt == 0) {
                    mHandler.sendEmptyMessage(4);
                } else {
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
                convertView = getLayoutInflater().inflate(R.layout.common_list_items, parent, false);
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
                Log.d(TAG, "isChecked=" + isChecked);
                if (isChecked) {
                    checkedPosition = buttonView.getId();
                    if (lastCheckedPosition != -1) {
                        RadioButton tempButton = (RadioButton) findViewById(lastCheckedPosition);
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
