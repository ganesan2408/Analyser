/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.terminal.Term;
import com.yhh.analyser.utils.AppUtils;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreActivity extends Activity{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "MainBoxFragment";
    
    private final String[] mItemName = new String[]{
            "CPU压测", "IO压测", "VibratorTool", "Wakelock",
            "Terminal", "虚位", "虚位" , "虚位"
    };
    
    private final String[] mItemDescription = new String[]{
            "CPU压测", "IO压测", "震动调试", "休眠与唤醒锁",
            "命令行终端模拟器", "敬请期待", "敬请期待", "敬请期待"
    };
    
    private final int[] mItemImage = {
            R.drawable.logo1
    };
    
    private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
    private SimpleAdapter mAdapter;
    private GridView mGridView;
    private Context mContext;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbox_more);
        mContext = MoreActivity.this;
        initActionBar();
        getData();
        initView();
    }
    
    private void initView(){
        String[] from = {"image","text","description"};
        int[] to = {R.id.image, R.id.text, R.id.description};
        mAdapter = new SimpleAdapter(this, mDataList, R.layout.main_toolbox_item, from, to);
        
        mGridView = (GridView) findViewById(R.id.more_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position == 4){
                    Intent intent = new Intent(mContext, Term.class);
                    mContext.startActivity(intent);
                }else{
                    startApp(position);
                }
            }
            
        });
    }

    public List<Map<String, Object>> getData(){        
        for(int i=0;i<mItemName.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", mItemImage[0]);
            map.put("text", mItemName[i]);
            map.put("description", mItemDescription[i]);
            mDataList.add(map);
        }
        return mDataList;
    }
    
    
    public void runApp( int rawId, String AppName, String pkgName, String ActName){
        if(AppUtils.isAppInstalled(mContext, pkgName)){
            //启动apk
            AppUtils.startApp(mContext, pkgName, ActName);
        }else{
            String path = Environment.getExternalStorageDirectory().toString() + "/systemAnalyzer/"+AppName+".apk";
            //资源拷贝
            FileUtils.copyRaw2Local(this, rawId, path);
            //启动安装过程
            AppUtils.installApp(this, path);
        }
    }
    
    public void startApp(int index){
        String AppName;
        String pkgName;
        String ActName ;
        int rawId;
        
        switch(index){
            case 0:
                rawId = R.raw.cputiger;
                AppName = "cputiger";
                pkgName = "com.tiger.cpu";
                ActName = "com.tiger.cpu.Main";
                runApp(rawId, AppName, pkgName, ActName);
                break;
                
            case 1:
                rawId = R.raw.iotiger;
                AppName = "iotiger";
                pkgName = "com.tiger.io";
                ActName = "com.tiger.io.IOActivity";
                runApp(rawId, AppName, pkgName, ActName);
                break;
                
            case 2:
                rawId = R.raw.vibrator_tool;
                AppName = "vibrator_tool";
                pkgName = "com.lenovo.vibratortool";
                ActName = "com.lenovo.vibratortool.MainActivity";
                runApp(rawId, AppName, pkgName, ActName);
                break;
                
            case 3:
                rawId = R.raw.wakelock_tool;
                AppName = "wakelock_tool";
                pkgName = "com.lenovo.wakelocktools";
                ActName = "com.lenovo.wakelocktools.MainActivity";
                runApp(rawId, AppName, pkgName, ActName);
                break;
                
            default:
                Toast.makeText(mContext, "正在设计筹划中，敬请期待！", Toast.LENGTH_SHORT).show();
                break;
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
}