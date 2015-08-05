/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.app.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.Main;
import com.yhh.analyser.R;
import com.yhh.app.setttings.SettingFloatingActivity;
import com.yhh.app.setttings.SettingMonitorActivity;
import com.yhh.app.setttings.SettingsActivity;
import com.yhh.fragment.MonitorFragment;
import com.yhh.info.InfoFactory;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DensityUtils;

/**
 * Service running in background
 */
public class MonitorService extends Service {

	private final static String TAG =  ConstUtils.DEBUG_TAG+ "MonitorService";
	private boolean DEBUG = true;

	private WindowManager windowManager = null;
	private WindowManager.LayoutParams wmParams = null;
	private WindowManager.LayoutParams titleParams = null;
	private View viFloatingWindow;
	private View viFloatingTitle;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private static int y0;
	
	private TextView mFloatLv;
	private Button mFloatStopBtn;
	private Button mFloatChangeBackgroundBtn;
	private Button mFloatHideBtn;
	private Button mFloatTopBtn;
	
	private StringBuffer mFloatContent;
	private String[] itemTitles;
	private String[] itemUnitTitles;
	
	private int delaytime;
	private DecimalFormat fomart;
	
	private InfoCollector infoAdmin;
	private ShellMonitor mShellMonitor;
	private boolean isRunTop =false;
	private Handler handler;
	private SharedPreferences mPreferences;
	
	private boolean isFloating;
	private int mFloatColorIndex =1;
	private String appName, packageName, startActivity;
	private int pid, uid;
	
	public static boolean isServiceStoped = true;
	public static String resultFilePath;
	
	// get start time
    private static final int MAX_START_TIME_COUNT = 5;
    private int getStartTimeCount = 0;
    private boolean isGetStartTime = true;
    private String startTime = "";
	public static final String MONITOR_SERVICE_ACTION = "com.yhh.app.MonitorService";
	
	private boolean[] mIsFloatingItem = new boolean[SettingMonitorActivity.MONITOR_ITEMS_COUNT];

	@Override
	public void onCreate() {
		Log.i(TAG, "MonitorService onCreate");
		super.onCreate();
		handler = new Handler();
		isServiceStoped = false;
		fomart = new DecimalFormat();
		fomart.setMaximumFractionDigits(2);
		fomart.setMinimumFractionDigits(0);
		
		mShellMonitor = ShellMonitor.newInstance(this);
		
	}

	private void readPrefs(){
	    if(DEBUG){
	        Log.i(TAG,"INTO readPrefs");
	    }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for(int i=0;i<SettingMonitorActivity.MONITOR_ITEMS_COUNT;i++){
            mIsFloatingItem[i] = !mPreferences.getBoolean(SettingFloatingActivity.PREF_FLOATING_ITEMS[i], false);
        }
        int interval = mPreferences.getInt(SettingsActivity.KEY_INTERVAL, 1);
        delaytime = interval * 1000;
        
        for(int i=0;i< mIsFloatingItem.length;i++){
            if(mIsFloatingItem[i]){
               isFloating = true;
               break;
            }
        }
        isFloating |=  mShellMonitor.isEnabled();
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "service onStart");
		PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(this, MonitorFragment.class), 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentIntent(contentIntent)
		    .setSmallIcon(R.drawable.system3)
		    .setWhen(System.currentTimeMillis()).setAutoCancel(true)
		    .setContentTitle(getResources().getString(R.string.main_app_analyser));
		startForeground(startId, builder.build());

		pid = intent.getExtras().getInt("pid");
		uid = intent.getExtras().getInt("uid");
		appName = intent.getExtras().getString("appName");
		packageName = intent.getExtras().getString("packageName");
		startActivity = intent.getExtras().getString("startActivity");
		
		createMonitorFile();
		infoAdmin = new InfoCollector(getBaseContext(),pid);
		infoAdmin.writeTitle2File();
		
		readPrefs();
		initUI();
		
		ExceptionStat.getInstance().clear();
		
