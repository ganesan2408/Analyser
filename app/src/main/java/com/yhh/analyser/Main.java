package com.yhh.analyser;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yhh.activity.menu.AboutActivity;
import com.yhh.activity.menu.FeedbackActivity;
import com.yhh.activity.menu.MyFileActivity;
import com.yhh.activity.menu.MyShotActivity;
import com.yhh.fragment.MonitorFragment;
import com.yhh.fragment.PerformanceFragment;
import com.yhh.fragment.StatusViewerFragment;
import com.yhh.fragment.ToolBoxFragment;
import com.yhh.info.app.PhoneInfo;
import com.yhh.utils.ConstUtils;
import com.yhh.utils.DialogUtils;
import com.yhh.widget.slidingmenu.SlidingMenu;

public class Main extends FragmentActivity {
    private static final String TAG = ConstUtils.DEBUG_TAG+ "Main";
    
    private FragmentTabHost mTabHost;
    private RadioGroup mTabRg;
    private Long mExitTime = (long) 0;
    
//    public static String MONITOR_PARENT_PATH;
    
    private final Class[] mFragments = {
            MonitorFragment.class, 
            StatusViewerFragment.class,
            PerformanceFragment.class, 
            ToolBoxFragment.class
    };
    
    private final int[] mRadioIds = {
            R.id.tab_rb_1,  
            R.id.tab_rb_2,
            R.id.tab_rb_3, 
            R.id.tab_rb_4
    };
    
    private final int[] mTitles = {
            R.string.title_monitor,
            R.string.title_view,
            R.string.title_performance,
            R.string.title_tool
    };
    
    private SlidingMenu menuMenu;
    private TextView appNameTv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        firstCreateShortCut();
        initParams();
        initNav();
        initView();
        initMenu();
        setDefaultView();

        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initParams(){
//        MONITOR_PARENT_PATH = getFilesDir().getAbsolutePath() +"/monitor";
      
    }
    
    private void initView(){
        //设置tab host
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, this.getSupportFragmentManager(), R.id.realtabcontent);
        final int count = mFragments.length;
        for(int i=0;i<count;i++){
            TabSpec tabSpec = mTabHost.newTabSpec(i+"").setIndicator(i+"");
            mTabHost.addTab(tabSpec, mFragments[i], null);
        }
        
        //设置切换按钮
        mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
        mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < count; i++) {
                    if (mRadioIds[i] == checkedId) {
                        mTabHost.setCurrentTab(i);
                        appNameTv.setText(mTitles[i]);
                        break;
                    }
                }
            }

        });
        
    }
    
  //设置默认选中的Tab
    private void setDefaultView(){
        appNameTv.setText(mTitles[0]);
        mTabHost.setCurrentTab(0);
    }
    
    /**
     * 初始化 左拉菜单
     */
    private void initMenu(){
        menuMenu = new SlidingMenu(Main.this);
        menuMenu.setMode(SlidingMenu.LEFT);
        menuMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置触摸屏幕的模式
        menuMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); // 设置滑动菜单视图的宽度
        menuMenu.setFadeDegree(0.9f);  // 设置渐入渐出效果的值 
        menuMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menuMenu.setMenu(R.layout.menu);
        
        //设置手机型号
        ((TextView)findViewById(R.id.phone_model)).setText(PhoneInfo.getPhoneType());
        menuMenu.setBackgroundColor(0xe5e5e5);
        
//        // 配置背景图片
//        menuMenu.setBackgroundImage(R.drawable.menu_bg3);
//        // 设置专场动画效果     
//        menuMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//            
//            @Override
//            public void transformCanvas(Canvas canvas, float percentOpen) {
//                float scale = (float) (percentOpen * 0.25 + 0.75);
//                canvas.scale(scale, scale, -canvas.getWidth() / 2, canvas.getHeight() / 2);
//            }
//        });
//        
//        menuMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
//            
//            @Override
//            public void transformCanvas(Canvas canvas, float percentOpen) {
//                float scale = (float) (1 - percentOpen * 0.25);
//                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
//            }
//        });

    }
    
    private void initNav(){
        appNameTv = (TextView) findViewById(R.id.appName);
        findViewById(R.id.menu_nav).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(menuMenu !=null){
                    menuMenu.toggle();
                }
            }
        });
    }
    

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
          if ((System.currentTimeMillis() - mExitTime) > 2000) {
              Toast.makeText(this, R.string.quite_alert, Toast.LENGTH_SHORT).show();
              mExitTime = System.currentTimeMillis();
          } else {
              finish();
          }
          return true;
      }
      return super.onKeyDown(keyCode, event);
  }
    
    public void menuClick(View v){
        switch(v.getId()){
            case R.id.mmenu_device_info:
                DialogUtils.showAlergDialog(this, getString(R.string.about_phone), 
                        (new PhoneInfo()).toString());
                break;
                
            case R.id.mmenu_create_icon:
                createShortCutDialog();
                break;

            case R.id.mmenu_my_file:
                Intent fIntent = new Intent(this, MyFileActivity.class);
                startActivity(fIntent);
                break;

            case R.id.mmenu_my_shot:
                Intent shotIntent = new Intent(this, MyShotActivity.class);
                startActivity(shotIntent);
                break;
                
            case R.id.mmenu_my_feedback:
                Intent fbIntent = new Intent(this, FeedbackActivity.class);
                startActivity(fbIntent);
                break;
                
            case R.id.mmenu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
        }
    }

    private void createShortCut(){
        Intent shortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // add name
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        // add intent
        ComponentName comp = new ComponentName(this.getPackageName(),"."+this.getLocalClassName());
        Intent mainIntent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
        // add icon
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.logo1);
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes); 
        // only one
        shortCutIntent.putExtra("duplicate", false); 
        
        sendBroadcast(shortCutIntent);
    }
    
    private void createShortCutDialog(){
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setTitle(R.string.create_icon)
                .setMessage(R.string.create_icon_msg)
                .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        createShortCut();
                    }
                })
                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }
    
    private void firstCreateShortCut(){
        SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
        boolean firstStart = pref.getBoolean("First", true);
        if(firstStart){
            createShortCutDialog();
            Editor editor = pref.edit();
            editor.putBoolean("First", false);
            editor.commit();
        }
    }
}
