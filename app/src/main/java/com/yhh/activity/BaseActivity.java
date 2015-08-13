package com.yhh.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by yuanhh1 on 2015/8/13.
 */
public class BaseActivity extends Activity{
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }

//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.move_right_in_activity,R.anim.move_right_out_activity);
//    }
//
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.move_right_in_activity,R.anim.move_right_out_activity);
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.move_left_in_activity,R.anim.move_left_out_activity);
//    }
}
