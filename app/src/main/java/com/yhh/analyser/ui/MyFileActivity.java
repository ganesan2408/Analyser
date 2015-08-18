package com.yhh.analyser.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.DensityUtils;
import com.yhh.analyser.utils.FileUtils;

import java.util.Arrays;
import java.util.List;

public class MyFileActivity extends Activity {
    private final static String TAG = ConstUtils.DEBUG_TAG + "MyFileActivity";
    private List<String> mShotList;
    private ShotAdapter mAdapter;
    private SwipeMenuListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_myshot);
        initActionBar();

        createSwipeListView();
    }

    private void createSwipeListView(){
        ((TextView)findViewById(R.id.title_screen)).setText(R.string.monitor_file_path);
        reflash();
        mListView = (SwipeMenuListView) findViewById(R.id.shot_swipe_ll);
        if(mShotList !=null) {
            mAdapter = new ShotAdapter();
            mListView.setAdapter(mAdapter);
        }
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(DensityUtils.dip2px(MyFileActivity.this, 90));
                // set item title
                openItem.setTitle("解析");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DensityUtils.dip2px(MyFileActivity.this, 90));
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
                        mAdapter.notifyDataSetChanged();
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

        // test item long click
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(getApplicationContext(), position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void reflash(){
        mShotList = Arrays.asList(FileUtils.listFiles(AppConfig.MONITOR_DIR));
    }




    class ShotAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mShotList==null?0:mShotList.size();
        }

        @Override
        public Object getItem(int position) {
            return mShotList==null?null:mShotList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView ==null){
                convertView = View.inflate(getApplicationContext(),R.layout.item_list_screenshot, null);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder == null){
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.shot_iv = (ImageView) convertView.findViewById(R.id.shotitem_icon_iv);
                holder.name_tv = (TextView) convertView.findViewById(R.id.shotitem_name_tv);
            }
            String name = (String) getItem(position);
            holder.shot_iv.setImageResource(R.drawable.file_logo);
            holder.name_tv.setText(name);
            return convertView;
        }

        class ViewHolder{
            ImageView shot_iv;
            TextView name_tv;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }

    private void analyticFile(String path){
        Intent intent = new Intent(this, ChartMonitorActivity.class);
        intent.putExtra(MonitorAppActivity.MONITOR_PATH, path);
        startActivity(intent);
    }

    private void deleteMonitorFile(String path){
        FileUtils.deleteFile(AppConfig.MONITOR_DIR + path);
        reflash();
    }


}
