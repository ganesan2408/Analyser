/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseFragment;
import com.yhh.androidutils.DebugLog;
import com.yhh.androidutils.ShellUtils;
import com.yhh.androidutils.StringUtils;

public class PowerLockFragment extends BaseFragment {
    private TextView mLockTv;
    private Button mRefreshBtn;

    private static final String CMD_POWER_STATUS = "cat /sys/private/pm_status";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.power_lock, null);
        mLockTv = (TextView) view.findViewById(R.id.tv_power_lock);
        mRefreshBtn = (Button) view.findViewById(R.id.btn_lock_refresh);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        work();
    }

    private void work() {
        String rawInfo = ShellUtils.execCommand(CMD_POWER_STATUS).successMsg;
        if (StringUtils.isBlank(rawInfo)) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        String[] rawInfoArr = rawInfo.split("\\n");
        for (String line : rawInfoArr) {
            DebugLog.d(line);
            if(line.contains("active")){
             sb.append(line).append("\n");
            }
        }
        mLockTv.setText(sb.toString());
    }

}
