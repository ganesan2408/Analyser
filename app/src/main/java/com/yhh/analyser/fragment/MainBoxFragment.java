/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yhh.analyser.ui.LogAnalyActivity;
import com.yhh.analyser.ui.WakeupActivity;
import com.yhh.analyser.R;
import com.yhh.analyser.ui.BenchmarkActivity;
import com.yhh.analyser.ui.LogViewActivity;
import com.yhh.analyser.ui.AutoActivity;
import com.yhh.analyser.ui.KernelActivity;
import com.yhh.analyser.ui.MoreActivity;
import com.yhh.analyser.ui.NodeViewActivity;
import com.yhh.analyser.ui.BrightnessActivity;
import com.yhh.analyser.utils.ConstUtils;

public class MainBoxFragment extends Fragment{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "MainBoxFragment";
    private Context mContext;
    
    private int[] viewIds= new int[]{
            R.id.tool_node_rl,
            R.id.tool_bright_rl,
            R.id.tool_wakeup_rl,
            R.id.tool_kernel_rl,
            
            R.id.tool_logviewer_rl,
            R.id.tool_logparser_rl,
            R.id.tool_auto_rl,
            R.id.tool_score_rl,
            
            R.id.tool_more_rl
    };
    
    private Class[] targetClasses = new Class[]{
            NodeViewActivity.class,
            BrightnessActivity.class,
            WakeupActivity.class,
            KernelActivity.class,
            
            LogViewActivity.class,
            LogAnalyActivity.class,
            AutoActivity.class,
            BenchmarkActivity.class,
            
            MoreActivity.class
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_toolbox, null);
        initListener(v);
        return v;
    }

    public void initListener(View v) {
        int count = viewIds.length;
        for(int i=0;i<count;i++){
            final int index = i;
            v.findViewById(viewIds[index]).setOnClickListener(new View.OnClickListener() {
                        
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, targetClasses[index]);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}