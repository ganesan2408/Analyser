package com.yhh.analyser;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yhh.analyser.model.app.PhoneInfo;
import com.yhh.analyser.utils.DialogUtils;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.view.activity.AboutActivity;
import com.yhh.analyser.view.activity.FeedbackActivity;
import com.yhh.analyser.view.activity.ShotActivity;
import com.yhh.analyser.view.fragment.MainBoxFragment;
import com.yhh.analyser.view.fragment.MainMonitorFragment;
import com.yhh.analyser.view.fragment.MainPerfFragment;
import com.yhh.analyser.view.fragment.MainPowerFragment;
import com.yhh.analyser.widget.slidingmenu.SlidingMenu;
import com.yhh.androidutils.PreferencesUtils;

public class Main extends BaseActivity {

    private FragmentTabHost mTabHost;
    private RadioGroup mTabRg;
    private Long mExitTime = (long) 0;

    private final Class[] mFragments = {
            MainMonitorFragment.class,
            MainPerfFragment.class,
            MainPowerFragment.class,
            MainBoxFragment.class
    };

    private final int[] mRadioIds = {
            R.id.tab_rb_monitor,
            R.id.tab_rb_perf,
            R.id.tab_rb_power,
            R.id.tab_rb_box
    };

    private SlidingMenu menuMenu;

    @Override
    protected boolean hasMenuButton() {
        return true;
    }

    @Override
    protected void onActionBarMenuClick() {
        super.onActionBarMenuClick();
        menuMenu.toggle();
    }

    @Override
    protected boolean hasBackButton() {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_main);

        initView();
        initMenu();
        setDefaultView();

        firstCreateShortCut();

        MobclickAgent.updateOnlineConfig(this);  //更新在线配置

        UmengUpdateAgent.update(this); //更新apk

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initView() {
        //设置tab host
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, this.getSupportFragmentManager(), R.id.realtabcontent);
        final int count = mFragments.length;
        for (int i = 0; i < count; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
            mTabHost.addTab(tabSpec, mFragments[i], null);
        }

        //设置切换按钮
        mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
        mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < count; i++) {
                    if (mRadioIds[i] == checkedId) {
                        mTabHost.setCurrentTab(i);
                        break;
                    }
                }
            }

        });

    }

    //设置默认选中的Tab
    private void setDefaultView() {
        mTabHost.setCurrentTab(0);
    }

    /**
     * 初始化 左拉菜单
     */
    private void initMenu() {
        menuMenu = new SlidingMenu(Main.this);
        menuMenu.setMode(SlidingMenu.LEFT);
        menuMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// 设置触摸屏幕的模式
        menuMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 设置滑动菜单视图的宽度
        menuMenu.setFadeDegree(0.9f);  // 设置渐入渐出效果的值 
        menuMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menuMenu.setMenu(R.layout.menu);
        menuMenu.setTouchmodeMarginThreshold(64);

        //设置手机型号
        ((TextView) findViewById(R.id.phone_model)).setText(PhoneInfo.getPhoneType());
        menuMenu.setBackgroundColor(0xe5e5e5);


        menuMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                mActionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
            }
        });

        menuMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                mActionBar.setHomeAsUpIndicator(R.drawable.actionbar_menu);
            }
        });

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.quite_alert, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void menuClick(View v) {
        switch (v.getId()) {
            case R.id.mmenu_device_info:
                DialogUtils.showAlergDialog(this, getString(R.string.about_phone),
                        (new PhoneInfo()).toString());
                break;

            case R.id.mmenu_create_icon:
                createShortCutDialog();
                break;

            case R.id.mmenu_my_shot:
                Intent shotIntent = new Intent(this, ShotActivity.class);
                startActivity(shotIntent);
                break;

            case R.id.mmenu_my_feedback:
                Intent fbIntent = new Intent(this, FeedbackActivity.class);
                startActivity(fbIntent);
                break;

            case R.id.mmenu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
        }
    }

    private void createShortCut() {
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // add name
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        // add intent
        ComponentName comp = new ComponentName(this.getPackageName(), "." + this.getLocalClassName());
        Intent mainIntent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
        // add icon
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.logo1);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // only one
        shortCutIntent.putExtra("duplicate", false);

        sendBroadcast(shortCutIntent);
    }

    private void createShortCutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_icon)
                .setMessage(R.string.create_icon_msg)
                .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        createShortCut();
                    }
                })
                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

    private void firstCreateShortCut() {
        boolean firstStart = PreferencesUtils.getInstance(this).get("First", true);
        if (firstStart) {
            createShortCutDialog();
            PreferencesUtils.getInstance(this).put("First", false);
        }
    }
}
