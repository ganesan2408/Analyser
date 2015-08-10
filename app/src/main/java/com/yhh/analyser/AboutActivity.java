package com.yhh.analyser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.utils.ConstUtils;

public class AboutActivity extends Activity {
    TextView mVersionTv;
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
        mVersionTv = (TextView) findViewById(R.id.about_version);
        mVersionTv.setText(getVersion());
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

    private String getVersion(){
        String version=null;
        try {
            version = " V"+ getPackageManager().getPackageInfo(ConstUtils.MY_PACKAGE_NAME, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
