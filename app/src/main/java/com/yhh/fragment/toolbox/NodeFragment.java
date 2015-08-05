/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.fragment.toolbox;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.robot.RootTools;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.utils.ShellUtils;
import com.yhh.utils.ShellUtils.CommandResult;

public class NodeFragment extends Fragment {
    private static final String TAG = ConstUtils.DEBUG_TAG+ "NodeFragment";

    private EditText autoKey;
    private LinearLayout mCatalogLayout;
    private ListView mNodeList;
    
    private ArrayList<String> mNodeAdapter = new ArrayList<String>();
    private ArrayList<Boolean> mIsFile;
    
    private int mDepth=0;
    private String mRootPath="/";

    private static final String COMMAND_SH = "sh";
    private static final String COMMAND_EXIT = "exit\n";
    private static final String COMMAND_LINE_END = "\n";

    private Process shellProcess = null;
    private DataOutputStream cmdStream = null;
    private BufferedReader successReader = null;
    private BufferedReader errorReader = null;
    
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }
    
    
//    @Override
//    public void onBackPressed() {
//        if(mDepth>0){
//            mCatalogLayout.removeViews(mDepth, 1);
//            updateCatalog(--mDepth);
//        }else{
//            super.onBackPressed();
//        }
//    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.shell_terminal, null) ;
        mNodeList = (ListView) v.findViewById(R.id.node_list);
        autoKey = (EditText) v.findViewById(R.id.adbKey);
        mCatalogLayout = (LinearLayout) v.findViewById(R.id.catalog_ll);

        autoInputShell();
        addCatalogView("/");
        
        // 点击具体节点
        mNodeList.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(mIsFile.get(position)){
                  final String nodePath =  mNodeAdapter.get(position);
                  new Thread(new Runnable(){

                    @Override
                    public void run() {
                        CommandResult cr = ShellUtils.execCommand("cat "+mRootPath+nodePath, false);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("CommandResult", cr);
                        bundle.putString("title", nodePath);
                        msg.what = 0x11;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                  }).start();
                }else{
                    mDepth++;
                    mRootPath += mNodeAdapter.get(position)+"/";
                    addCatalogView(mNodeAdapter.get(position)+"/");
                    autoKey.setText("");
                }
            }
        });
        return v ;
    }
    
    public void onStart(){
        super.onStart();
        updateCatalog(mDepth);
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                ShellUtils.CommandResult cr = (CommandResult) msg.getData()
                        .getSerializable("CommandResult");
                String title = msg.getData().getString("title");
                Log.i(TAG,"title="+title);
                DialogUtils.showAlergDialog(mContext, title, cr.successMsg+" "+ cr.errorMsg);
            }
        };
    };

    // 添加节点目录
    private void addCatalogView(String content){
        final TextView tv = new TextView(mContext);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        tv.setLayoutParams(params);
        tv.setText(content);
        tv.setTextSize(25);
        tv.setPadding(5, 0, 5, 0);
        
        final int index = mDepth;
        tv.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.i(TAG,"  index=" + index +", mDepth="+mDepth);
                mCatalogLayout.removeViews(index+1, mDepth-index);
                mDepth = index;
                updateCatalog(mDepth);
            }
        });
        mCatalogLayout.addView(tv);
    }
    
    //更新 目录
    private void updateCatalog(int curDepth){
        String curPath = "";
        int index = getIndexOf(curDepth+1);
        if(index>0){
            mRootPath = mRootPath.substring(0, index+1);
        }else{
            mRootPath = "/";
        }
        Log.i(TAG,"mRootPath=" +mRootPath);
        
        mNodeAdapter = getAutoAdapterArray("");
        if (mNodeAdapter != null) {
            Log.e(TAG, "adapterArray length="+mNodeAdapter.size());
            ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(
                    mContext, R.layout.auto_comlete_drop_down, mNodeAdapter);
            mNodeList.setAdapter(newAdapter);
        } else {
            Log.e(TAG, "adapterArray is null.");
            mNodeList.setAdapter(null);
        }
    }
    
    private int getIndexOf(int num){
        char[] cRoot = mRootPath.toCharArray();
        for(int i=0;i<cRoot.length;i++){
            if(cRoot[i]=='/'){
                num--;
                if(num<=0){
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * 生成智能Shell输入
     */
    public void autoInputShell() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.auto_comlete_drop_down, ConstUtils.CommandArray);
        autoKey.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
//                Log.i(TAG, "==onTextChanged:" + s);
                mNodeAdapter = getAutoAdapterArray(s.toString());
                if (mNodeAdapter != null) {
                    ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(
                            mContext, R.layout.auto_comlete_drop_down, mNodeAdapter);
                    mNodeList.setAdapter(newAdapter);
                } else {
                    mNodeList.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    /**
     * 获取智能显示的列表
     * 
     * @param prefix
     * @return
     */
    private ArrayList<String> getAutoAdapterArray(String prefix) {
        ArrayList<String> adapterArray = new ArrayList<String>();
        mIsFile = new ArrayList<Boolean>();
        
        try {
            File file = new File(mRootPath);
            File[] subFiles = null;
            if (file.exists() && file.isDirectory()) {
                subFiles = file.listFiles();
                if (subFiles != null) {
                    int i = 0;
                    for (File f : subFiles) {
                        if (prefix == null ||f.getName().contains(prefix)) {
                            adapterArray.add(f.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "getAutoAdapterArray error", e);
        }
        Collections.sort(adapterArray);
        for(int i=0;i<adapterArray.size();i++){
            if(new File(mRootPath+adapterArray.get(i)).isFile()){
                mIsFile.add(true);
            }else{
                mIsFile.add(false);
            }
        }
        return adapterArray;
    }
    

    
    public void intTerminal() {
        try {
            shellProcess = Runtime.getRuntime().exec(COMMAND_SH);
            cmdStream = new DataOutputStream(shellProcess.getOutputStream());
            successReader = new BufferedReader(new InputStreamReader(
                    shellProcess.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(
                    shellProcess.getErrorStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finishTerminal() {
        try {
            cmdStream.writeBytes(COMMAND_EXIT);
            cmdStream.flush();

            if (cmdStream != null) {
                cmdStream.close();
            }
            if (successReader != null) {
                successReader.close();
            }
            if (errorReader != null) {
                errorReader.close();
            }
        } catch (IOException e) {
            Log.i(TAG, "finishTerminal ERROR.", e);
        }

        if (shellProcess != null) {
            shellProcess.destroy();
        }
    }

    public void runShell(String command) {
        if (command == null) {
            Log.e(TAG, "command is null.");
            return;
        }

        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        String line;
        try {
            cmdStream.write(command.getBytes());
            cmdStream.writeBytes(COMMAND_LINE_END);
            cmdStream.flush();

            while ((line = successReader.readLine()) != null) {
                successMsg.append(line + "\n");
            }
            while ((line = errorReader.readLine()) != null) {
                errorMsg.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
