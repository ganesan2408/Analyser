package com.yhh.analyser.view.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;

/**
 * Created by yuanhh1 on 2015/9/24.
 */
public class SyncTimeActivity extends BaseActivity  {
    private Button syncBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_time);
        initView();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private void initView(){
        final EditText offsetEt = (EditText) findViewById(R.id.et_offset);
        syncBtn = (Button) findViewById(R.id.btn_sync);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long offsetMs = 0;
                try {
                    offsetMs = Long.parseLong(offsetEt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (offsetMs == 0) {
                    Toast.makeText(SyncTimeActivity.this, "please input offset time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SystemClock.setCurrentTimeMillis(System.currentTimeMillis() + offsetMs);
                offsetEt.setText("");
                Toast.makeText(SyncTimeActivity.this, "sync success.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
