package com.yhh.analyser.view.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.adapter.OnekeyAdapter;
import com.yhh.analyser.bean.app.AppInfo;
import com.yhh.analyser.bean.app.ProcessInfo;
import com.yhh.analyser.config.OneKeyConfig;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.widget.ProgressWheel;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenu;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenuCreator;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenuItem;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenuListView;
import com.yhh.androidutils.AppUtils;
import com.yhh.androidutils.DebugLog;
import com.yhh.androidutils.ScreenUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OneKeySleepActivity extends BaseActivity {

    private LinearLayout mLoopTitle;
    private TextView mSleepBtn;
    private TextView mSleepTitleBtn;
    private ProgressWheel mWheel;

    private ProcessInfo mProceessInfo;
    private List<AppInfo> mRunningApps;

    private int mAppNum;
    private SwipeMenuListView mListView;
    private OnekeyAdapter mOnekeyAdapter;
    private List<String> mAppRunningList;


    private PowerManager mPowerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onekey_sleep);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mProceessInfo = new ProcessInfo();

        initView();

        createSwipeListView();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateAppList();
        mLoopTitle.setVisibility(View.GONE);
    }

    private void initView() {
        mSleepBtn = (TextView) findViewById(R.id.txt_onekey_sleep);
        mSleepTitleBtn = (TextView) findViewById(R.id.txt_sleep_title);
        mWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        mListView = (SwipeMenuListView) findViewById(R.id.smlv_app_list);
        mLoopTitle = (LinearLayout) findViewById(R.id.ll_loop);

        mSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoopTitle.setVisibility(View.VISIBLE);
                new OneKeyTask().execute();
            }
        });
    }


    private void createSwipeListView() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "white" item
                SwipeMenuItem addWhiteItem = new SwipeMenuItem(
                        mContext);
                // set item background
                addWhiteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                addWhiteItem.setWidth(ScreenUtils.dp2px(mContext, 100));
                // set item title
                addWhiteItem.setTitle("加入白名单");
                // set item title font size
                addWhiteItem.setTitleSize(16);
                // set item title font color
                addWhiteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(addWhiteItem);

                // create "close" item
                SwipeMenuItem closeItem = new SwipeMenuItem(mContext);
                // set item background
                closeItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                closeItem.setWidth(ScreenUtils.dp2px(mContext, 90));
                // set item title
                closeItem.setTitle("关闭");
                // set item title font size
                closeItem.setTitleSize(16);
                // set item title font color
                closeItem.setTitleColor(Color.WHITE);
                // set a icon
//                closeItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(closeItem);
            }
        };

        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // 加入白名单
                        addWhiteList(mRunningApps.get(position).getPackageName());
                        break;
                    case 1:
                        // 关闭
                        forceStopApp(mRunningApps.get(position).getPackageName());
                        updateAppList();
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
    }

    private void addWhiteList(String pkgName) {
        try {
            OneKeyConfig.addWhite(pkgName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forceStopApp(String pkgName) {
        try {
            AppUtils.forceStopApp(mContext, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateAppList() {
        initOrUpdateRunnintApp();

        if (mAppNum < 1) {
            return;
        }

        mAppRunningList = new ArrayList<>();
        for (int i = 0; i < mAppNum; i++) {
            mAppRunningList.add(mRunningApps.get(i).getName());
        }

        mOnekeyAdapter = new OnekeyAdapter(mAppRunningList);
        mListView.setAdapter(mOnekeyAdapter);
    }

    private void initOrUpdateRunnintApp() {
        mRunningApps = mProceessInfo.getAllRunningApp(this);
        mAppNum = mRunningApps.size();
        mSleepTitleBtn.setText("" + mAppNum);
        Toast.makeText(this,"App num="+mAppNum,Toast.LENGTH_SHORT).show();
    }


    public void startSleep() {
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
            ArrayList<String> whiteList = OneKeyConfig.getWhiteList();
            String pkgName;
            for (int i = 0; i < mAppNum; i++) {
                pkgName = mRunningApps.get(i).getPackageName();
                //过滤白名单的应用程序
                if (whiteList.contains(pkgName)) {
                    continue;
                }
                DebugLog.d("==>" + pkgName);
                forceStopApp(pkgName);
                //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                publishProgress(i);
            }
//            mSecondRunningApps = mProceessInfo.getAllRunningApp(mContext);
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
                startSleep();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_onekey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            createShortCut();
            return true;
        }else if (item.getItemId() == R.id.item_white_app) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createShortCut() {
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // add name
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.txt_onekey_sleep));
        // add intent
        ComponentName comp = new ComponentName(this.getPackageName(), ".view.activity.OneKeySleepActivity");
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
