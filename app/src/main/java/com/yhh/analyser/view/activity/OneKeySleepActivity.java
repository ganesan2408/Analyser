package com.yhh.analyser.view.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.bean.app.AppInfo;
import com.yhh.analyser.bean.app.ProcessInfo;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.utils.AppUtils;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DebugLog;
import com.yhh.analyser.widget.ProgressWheel;

import java.lang.reflect.Method;
import java.util.List;

public class OneKeySleepActivity extends BaseActivity {

    private TextView mSleepBtn;
    private TextView mTipsTv;
    private TextView mSleepTitleBtn;
    private ProgressWheel mWheel;

    private ProcessInfo mProceessInfo;
    private List<AppInfo> mRunningApps;
    private List<AppInfo> mSecondRunningApps;

    private int mAppNum;

    private PowerManager mPowerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onekey_sleep);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        initView();
        work();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    private void initView() {
        mSleepBtn = (TextView) findViewById(R.id.txt_onekey_sleep);
        mTipsTv = (TextView) findViewById(R.id.tv_info_tips);
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


    public void startSleep(){
        try {
            Class<?> clazz = Class.forName("android.os.PowerManager");
            Method method = clazz.getMethod("goToSleep", long.class);
            method.invoke(mPowerManager, SystemClock.uptimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    DebugLog.d("==>" + mRunningApps.get(i).getPackageName());
                    Thread.sleep(5);
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
            if (values[0] < mAppNum) {

                float progress = 100 * (values[0] + 1) / mAppNum;
                mWheel.setProgress((int) (3.6 * progress));
                mWheel.setText(mRunningApps.get(values[0]).getName());

                mSleepTitleBtn.setText((values[0] + 1) + "/" + mAppNum);
            } else {
                mRunningApps = mSecondRunningApps;
                mAppNum = mSecondRunningApps.size();
                mWheel.setText("Complete");
                startSleep();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            mSleepBtn.setText(R.string.txt_onekey_sleep);
            mSleepTitleBtn.setText("" + mAppNum);

            Log.i(ConstUtils.DEBUG_TAG, "startSleep");

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onekey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            createShortCut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createShortCut() {
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // add name
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.txt_onekey_sleep));
        // add intent
        ComponentName comp = new ComponentName(this.getPackageName(), ".ui.OneKeySleepActivity");
        Intent mainIntent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
        // add icon
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.logo_onekey);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // only one
        shortCutIntent.putExtra("duplicate", false);

        sendBroadcast(shortCutIntent);
    }

}
