package com.yhh.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.app.monitor.SingleAppMonitor;
import com.yhh.info.app.AppInfo;
import com.yhh.info.app.ProcessInfo;
import com.yhh.utils.ConstUtils;
import com.yhh.widget.letterlistview.CharacterParser;
import com.yhh.widget.letterlistview.ClearEditText;
import com.yhh.widget.letterlistview.PinyinComparator;
import com.yhh.widget.letterlistview.SideBar;
import com.yhh.widget.letterlistview.SideBar.OnTouchingLetterChangedListener;
import com.yhh.widget.letterlistview.SortAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonitorFragment extends Fragment {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "MonitorFragment";
    
    private Context mContext;
    private static List<AppInfo> mAppList;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        mContext = this.getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        View v = inflater.inflate(R.layout.main_monitor, null) ;
        initViews(v);
        return v;
    }
	

	private void initViews(View v) {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) v.findViewById(R.id.sidrbar);
		dialog = (TextView) v.findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});
		
		sortListView = (ListView) v.findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    Intent intent = new Intent(mContext, SingleAppMonitor.class);
			    intent.putExtra("appName", ((AppInfo)adapter.getItem(position)).getName());
			    intent.putExtra("packageName", ((AppInfo)adapter.getItem(position)).getPackageName());
			    startActivity(intent);
			}
		});
		
		mClearEditText = (ClearEditText) v.findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
		
		if(null == mAppList){
		    Log.i(TAG,"mAppList == NULL");
		    new Thread(new Runnable(){

                @Override
                public void run() {
                    mAppList = new ProcessInfo().getLaunchApps(mContext);
                    mHandler.sendMessage(mHandler.obtainMessage(0));
                }
		        
		    }).start();
           
        }else{
            mHandler.sendMessage(mHandler.obtainMessage(1));
        }
		
	}
	
	private Handler mHandler = new Handler(){
	    public void handleMessage(Message msg) {
			if(msg.what ==0) {
				filledData();
			}
	        // 根据a-z进行排序源数据
	        Collections.sort(mAppList, pinyinComparator);
	        adapter = new SortAdapter(mContext, mAppList);
	        sortListView.setAdapter(adapter);
	        
	    }
	};

	/**
	 * 为ListView填充数据
	 */
	private void filledData(){
		int appsNum = mAppList.size();
		for(int i=0; i<appsNum; i++){
			
		    //汉字转换成拼音
            String pinyin = characterParser.getSelling(mAppList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                mAppList.get(i).setFirstLetter(sortString.toUpperCase());
            }else{
                mAppList.get(i).setFirstLetter("#");
            }
		}
		
		AppInfo specailModel = new AppInfo();
        specailModel.setName("系统监控");
        specailModel.setPackageName("0");
        specailModel.setLogo(mContext.getResources().getDrawable(R.drawable.logo1));
        specailModel.setFirstLetter("☆");
		mAppList.add(specailModel);
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<AppInfo> filterDateList = new ArrayList<AppInfo>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = mAppList;
		}else{
			filterDateList.clear();
			for(AppInfo appInfo : mAppList){
				String name = appInfo.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(appInfo);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
}
