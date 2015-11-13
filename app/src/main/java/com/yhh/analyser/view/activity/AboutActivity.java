package com.yhh.analyser.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;
import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.androidutils.AppUtils;
import com.yhh.androidutils.DebugLog;

public class AboutActivity extends BaseActivity {
    RelativeLayout mAboutRl;
    RelativeLayout mTdcRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_about);
        initView();
        initListener();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private void initView(){
        TextView mLogoInfotv = (TextView) findViewById(R.id.tv_about_logo_name);
        mAboutRl = (RelativeLayout) findViewById(R.id.rl_about_main);
        mTdcRl = (RelativeLayout) findViewById(R.id.rl_tdc);

        String logoInfo = mContext.getText(R.string.app_name) + " "+ AppUtils.getAppVersionName(this);
        mLogoInfotv.setText(logoInfo);
    }

    private void initListener(){
        findViewById(R.id.about_two_dimension_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        findViewById(R.id.about_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUpdateAgent.update(AboutActivity.this); //更新umeng
                DebugLog.d("update version.");
            }
        });

        findViewById(R.id.about_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpIntent = new Intent(AboutActivity.this, HelpActivity.class);
                startActivity(helpIntent);
            }
        });

        mTdcRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage();
            }
        });

    }

    private void showImage() {
        mTdcRl.setVisibility(View.VISIBLE);
        mAboutRl.setVisibility(View.GONE);
    }

    private void hideImage(){
        mTdcRl.setVisibility(View.GONE);
        mAboutRl.setVisibility(View.VISIBLE);
    }

}
