/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import com.yhh.analyser.R;
import com.yhh.analyser.ui.base.BaseActivity;

public abstract class ChartBaseActivity extends BaseActivity {
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
