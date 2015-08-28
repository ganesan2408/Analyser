package com.yhh.analyser.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.bean.app.AppInfo;
import com.yhh.analyser.bean.app.ProcessInfo;
import com.yhh.analyser.ui.base.BaseActivity;
import com.yhh.analyser.utils.AppUtils;
import com.yhh.analyser.utils.DebugLog;
import com.yhh.analyser.widget.ProgressWheel;

import java.util.List;

public class OneKeySleepActivity extends BaseActivity {

    private TextView mSleepBtn;
    private TextView mSleepTitleBtn;
    private ProgressWheel mWheel;

    private ProcessInfo mProceessInfo;
    private List<AppInfo> mRunningApps;
    private List<AppInfo> mSecondRunningApps;

    private int mAppNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onekey_sleep);
        initView();
        work();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    private void initView() {
        mSleepBtn = (TextView) findViewById(R.id.txt_onekey_sleep);
        mSleepTitleBtn = (TextView) findViewById(R.id.txt_sleep_title);
        mWheel = (ProgressWheel) findViewById(R.id.progress_wheel);

        mSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OneKeyTask().execute();
            }
        });
    }

    private void work() {
        mProceessInfo = new ProcessInfo();
        mRunningApps = mProceessInfo.getAllRunningApp(this);
        mAppNum = mRunningApps.size();
        mSleepTitleBtn.setText("" + mAppNum);
    }

    private class OneKeyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            mSleepBtn.setText("正在优化");
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < mAppNum; i++) {
                try {

                    DebugLog.d("==>"+mRunningApps.get(i).getPackageName());
                    Thread.sleep(30);
                    AppUtils.forceStopApp(mContext, mRunningApps.get(i).getPackageName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                publishProgress(i);
            }
            mSecondRunningApps = mProceessInfo.getAllRunningApp(mContext);
            publishProgress(mAppNum);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(values[0] <mAppNum) {

                float progress = 100 * (values[0] + 1) / mAppNum;
                mWheel.setProgress((int) (3.6 * progress));
                mWheel.setText(mRunningApps.get(values[0]).getName());

                mSleepTitleBtn.setText((values[0] + 1) + "/" + mAppNum);
            }else {
                mRunningApps = mSecondRunningApps;
                mAppNum = mSecondRunningApps.size();
                mWheel.setText("Complete");
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mSleepBtn.setText(R.string.txt_onekey_sleep);
            mSleepTitleBtn.setText("" + mAppNum);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
