/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;
import com.yhh.analyser.utils.ConstUtils;
import com.yhh.analyser.utils.RootUtils;

public class KernelActivity extends BaseActivity {
	private static String TAG= ConstUtils.DEBUG_TAG+"KernelActivity";
	
	private CheckBox mPanicCb;
	private TextView mPanicTv;
	private Button mKernelCrashBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_kernel);

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


}
