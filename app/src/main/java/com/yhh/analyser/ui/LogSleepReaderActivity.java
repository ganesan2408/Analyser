/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LogSleepReaderActivity extends BaseActivity {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "LogSleepReaderActivity";
    private static final int DOT_PER_SEC = 4;

    private TextView sleeplog;
    private Button sta_button;

    private String path;

    public String logContent_disp = null, logContent_sta = null;
    private int sleepTimes = 0, sleepTime = 0, awakeTime = 0;
    private int logStartTime = 0, logEndtime = 0;
    private StringBuffer mBuffer = new StringBuffer();
    private String logStartTime_str = "";
    private String drawraw = "";
    private int flg = 0;
    public ProgressDialog myDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        path = bundle.getString("logPath");
        Log.i(TAG, "path =" + path);

            setContentView(R.layout.log_sleep_statistics);
            sleeplog = (TextView) findViewById(R.id.loginfo);
            sta_button = (Button) findViewById(R.id.statistics);
            logContent_sta = readFile(path);
            logContent_disp = mBuffer.toString();
            sleeplog.setText(logContent_disp);

            sta_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog = ProgressDialog.show(LogSleepReaderActivity.this,
                            getString(R.string.mydialog_title),
                            getString(R.string.mydialog_body), true);
                    new Thread() {
                        public void run() {
                            while (flg == 0) {
                                if (!myDialog.isShowing()) {
                                    break;
                                }
                            }
                            if (flg == 1) {
                                awakeTime = logEndtime - logStartTime
                                        - sleepTime;
                                Intent intent = new Intent();
                                intent.setClass(LogSleepReaderActivity.this,
                                        LogSleepStatActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("sleepTimes", sleepTimes);
                                bundle.putInt("sleepTime", sleepTime);
                                bundle.putInt("awakeTime", awakeTime);
                                bundle.putString("logStartTime_str",
                                        logStartTime_str);
                                bundle.putString("drawraw", drawraw);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                myDialog.dismiss();
                            }
                        }
                    }.start();
                }
            });
            new Thread() {
                public void run() {
                    drawraw = statistic(logContent_sta);
                    flg = 1;
                }
            }.start();
    }
    
    private String readFile(String filePath) {
        int linenum = 1;
        String line;
        int pos;
        StringBuffer sBuffer = new StringBuffer();
        try {
            FileInputStream fInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fInputStream, "GB2312");
            BufferedReader in = new BufferedReader(inputStreamReader);
            if (!new File(path).exists()) {
                in.close();
                return null;
            }
            while ((line = in.readLine()) != null) {
                if (linenum % 2 == 0) {
                    sBuffer.append(line + "\n" + "\n");
                    pos = findCharInString(line, ']', 1) + 1;
                    mBuffer.append(line.charAt(0)
                            + line.substring(pos, line.length()) + "\n"
                            + "\n");
                } else {
                    sBuffer.append(line + "\n");
                    pos = findCharInString(line, ']', 1) + 1;
                    mBuffer.append(line.charAt(0)
                            + line.substring(pos, line.length()) + "\n");
                }
                linenum++;
            }
            Log.i(TAG, "read linenum:" + linenum);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            sBuffer.append(e.getMessage());
        }
        return sBuffer.toString();
    }

    private int stopflg = 0;

    private String statistic(String logcontent) {
        int len;
        int startTime = 0, endTime = 0, index = 0;
        int elapseTime = 0;
        String drawraw = "";
        len = logcontent.length();
        sleepTimes = 0;
        sleepTime = 0;
        awakeTime = 0;
        for (int i = 0; i < len; i++) {
            if (stopflg == 0) {
                // Log.i("i ="+i+":"+logcontent.charAt(i), "SleeplogViewer");

                if (logcontent.charAt(i) == '>') {
                    startTime = getTime(logcontent, i);
                    // Log.i("startTime ="+startTime+">", "SleeplogViewer");
                    sleepTimes++;
                    if (i == 0) {
                        logStartTime = startTime;
                        // logStartTime_str = logContent_sta.substring(24, 32);
                        logStartTime_str = getStartTimeString(logcontent, i);
                    } else if (startTime - logStartTime < -3600 * 24
                            || startTime - logStartTime > 3600 * 24) {
                        logStartTime = startTime;
                        // logStartTime_str = logContent_sta.substring(24, 32);
                        logStartTime_str = getStartTimeString(logcontent, i);
                        endTime = 0;
                        index = 0;
                        elapseTime = 0;
                        sleepTimes = 0;
                        sleepTime = 0;
                        awakeTime = 0;
                        drawraw = "";
                    }
                    i = findCharInString(logcontent, '\n', i);
                } else if (logcontent.charAt(i) == '<') {
                    endTime = getTime(logcontent, i);
                    sleepTime += endTime - startTime;
                    logEndtime = endTime;
                    elapseTime = startTime - logStartTime;
                    for (int j = index; j < elapseTime / DOT_PER_SEC; j++) {
                        drawraw += '1';
                    }
                    index = elapseTime / DOT_PER_SEC;
                    for (int k = 0; k < (endTime - startTime) / DOT_PER_SEC; k++) {
                        drawraw += '0';
                        index++;
                    }
                    i = findCharInString(logcontent, '\n', i);
                } else if (logcontent.charAt(i) == '^') {
                    sleepTimes--;
                    endTime = getTime(logcontent, i);
                    // Log.i("endTime ="+startTime+" ^", "SleeplogViewer");
                    logEndtime = endTime;
                    elapseTime = startTime - logStartTime;
                    for (int j = index; j < elapseTime / DOT_PER_SEC; j++) {
                        drawraw += '1';
                    }
                    index = elapseTime / DOT_PER_SEC;
                    i = findCharInString(logcontent, '\n', i);
                }
            } else {
                break;
            }
        }
        return drawraw;

    }

    private int getTime(String s, int currentIndex) {
        int startIndex, endIndex;
        String time_s = "";
        int time_i = 0;
        startIndex = findCharInString(s, '[', currentIndex) + 1;
        endIndex = findCharInString(s, ']', currentIndex);
        time_s = s.substring(startIndex, endIndex);
        time_i = Integer.parseInt(time_s);

        return time_i;
    }

    private String getStartTimeString(String s, int currentIndex) {
        int startIndex;
        String time_s = "";

        startIndex = findCharInString(s, ' ', currentIndex) + 1;
        time_s = s.substring(startIndex, startIndex + 8);

        return time_s;
    }

    private int findCharInString(String s, char c, int currentIndex) {
        int i = currentIndex;
        for (; i < s.length(); i++) {
            if (s.charAt(i) == c)
                break;
        }
        return i;
    }

    @Override
    protected void onDestroy() {
        stopflg = 1;
        super.onDestroy();
    }

}
