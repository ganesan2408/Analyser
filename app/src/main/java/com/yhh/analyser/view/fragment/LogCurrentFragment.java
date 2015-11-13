/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.widget.IndexableListView;
import com.yhh.analyser.view.activity.LogReaderActivity;
import com.yhh.androidutils.ArrayUtils;
import com.yhh.androidutils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogCurrentFragment extends Fragment {
    private static final String TAG =  LogUtils.DEBUG_TAG+ "CurrentLogFragment";
    
    private IndexableListView mLogLv;
    private List<String> mLogNameList = null;
    
    private Context mcontext;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext = this.getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_current_fragment, container, false);  
        mLogLv = (IndexableListView) view.findViewById(R.id.log_current_lv);
        mLogNameList = ArrayUtils.toList(FileUtils.listFolderOrFile(LogUtils.LOG_DIR));
        if(mLogNameList != null && mLogNameList.size() >0){
        Collections.sort(mLogNameList);
        IndexableListView.ContentAdapter adapter = mLogLv.new ContentAdapter(this.getActivity(),
                android.R.layout.simple_list_item_1, mLogNameList);
        mLogLv.setAdapter(adapter);
        mLogLv.setFastScrollEnabled(true);
        
        mLogLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        Intent  intent = new Intent(mcontext, LogReaderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("logPath", LogUtils.LOG_DIR + "/" + mLogNameList.get(arg2));
                        intent.putExtras(bundle);
                        mcontext.startActivity(intent);
                    }
                });
        }else{
            Log.i(TAG,"Not found Log");
        }
        return view;
    }
    
    /**
     *  过滤log, 显示满足要求的log
     * @param filePath
     */
    private List<String> filterLog(String filePath) {
        List<String> mLogNameList = new ArrayList<String>();
        File f = new File(filePath);
        File[] files = f.listFiles();
        if(files ==null){
            Log.e(TAG,"filePath="+filePath+" is not exist");
            return null;
        }
        
        for (int i = 0; i < files.length; i++) {
            String tmpName = files[i].getName();
            for (String log : LogUtils.LOG_ALL) {
                if (tmpName.startsWith(log)) {
                    mLogNameList.add(tmpName);
                }
            }
        }
        Collections.sort(mLogNameList);
        return mLogNameList;
    }
}
