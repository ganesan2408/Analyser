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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.utils.RootUtils;
import com.yhh.analyser.utils.ConstUtils;

public class KernelActivity extends Activity {
	private static String TAG= ConstUtils.DEBUG_TAG+"KernelActivity";
	
	private CheckBox mPanicCb;
	private TextView mPanicTv;
	private Button mKernelCrashBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_kernel);
		initActionBar();
		
        initUI();
	}
	
	private void initUI(){
        mPanicCb = (CheckBox) findViewById(R.id.kernel_cb);
        mPanicTv = (TextView) findViewById(R.id.kernel_tv);
        mKernelCrashBtn = (Button) findViewById(R.id.kernel_crash);
        
        mPanicCb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if(arg1){
                    mPanicTv.setText("panic is reboot and ssr is reboot");
                    RootUtils.getInstance().setSystemProperty("sys.dloadmode.ssr", "SYSTEM");
                }else{
                    mPanicTv.setText("panic is download and ssr is related");
                    RootUtils.getInstance().setSystemProperty("sys.dloadmode.ssr", "RELATED");
                }
            }
            
        });
        
        mKernelCrashBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                openCrashDialog();
            }
        });
	}
	
    public  void openCrashDialog() {
        AlertDialog.Builder builder = new  AlertDialog.Builder(KernelActivity.this);
        builder.setTitle("Warning")
                .setMessage("Are you sure to simulate a kernel crash?")
                .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        RootUtils.getInstance().setSystemProperty("sys.lenovo.simulate.ke", "TRUE");
                    }
                })
                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
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
