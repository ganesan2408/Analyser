/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.afragment.performance;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;

public class FpsGovernor extends Fragment{
    private static final String TAG = ConstUtils.DEBUG_TAG +"FpsGovernor";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.performance_fps, container, false);  
        return view;
    }
    
    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy.");
        super.onDestroy();
    }
}