		handler.post(task);
		return START_NOT_STICKY;
	}
	
	private void initUI(){
	    if (isFloating) {
            viFloatingWindow = LayoutInflater.from(this).inflate(R.layout.app_monitor_floating, null);
            viFloatingTitle = LayoutInflater.from(this).inflate(R.layout.app_monitor_floating_title, null);
            mFloatLv = (TextView) viFloatingWindow.findViewById(R.id.float_items_lv);
            mFloatLv.setText(R.string.calculating);
            changeFloatColor(Color.MAGENTA);
            itemTitles = this.getResources().getStringArray(R.array.monitor_items);
            itemUnitTitles = this.getResources().getStringArray(R.array.monitor_unit_items);
            
            mFloatStopBtn = (Button) viFloatingTitle.findViewById(R.id.float_stop_btn);
            mFloatChangeBackgroundBtn = (Button) viFloatingTitle.findViewById(R.id.float_change_btn);
            mFloatHideBtn = (Button) viFloatingTitle.findViewById(R.id.float_hide_btn);
            mFloatTopBtn = (Button) viFloatingTitle.findViewById(R.id.float_top_btn);
            mFloatStopBtn.setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                  isServiceStoped = true;
                  Intent intent = new Intent();
                  intent.setAction(MONITOR_SERVICE_ACTION);
                  sendBroadcast(intent);
                  Toast.makeText(MonitorService.this, R.string.monitor_stop_tips, Toast.LENGTH_LONG).show();
                  stopSelf();
              }
          });
            
            mFloatHideBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getString(R.string.float_fold).equals(mFloatHideBtn.getText().toString())) {
                        mFloatLv.setVisibility(View.GONE);
                        mFloatHideBtn.setText(getString(R.string.float_unfold));
                    }else{
                        mFloatLv.setVisibility(View.VISIBLE);
                        mFloatHideBtn.setText(getString(R.string.float_fold));
                    }
                }
            });
            mFloatChangeBackgroundBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoChangeFloatColor();
                }
            });
            
            mFloatTopBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getString(R.string.float_top).equals(mFloatTopBtn.getText().toString())) {
                        mFloatTopBtn.setText(getString(R.string.float_monitor));
                        isRunTop = true;
                    }else{
                        mFloatTopBtn.setText(getString(R.string.float_top));
                        isRunTop = false;
                    }
                }
            });
            createFloatingWindow();
	    }
	}
	
	@SuppressLint({ "SimpleDateFormat", "SdCardPath" })
    private void createMonitorFile() {
		SimpleDateFormat formatter = new SimpleDateFormat("MMdd_HHmmss");
		String mDateTime = formatter.format(Calendar.getInstance().getTime().getTime());
		if(appName == null){
		    resultFilePath = Main.MONITOR_PARENT_PATH + "/" + mDateTime + "";
		}else{
		    resultFilePath = Main.MONITOR_PARENT_PATH + "/" + mDateTime + "_" + appName;
		}
	}

	private void createFloatingWindow() {
		SharedPreferences shared = getSharedPreferences("float_flag", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		
		y0 = DensityUtils.dip2px(this, 25);
		windowManager = (WindowManager) getApplicationContext().getSystemService("window");
		wmParams =  new WindowManager.LayoutParams();
		wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		wmParams.flags =  LayoutParams.FLAG_LAYOUT_IN_SCREEN|LayoutParams.FLAG_NOT_TOUCHABLE|LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = y0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.alpha = 1.0f;
		windowManager.addView(viFloatingWindow, wmParams);
		
		titleParams =  new WindowManager.LayoutParams();
		titleParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		titleParams.flags =  LayoutParams.FLAG_LAYOUT_IN_SCREEN |LayoutParams.FLAG_NOT_FOCUSABLE;
		titleParams.format = PixelFormat.RGBA_8888;
		titleParams.gravity = Gravity.LEFT | Gravity.TOP;
		titleParams.x = 0;
		titleParams.y = 0;
		titleParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		titleParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		titleParams.alpha = 1.0f;
		windowManager.addView(viFloatingTitle, titleParams);
		
		viFloatingTitle.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					updateViewPosition();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});
	}

	private Runnable task = new Runnable() {

		public void run() {
			if (!isServiceStoped) {
				dataRefresh();
				
				if (isFloating) {
				    if(viFloatingWindow != null){
				        windowManager.updateViewLayout(viFloatingWindow, wmParams);
				    }
					if(viFloatingTitle != null){
					    windowManager.updateViewLayout(viFloatingTitle, titleParams);
					}
				}

				if(startActivity != "-1"){
                    getStartTimeFromLogcat();
                }
			} else {
				Intent intent = new Intent();
				intent.setAction(MONITOR_SERVICE_ACTION);
				sendBroadcast(intent);
				stopSelf();
			}
		}
	};
	
	/**
	 * refresh floating window data.
	 * 
	 */
	public void dataRefresh() {
		if (isFloating) {
		    if(isRunTop){
		        refreshTop();
		    }else if(mShellMonitor.isEnabled()){
		        refreshAdb();
		    }else{
		        HashMap<Integer,String> monitorInfo = infoAdmin.admin();
    		    mFloatContent = new StringBuffer();
    		    for(int i=0;i< itemTitles.length-1;i++){
        		    if(mIsFloatingItem[i]){
        		        mFloatContent.append(itemTitles[i]+": ");
    		            mFloatContent.append(monitorInfo.get(i));
        		        mFloatContent.append(itemUnitTitles[i]+"\n");
                    }
    		    }
    		    if(mIsFloatingItem[12]){
                    mFloatContent.append(itemTitles[12]+": ");
                    mFloatContent.append(monitorInfo.get(InfoCollector.TRAFFIC_SEND_SPEED)+ "/"+
                            monitorInfo.get(InfoCollector.TRAFFIC_REV_SPEED));
                    mFloatContent.append(itemUnitTitles[12]);
                }
    		    mFloatLv.setText(mFloatContent.toString().trim());
    		    
    		    handler.postDelayed(task, delaytime);
		    }
		}
	}
	
	ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
	private void refreshAdb(){
	    if(isFloating && mShellMonitor.isEnabled()){
        	singleThreadExecutor.execute(new Runnable(){
        	    public void run() {
        	        try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
        	        String adbRes = mShellMonitor.exec();
        	        if(adbRes!=null){
        	            adbRes = adbRes.trim();
        	        }
        	        mShellHandler.sendMessage(mShellHandler.obtainMessage(1, adbRes));
        	    }
        	});
	    }else{
	        dataRefresh();
	    }
	}
	private void refreshTop(){
	    if(isFloating && isRunTop){
            singleThreadExecutor.execute(new Runnable(){
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
                    String adbRes = mShellMonitor.execTop(10);
                    mShellHandler.sendMessage(mShellHandler.obtainMessage(2, adbRes));
                }
            });
	    }else{
	        dataRefresh();
	    }
    }
	
	private Handler mShellHandler = new Handler(){
	       public void handleMessage(Message msg) {
	           if(msg.what == 1){
	               refreshAdb();
	               mFloatLv.setText((String)msg.obj);
	           }else  if(msg.what == 2){
                   refreshTop();
                   mFloatLv.setText((String)msg.obj);
               }
	       };
	    };

	private void updateViewPosition() {
		if (viFloatingWindow != null) {
		    wmParams.x = (int) (x - mTouchStartX);
	        wmParams.y = (int) (y + y0 - mTouchStartY);
			windowManager.updateViewLayout(viFloatingWindow, wmParams);
		}
		
		if (viFloatingTitle != null) {
		    titleParams.x = (int) (x - mTouchStartX);
		    titleParams.y = (int) (y - mTouchStartY);
		    windowManager.updateViewLayout(viFloatingTitle, titleParams);
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "service onDestroy");
		if (windowManager != null) {
			windowManager.removeView(viFloatingWindow);
			viFloatingWindow = null;
		}
		if (windowManager != null) {
            windowManager.removeView(viFloatingTitle);
            viFloatingTitle = null;
        }
		handler.removeCallbacks(task);
		infoAdmin.closeOpenedStream();
		InfoFactory.getInstance().destory();
        
//		if(isServiceStop){
//		    Intent chartIntent = new Intent(MonitorService.this, AppChartAnalyser.class);
//		    chartIntent.putExtra(AppMonitor.MONITOR_PATH, "");
//		    chartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		    chartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(chartIntent);
//		}
		
		super.onDestroy();
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void getStartTimeFromLogcat() {
        if (!isGetStartTime || getStartTimeCount >= MAX_START_TIME_COUNT) {
            return;
        }
        try {
            String logcatCommand = "logcat -v time -d ActivityManager:I *:S";
            Process process = Runtime.getRuntime().exec(logcatCommand);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder strBuilder = new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                strBuilder.append(line);
                strBuilder.append(ConstUtils.LINE_END);
                String regex = ".*Displayed.*" + startActivity + ".*\\+(.*)ms.*";
                if (line.matches(regex)) {
                    Log.w(TAG, line);
                    if (line.contains("total")) {
                        line = line.substring(0, line.indexOf("total"));
                    }
                    startTime = line.substring(line.lastIndexOf("+") + 1, line.lastIndexOf("ms") + 2);
                    Toast.makeText(MonitorService.this, getString(R.string.start_time) + startTime, Toast.LENGTH_LONG).show();
                    isGetStartTime = false;
                    break;
                }
            }
            getStartTimeCount++;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
	
    private void autoChangeFloatColor(){
        if(mFloatColorIndex%7 ==0){
            changeFloatColor(Color.BLUE);
        }else if(mFloatColorIndex%7 ==1){
            changeFloatColor(Color.GREEN); 
        }else if(mFloatColorIndex%7 ==2){
            changeFloatColor(Color.YELLOW); 
        }else if(mFloatColorIndex%7 ==3){
            changeFloatColor(Color.BLACK);
        }else if(mFloatColorIndex%7 ==4){
            changeFloatColor(Color.DKGRAY); 
        }else if(mFloatColorIndex%7 ==5){
            changeFloatColor(Color.CYAN); 
        }else if(mFloatColorIndex%7 ==6){
            changeFloatColor(Color.MAGENTA);
        }else{
            changeFloatColor(Color.RED); 
        }
        mFloatColorIndex++;
    }
    
    private void changeFloatColor(int colorRes){
        mFloatLv.setTextColor(colorRes);
    }
}