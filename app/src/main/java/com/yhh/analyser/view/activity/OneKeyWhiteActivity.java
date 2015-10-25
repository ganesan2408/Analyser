package com.yhh.analyser.view.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.yhh.analyser.R;
import com.yhh.analyser.adapter.OnekeyAdapter;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.config.OneKeyConfig;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenu;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenuCreator;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenuItem;
import com.yhh.analyser.widget.swipemenulistview.SwipeMenuListView;
import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.ScreenUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OneKeyWhiteActivity extends BaseActivity {


    private SwipeMenuListView mListView;

    private OnekeyAdapter mOnekeyAdapter;
    private List<String> mWhiteList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onekey_sleep);


        initView();

        createSwipeListView();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateAppList();
    }

    private void initView() {

        mListView = (SwipeMenuListView) findViewById(R.id.smlv_white_list);

    }


    private void createSwipeListView() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "white" item
                SwipeMenuItem addWhiteItem = new SwipeMenuItem(
                        mContext);
                // set item background
                addWhiteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                addWhiteItem.setWidth(ScreenUtils.dp2px(mContext, 100));
                // set item title
                addWhiteItem.setTitle("取消白名单");
                // set item title font size
                addWhiteItem.setTitleSize(16);
                // set item title font color
                addWhiteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(addWhiteItem);

                // create "close" item
//                SwipeMenuItem closeItem = new SwipeMenuItem(mContext);
//                // set item background
//                closeItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
//                // set item width
//                closeItem.setWidth(DensityUtils.dip2px(mContext, 90));
//                // set item title
//                closeItem.setTitle("关闭");
//                // set item title font size
//                closeItem.setTitleSize(16);
//                // set item title font color
//                closeItem.setTitleColor(Color.WHITE);
//                // set a icon
//                // add to menu
//                menu.addMenuItem(closeItem);
            }
        };

        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // 取消白名单
                        deleteWhiteList(mWhiteList.get(position));
                        updateAppList();
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
    }

    private void deleteWhiteList(String pkgName) {
        try {
            OneKeyConfig.deleteWhite(pkgName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void updateAppList() {
        mWhiteList = (ArrayList) FileUtils.readFile2List(AppConfig.ONE_KEY_WHITE_LIST);

        mOnekeyAdapter = new OnekeyAdapter(mWhiteList);
        mListView.setAdapter(mOnekeyAdapter);
    }




}
