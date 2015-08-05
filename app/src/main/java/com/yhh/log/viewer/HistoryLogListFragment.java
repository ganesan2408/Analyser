/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.log.viewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;

public class HistoryLogListFragment extends Fragment{
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "HistoryLogFragment";
    private ArrayList<String> mFolder;
    private ArrayList<ArrayList<String>> mFileTree;
    private ExpandableListView mFileTreeElv;
    private Context mcontext;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext = this.getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_history_fragment, container, false);  
        getFileList();
        initExpandableListView(view);
        return view;
    }
    
    private void getFileList(){
        mFolder = new ArrayList<String>();
        mFileTree = new ArrayList<ArrayList<String>>();
        
        File dir = new File(FileUtils.PATH_SD_LOG);
        if(dir.exists()){
            File[] subDir = dir.listFiles();   
            if(subDir == null || subDir.length <= 0){
                return;
            }
            for(File sDir:subDir){
                if(sDir.isDirectory()){
                    mFolder.add(sDir.getName());
                    String choosedDir = sDir.getAbsolutePath();
                    //当log路径下，还存在log时采用
                    if(FileUtils.checkPath(choosedDir+"/aplog")){
                        choosedDir += "/aplog";
                    }else  if(FileUtils.checkPath(choosedDir+"/curlog")){
                        choosedDir += "/curlog";
                    }
                    ArrayList<String> subFileList = FileUtils.listAllFiles(choosedDir);
                    Collections.sort(subFileList);
                    mFileTree.add(subFileList);
                }
            }
            Collections.sort(mFolder, Collections.reverseOrder());
        }
    }
    
    private void initExpandableListView(View v){
        ExpandableListAdapter adapter = new BaseExpandableListAdapter(){

            @Override
            public int getGroupCount() {
                return mFolder.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return mFileTree.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return mFolder.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return mFileTree.get(groupPosition).get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
            
            private TextView getTextView(){
                AbsListView.LayoutParams lp =new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView tv = new TextView(mcontext);
                tv.setLayoutParams(lp);
                tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                tv.setPadding(36, 0, 0, 0);
                tv.setTextSize(20);
                return tv;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                    View convertView, ViewGroup parent) {
                LinearLayout ll = new LinearLayout(mcontext);
                ll.setOrientation(0);
                ImageView folderImage = new ImageView(mcontext);
                folderImage.setImageResource(R.drawable.ic_folder);
                ll.addView(folderImage);
                TextView folder = getTextView();
                folder.setText(mFolder.get(groupPosition));
                ll.addView(folder);
                return ll;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent) {
                TextView fileTv = getTextView();
                fileTv.setText(mFileTree.get(groupPosition).get(childPosition));
                return fileTv;
            }

            @Override
            public boolean isChildSelectable(int groupPosition,
                    int childPosition) {
                return true;
            }
        };
        
        mFileTreeElv = (ExpandableListView) v.findViewById(R.id.history_log_list);
        mFileTreeElv.setAdapter(adapter);
        mFileTreeElv.setOnChildClickListener(new OnChildClickListener(){

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                String targetPath = FileUtils.PATH_SD_LOG +"/" 
                    + mFolder.get(groupPosition) +"/"
                    + mFileTree.get(groupPosition).get(childPosition);
                Log.i(TAG,"targetPath"+targetPath);
                
                Intent intent;
                if(mFileTree.get(groupPosition).get(childPosition).contains(ConstUtils.LOG_SLEEP)){
                    intent = new Intent(mcontext, SleepLogReader.class);
                }else{
                    intent = new Intent(mcontext, ContentReaderActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putString("logPath", targetPath);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
            
        });
    }
}
