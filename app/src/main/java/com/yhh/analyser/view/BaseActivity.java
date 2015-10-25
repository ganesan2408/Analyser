package com.yhh.analyser.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;

import com.yhh.analyser.R;
import com.yhh.androidutils.StringUtils;

/**
 * Created by yuanhh1 on 2015/8/13.
 */
public class BaseActivity extends AppCompatActivity{
    protected Context mContext;
    protected ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        mActionBar = getSupportActionBar();

        if (hasActionBar()) {
            initActionBar(mActionBar);
        }else{
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }


    protected boolean hasActionBar() {
        return true;
    }

    /**
     * 返回按钮
     *
     * @return
     */
    protected boolean hasBackButton() {
        return true;
    }

    protected void onActionBarBackClick(){
        onBackPressed();
    }

    /**
     * 菜单按钮
     *
     * @return
     */
    protected boolean hasMenuButton(){
        return false;
    }

    protected void onActionBarMenuClick(){

    }

    /** 获取标题栏的名字*/
    protected int getActionBarTitle() {
        return R.string.app_name;
    }


    /**设置标题栏 名字*/
    public void setActionBarTitle(int resId) {
        if (resId != 0) {
            setActionBarTitle(getString(resId));
        }
    }

    /**设置标题栏 名字*/
    public void setActionBarTitle(String title) {
        if (StringUtils.isNull(title)) {
            title = getString(R.string.app_name);
        }
        if (hasActionBar() && mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }


    private void initActionBar(ActionBar actionBar) {
        if (actionBar == null)
            return;

        if (hasBackButton()) {
            actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }else if(hasMenuButton()){
            actionBar.setHomeAsUpIndicator(R.drawable.actionbar_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }else {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setDisplayUseLogoEnabled(false);
            int titleRes = getActionBarTitle();
            if (titleRes != 0) {
                actionBar.setTitle(titleRes);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(hasBackButton()){
                onActionBarBackClick();
            }else if(hasMenuButton()){
                onActionBarMenuClick();
            }

        }
        return super.onOptionsItemSelected(item);
    }


}
