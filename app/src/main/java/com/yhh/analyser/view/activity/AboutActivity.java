package com.yhh.analyser.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.utils.AppUtils;

public class AboutActivity extends BaseActivity {
    TextView mLogoInfotv;
    RelativeLayout mHelpRl;

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
        mLogoInfotv = (TextView) findViewById(R.id.tv_about_logo_name);
        mLogoInfotv.setText(mContext.getText(R.string.app_name) + " "+ AppUtils.getAppVersionName(this));

        mHelpRl = (RelativeLayout)findViewById(R.id.about_help);
        mHelpRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpIntent = new Intent(AboutActivity.this, HelpActivity.class);
                startActivity(helpIntent);
            }
        });
    }

    private void initListener(){
        findViewById(R.id.about_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this,"已是最新版本", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
