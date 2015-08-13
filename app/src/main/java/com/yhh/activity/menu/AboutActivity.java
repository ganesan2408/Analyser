package com.yhh.activity.menu;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.activity.BaseActivity;
import com.yhh.analyser.R;
import com.yhh.utils.AppUtils;

public class AboutActivity extends BaseActivity {
    TextView mLogoInfotv;
    RelativeLayout mHelpRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_about);
        initActionBar();
        initView();
        initListener();
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }

}
