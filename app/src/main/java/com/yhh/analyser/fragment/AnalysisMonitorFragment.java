/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.yhh.analyser.R;
import com.yhh.analyser.adapter.MonitorFileAdapter;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.ui.ChartMonitorActivity;
import com.yhh.analyser.ui.MonitorAppActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DensityUtils;
import com.yhh.analyser.utils.FileUtils;

import java.util.Arrays;
import java.util.List;

public class AnalysisMonitorFragment extends BaseFragment {
    
    private static final String TAG = ConstUtils.DEBUG_TAG+ "AnalysisMonitor";

    private List<String> mShotList;
    private MonitorFileAdapter mAdapter;
    private SwipeMenuListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_data, null);
        createSwipeListView(v);
        return v;
    }

    private void createSwipeListView(View v){
        String[] fileArray = FileUtils.listFiles(AppConfig.MONITOR_DIR);
        if(fileArray ==null){
            return;
        }

        mShotList = Arrays.asList(fileArray);
        mListView = (SwipeMenuListView) v.findViewById(R.id.shot_swipe_ll);
        if(mShotList !=null) {
            mAdapter = new MonitorFileAdapter(mShotList);
            mListView.setAdapter(mAdapter);
        }
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        mContext);
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(DensityUtils.dip2px(mContext, 90));
                // set item title
                openItem.setTitle("解析");
                // set item title font size
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DensityUtils.dip2px(mContext, 90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        analyticFile(mShotList.get(position));
                        break;
                    case 1:
                        // delete
                        deleteMonitorFile(mShotList.get(position));
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                analyticFile(mShotList.get(position));
            }
        });
//        // test item long click
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                           int position, long id) {
//                Toast.makeText(mContext, position + " long click", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
    }

    private void refresh(){
        String[] array =  FileUtils.listFiles(AppConfig.MONITOR_DIR);
        mShotList = array ==null? null: Arrays.asList(array);
        mAdapter = new MonitorFileAdapter(mShotList);
        mListView.setAdapter(mAdapter);

    }

    private void analyticFile(String path){
        Intent intent = new Intent(mContext, ChartMonitorActivity.class);
        intent.putExtra(MonitorAppActivity.MONITOR_PATH, path);
        startActivity(intent);
    }

    private void deleteMonitorFile(String path){
        FileUtils.deleteFile(AppConfig.MONITOR_DIR + path);
        refresh();
    }
    
}