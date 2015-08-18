/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.support.v4.app.FragmentActivity;

import com.yhh.analyser.R;

public abstract class ChartBaseActivity extends FragmentActivity {
    public String mLogType;
    //abondan
    public int mDataType;
    public int mChartType;
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
