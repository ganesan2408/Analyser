package com.yhh.analyser.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yhh.analyser.R;
import com.yhh.analyser.service.RebootService;
import com.yhh.analyser.utils.RootUtils;

public class RebootActivity extends AppCompatActivity {
    private static final int DELAY = 10*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot);

        initView();
    }


    private void initView(){
        Button rebootBtn = (Button) findViewById(R.id.btn_repeat_reboot);
        Button stopBtn = (Button) findViewById(R.id.btn_stop_reboot);
        final EditText timeBtn = (EditText) findViewById(R.id.et_offset_time);

        rebootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUtils.getInstance().setSystemProperty(RootUtils.REBOOT_KEY, "repeat");
                RootUtils.getInstance().setSystemProperty(RootUtils.REBOOT_TIME_KEY,timeBtn.getText().toString());
                Log.i("TAG", RootUtils.getInstance().getSystemProperty(RootUtils.REBOOT_KEY));
                Intent rService = new Intent(RebootActivity.this, RebootService.class);
                startService(rService);

            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUtils.getInstance().setSystemProperty(RootUtils.REBOOT_KEY, "0");
                RootUtils.getInstance().setSystemProperty(RootUtils.REBOOT_TIME_KEY,"3000");
                Log.i("TAG", RootUtils.getInstance().getSystemProperty(RootUtils.REBOOT_KEY));
            }
        });
    }
}
