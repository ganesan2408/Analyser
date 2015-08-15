/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.afragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;

public class MoreDetailActivity extends Fragment{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "ToolBoxFragment";
    private Context mContext;
    
    private final String[] mItemName = new String[]{
            "查看节点", "安兔兔跑分", "亮度调节" ,"Kernel",
            "查看Log", "解析Log", "自动化Case" ,"更多"
    };
    
    private final int[] mItemImage = {
    };
    
    private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
    private SimpleAdapter mAdapter;
    private GridView mGridView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        getData();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_toolbox, null);
        String[] from = {"image","text"};
        int[] to = {R.id.image, R.id.text};
        mAdapter = new SimpleAdapter(mContext, mDataList, R.layout.main_toolbox_item, from, to);
        
        mGridView = (GridView) v.findViewById(R.id.toolbox_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    Toast.makeText(mContext, "position="+position+", id="+id, Toast.LENGTH_SHORT).show();
            }
            
        });
        return v;
    }

    public List<Map<String, Object>> getData(){        
        for(int i=0;i<mItemName.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", mItemImage[0]);
            map.put("text", mItemName[i]);
            mDataList.add(map);
        }
        return mDataList;
    }
}