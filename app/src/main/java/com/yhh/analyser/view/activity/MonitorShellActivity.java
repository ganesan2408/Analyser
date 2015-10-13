/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.core.MonitorFactory;
import com.yhh.analyser.service.MonitorService;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.MyFileUtils;
import com.yhh.analyser.utils.StringUtils;
import com.yhh.analyser.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MonitorShellActivity extends BaseActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG + "mshell";
    private boolean DEBUG = true;

    private Button mMonitorBtn;
    private Button mStoreupBtn;

    private EditText mCmdEdit;
    private ListView mShellLv;


    private List<String> mShellList;

    public static String sCommand;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_monitor_shell_settings);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initUI() {
        mMonitorBtn = (Button) findViewById(R.id.btn_senior_monitor);
        mStoreupBtn = (Button) findViewById(R.id.btn_store_up);
        mMonitorBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sCommand = mCmdEdit.getText().toString();

                if (StringUtils.isBlank(sCommand)) {
                    Toast.makeText(MonitorShellActivity.this, "adb指令不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent monitorService = new Intent();
                monitorService.setClass(mContext, MonitorService.class);
                monitorService.putExtra("type", MonitorFactory.TYPE_SHELL);
                monitorService.putExtra("command", sCommand);
                monitorService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startService(monitorService);
                finish();
            }

        });

        mStoreupBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateData();
            }
        });


        mCmdEdit = (EditText) findViewById(R.id.shell_command);

        mShellLv = (ListView) findViewById(R.id.lv_shell);
        mShellList = getData();
        if (mShellList == null) {
            mShellList = new ArrayList<>();
        }

        mShellLv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mShellList));
        mShellLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCmdEdit.setText(mShellList.get(position));
            }
        });

//        mShellLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                return false;
//            }
//        });

    }

    private List<String> getData() {
        return MyFileUtils.readFile2List(AppConfig.ADB_SHELL_FILE);
    }

    private void updateData() {
        String newCmd = mCmdEdit.getText().toString();
        if (StringUtils.isBlank(newCmd) || mShellList.contains(newCmd)) {
            return;
        }

        mShellList.add(newCmd);
        MyFileUtils.writeFile(AppConfig.ADB_SHELL_FILE, MyFileUtils.NEW_LINE + newCmd, true);

        if (mShellList != null) {
            mShellLv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, mShellList));
        }
    }

    private void deleteData(){
        String newCmd = mCmdEdit.getText().toString();
        if (StringUtils.isBlank(newCmd) || mShellList.contains(newCmd)) {
            return;
        }

        mShellList.remove(newCmd);
        MyFileUtils.writeFile(AppConfig.ADB_SHELL_FILE, MyFileUtils.NEW_LINE + newCmd, true);

        if (mShellList != null) {
            mShellLv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, mShellList));
        }
    }

}
