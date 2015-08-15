package com.yhh.afragment.performance;
///**
// * @author yuanhh1
// * 
// * @email yuanhh1@lenovo.com
// * 
// */
//package com.yhh.performance;
//
//import android.annotation.SuppressLint;
//import android.app.ActionBar;
//import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.support.v4.widget.DrawerLayout;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.yhh.analyser.R;
//import com.yhh.ldrawer.ActionBarDrawerToggle;
//import com.yhh.ldrawer.DrawerArrowDrawable;
//import com.yhh.utils.DialogUtils;
//
//
//public class MainPerfActivity extends Activity {
//
//    private DrawerLayout mDrawerLayout;
//    private ListView mDrawerList;
//    private ActionBarDrawerToggle mDrawerToggle;
//    private DrawerArrowDrawable drawerArrow;
//    
//    private FragmentManager fm;
//    private FragmentTransaction ft;
//    public static boolean sFirstStart=true;
//
//    @SuppressLint({ "NewApi", "ResourceAsColor" })
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.performance_main);
//        ActionBar ab = getActionBar();
//        ab.setDisplayShowHomeEnabled(false); 
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setHomeButtonEnabled(true);
//        
//        fm =  this.getFragmentManager();
//        
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.navdrawer);
//
//        drawerArrow = new DrawerArrowDrawable(this) {
//            @Override
//            public boolean isLayoutRtl() {
//                return false;
//            }
//        };
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//            drawerArrow, R.string.drawer_open,
//            R.string.drawer_close) {
//
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                invalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                invalidateOptionsMenu();
//            }
//        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();
//
//        drawerArrow.setColor(R.color.drawer_arrow_second_color);
//        
//        String[] values = new String[]{
//            "CPU(限核，限频)",
//            "GPU(限频)",
//            "其他"
//        };
//        setFrag(new CpuGovernor());
//        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//            mDrawerLayout.closeDrawer(mDrawerList);
//        } 
//        
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//            android.R.layout.simple_list_item_1, android.R.id.text1, values);
//        
//        mDrawerList.setAdapter(adapter);
//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Fragment frag = null;
//                switch (position) {
//                    case 0:
//                        frag = new CpuGovernor();
//                        break;
//                        
//                    case 1:
//                        frag = new GpuGovernor();
//                        break;
//                        
//                    case 2:
//                        frag = new FpsGovernor();
//                        break;
//                }
//                setFrag(frag);
//                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//                    mDrawerLayout.closeDrawer(mDrawerList);
//                } 
//            }
//        });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//                mDrawerLayout.closeDrawer(mDrawerList);
//            } else {
//                mDrawerLayout.openDrawer(mDrawerList);
//            }
//        }else  if(item.getItemId() == R.id.introduction_doc){
//            DialogUtils.showAlergDialog(this,getString(R.string.introduction_title),
//                    getString(R.string.introduction_performance));
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.getMenuInflater().inflate(R.menu.introduction, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//    
//    
//    void setFrag(Fragment afragment){
//        if(null == afragment){
//            return;
//        }
//        
//        ft = fm.beginTransaction();
//        ft.replace(R.id.perf_fragment,afragment);
//        ft.commit();
//    }
//    
//    
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}
