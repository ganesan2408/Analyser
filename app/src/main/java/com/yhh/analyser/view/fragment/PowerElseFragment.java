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
import com.yhh.androidutils.ShellUtils;
import com.yhh.androidutils.StringUtils;

public class PowerElseFragment extends BaseFragment {
    private TextView mVddminTv;

    private static final String CMD_VDDMIN = "cat /sys/kernel/debug/rpm_stats";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.power_else, null);
        mVddminTv = (TextView) view.findViewById(R.id.tv_vddmin);

        Button mRefreshBtn = (Button) view.findViewById(R.id.btn_refresh);
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

    private void work(){
        String result = ShellUtils.execCommand(CMD_VDDMIN).successMsg;
        if(!StringUtils.isBlank(result)) {
            mVddminTv.setText(result);
        }
    }

}
