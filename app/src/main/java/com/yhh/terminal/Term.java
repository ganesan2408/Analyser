/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yhh.terminal;


import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.terminal.emulatorview.EmulatorView;
import com.yhh.terminal.emulatorview.TermKeyListener;
import com.yhh.terminal.emulatorview.TermSession;
import com.yhh.terminal.emulatorview.UpdateCallback;
import com.yhh.terminal.util.SessionList;
import com.yhh.terminal.util.TermSettings;
import com.yhh.utils.ConstUtils;

/**
 * A terminal emulator activity.
 */

public class Term extends Activity implements UpdateCallback {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "Term";
    /**
     * The ViewFlipper which holds the collection of EmulatorView widgets.
     */
    private TermViewFlipper mViewFlipper;
    
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams wmParams = null;
    private boolean isFloat;
    private View mFloatingView;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    
    private SessionList mTermSessions;

    private SharedPreferences mPrefs;
    private TermSettings mSettings;

    private final static int SEND_CONTROL_KEY_ID = 3;

    private boolean mAlreadyStarted = false;
    private boolean mStopServiceOnFinish = false;

    private Intent TSIntent;

    public static final int REQUEST_CHOOSE_WINDOW = 1;
    public static final String EXTRA_WINDOW_ID = "jackpal.androidterm.window_id";
    private int onResumeSelectWindow = -1;
    private ComponentName mPrivateAlias;

    private PowerManager.WakeLock mWakeLock;
    private WifiManager.WifiLock mWifiLock;
    // Available on API 12 and later
    private static final int WIFI_MODE_FULL_HIGH_PERF = 3;

    private static final String ACTION_PATH_BROADCAST = "jackpal.androidterm.broadcast.APPEND_TO_PATH";
    private static final String ACTION_PATH_PREPEND_BROADCAST = "jackpal.androidterm.broadcast.PREPEND_TO_PATH";
    private static final String PERMISSION_PATH_BROADCAST = "jackpal.androidterm.permission.APPEND_TO_PATH";
    private static final String PERMISSION_PATH_PREPEND_BROADCAST = "jackpal.androidterm.permission.PREPEND_TO_PATH";
    private int mPendingPathBroadcasts = 0;
    
    private BroadcastReceiver mPathReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String path = makePathFromBundle(getResultExtras(false));
            mPendingPathBroadcasts--;

            if (mPendingPathBroadcasts <= 0 && mTermService != null) {
                populateViewFlipper();
                populateWindowList();
            }
        }
    };
    private static final int FLAG_INCLUDE_STOPPED_PACKAGES = 0x20;

    private TermService mTermService;
    
    private ServiceConnection mTSConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Bound to TermService");
            TermService.TSBinder binder = (TermService.TSBinder) service;
            mTermService = binder.getService();
            if (mPendingPathBroadcasts <= 0) {
                populateViewFlipper();
                populateWindowList();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mTermService = null;
        }
    };

    private ActionBar mActionBar;
    private int mActionBarMode = TermSettings.ACTION_BAR_MODE_NONE;

    private WindowListAdapter mWinListAdapter;
    
    private class WindowListActionBarAdapter extends WindowListAdapter implements UpdateCallback {
        // From android.R.style in API 13
        private static final int TextAppearance_Holo_Widget_ActionBar_Title = 0x01030112;

        public WindowListActionBarAdapter(SessionList sessions) {
            super(sessions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(Term.this);
            String title = getSessionTitle(position, getString(R.string.window_title, position + 1));
            label.setText(title);
            /** SDK >= 13*/
            label.setTextAppearance(Term.this, TextAppearance_Holo_Widget_ActionBar_Title);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        public void onUpdate() {
            notifyDataSetChanged();
            mActionBar.setSelectedNavigationItem(mViewFlipper.getDisplayedChild());
        }
    }

    private ActionBar.OnNavigationListener mWinListItemSelected = new ActionBar.OnNavigationListener() {
        public boolean onNavigationItemSelected(int position, long id) {
            int oldPosition = mViewFlipper.getDisplayedChild();
            if (position != oldPosition) {
                mViewFlipper.setDisplayedChild(position);
                if (mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES) {
                    mActionBar.hide();
                }
            }
            return true;
        }
    };

    private boolean mHaveFullHwKeyboard = false;

    private class EmulatorViewGestureListener extends SimpleOnGestureListener {
        private EmulatorView view;

        public EmulatorViewGestureListener(EmulatorView view) {
            this.view = view;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            //Check for link at tap location
            String link = view.getURLat(e.getX(), e.getY());
            if(link != null)
                execURL(link);
            else
                doUIToggle((int) e.getX(), (int) e.getY(), view.getVisibleWidth(), view.getVisibleHeight());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);
            if (absVelocityX > Math.max(1000.0f, 2.0 * absVelocityY)) {
                // Assume user wanted side to side movement
                if (velocityX > 0) {
                    // Left to right swipe -- previous window
                    mViewFlipper.showPrevious();
                } else {
                    // Right to left swipe -- next window
                    mViewFlipper.showNext();
                }
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * Intercepts keys before the view/terminal gets it.
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return backkeyInterceptor(keyCode, event) || keyboardShortcuts(keyCode, event);
        }

        /**
         * Keyboard shortcuts (tab management, paste)
         */
        private boolean keyboardShortcuts(int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            boolean isCtrlPressed = (event.getMetaState() & KeyEvent.META_CTRL_ON) != 0;
            boolean isShiftPressed = (event.getMetaState() & KeyEvent.META_SHIFT_ON) != 0;

            if (keyCode == KeyEvent.KEYCODE_TAB && isCtrlPressed) {
                if (isShiftPressed) {
                    mViewFlipper.showPrevious();
                } else {
                    mViewFlipper.showNext();
                }

                return true;
            } else if (keyCode == KeyEvent.KEYCODE_N && isCtrlPressed && isShiftPressed) {
                doCreateNewWindow();

                return true;
            } else {
                return false;
            }
        }

        /**
         * Make sure the back button always leaves the application.
         */
        private boolean backkeyInterceptor(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES && mActionBar.isShowing()) {
                /* We need to intercept the key event before the view sees it,
                   otherwise the view will handle it before we get it */
                onKeyUp(keyCode, event);
                return true;
            } else {
                return false;
            }
        }
    };

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.e(TAG, "onCreate");
        // 创建浮动框
        createFloatingWindow();
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSettings = new TermSettings(getResources(), mPrefs);
        mPrivateAlias = new ComponentName(this, RemoteInterface.PRIVACT_ACTIVITY_ALIAS);

        Intent broadcast = new Intent(ACTION_PATH_BROADCAST);
        /** SDK >= 13*/
        broadcast.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
        mPendingPathBroadcasts++;
        sendOrderedBroadcast(broadcast, PERMISSION_PATH_BROADCAST, mPathReceiver, null, RESULT_OK, null, null);

        broadcast = new Intent(broadcast);
        broadcast.setAction(ACTION_PATH_PREPEND_BROADCAST);
        mPendingPathBroadcasts++;
        sendOrderedBroadcast(broadcast, PERMISSION_PATH_PREPEND_BROADCAST, mPathReceiver, null, RESULT_OK, null, null);

        TSIntent = new Intent(this, TermService.class);
        startService(TSIntent);

        if (!bindService(TSIntent, mTSConnection, BIND_AUTO_CREATE)) {
            Log.w(TAG, "bind to service failed!");
        }
        
        /** SDK >= 13*/
        int actionBarMode = mSettings.actionBarMode();
        mActionBarMode = actionBarMode;
        switch (actionBarMode) {
        case TermSettings.ACTION_BAR_MODE_ALWAYS_VISIBLE:
            setTheme(R.style.Theme_Holo);
            break;
        case TermSettings.ACTION_BAR_MODE_HIDES:
            setTheme(R.style.Theme_Holo_ActionBarOverlay);
            break;
        }

        setContentView(R.layout.term_activity);
        mViewFlipper = (TermViewFlipper) findViewById(R.id.view_flipper);
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        int wifiLockMode = WifiManager.WIFI_MODE_FULL;
        /** SDK >= 13*/
        wifiLockMode = WIFI_MODE_FULL_HIGH_PERF;
        mWifiLock = wm.createWifiLock(wifiLockMode, TAG);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            mActionBar = actionBar;
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
            if (mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES) {
                actionBar.hide();
            }
        }

        mHaveFullHwKeyboard = checkHaveFullHwKeyboard(getResources().getConfiguration());

        updatePrefs();
        mAlreadyStarted = true;
        
    }

    private String makePathFromBundle(Bundle extras) {
        if (extras == null || extras.size() == 0) {
            return "";
        }

        String[] keys = new String[extras.size()];
        keys = extras.keySet().toArray(keys);
        Collator collator = Collator.getInstance(Locale.US);
        Arrays.sort(keys, collator);

        StringBuilder path = new StringBuilder();
        for (String key : keys) {
            String dir = extras.getString(key);
            if (dir != null && !dir.equals("")) {
                path.append(dir);
                path.append(":");
            }
        }

        return path.substring(0, path.length()-1);
    }

    private void populateViewFlipper() {
        if (mTermService != null) {
            mTermSessions = mTermService.getSessions();
            mTermSessions.addCallback(this);

            if (mTermSessions.size() == 0) {
                mTermSessions.add(createTermSession());
            }

            for (TermSession session : mTermSessions) {
                EmulatorView view = createEmulatorView(session);
                mViewFlipper.addView(view);
            }

            updatePrefs();

            Intent intent = getIntent();
            int flags = intent.getFlags();
            String action = intent.getAction();
            ComponentName component = intent.getComponent();
            if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0 &&
                    action != null && mPrivateAlias.equals(component)) {
                if (action.equals(RemoteInterface.PRIVACT_OPEN_NEW_WINDOW)) {
                    mViewFlipper.setDisplayedChild(mTermSessions.size()-1);
                } else if (action.equals(RemoteInterface.PRIVACT_SWITCH_WINDOW)) {
                    int target = intent.getIntExtra(RemoteInterface.PRIVEXTRA_TARGET_WINDOW, -1);
                    if (target >= 0) {
                        mViewFlipper.setDisplayedChild(target);
                    }
                }
            }

            mViewFlipper.resumeCurrentView();
        }
    }

    private void populateWindowList() {
        if (mActionBar == null) {
            // Not needed
            return;
        }

        if (mTermSessions != null) {
            int position = mViewFlipper.getDisplayedChild();
            WindowListAdapter adapter = mWinListAdapter;
            if (adapter == null) {
                adapter = new WindowListActionBarAdapter(mTermSessions);
                mWinListAdapter = adapter;

                SessionList sessions = mTermSessions;
                sessions.addCallback(adapter);
                sessions.addTitleChangedListener(adapter);
                mViewFlipper.addCallback(adapter);
                mActionBar.setListNavigationCallbacks(adapter, (OnNavigationListener) mWinListItemSelected);
            } else {
                adapter.setSessions(mTermSessions);
            }
            mActionBar.setSelectedNavigationItem(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewFlipper.removeAllViews();
        unbindService(mTSConnection);
        if (mStopServiceOnFinish) {
            stopService(TSIntent);
        }
        mTermService = null;
        mTSConnection = null;
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        
        destoryFloatingView();
    }

    private void restart() {
        startActivity(getIntent());
        finish();
    }

    protected static TermSession createTermSession(Context context, TermSettings settings, String initialCommand) {
        ShellTermSession session = new ShellTermSession(settings, initialCommand);
        session.setProcessExitMessage(context.getString(R.string.process_exit_message));

        return session;
    }

    private TermSession createTermSession() {
        TermSettings settings = mSettings;
        TermSession session = createTermSession(this, settings, null);
        session.setFinishCallback(mTermService);
        return session;
    }

    private TermView createEmulatorView(TermSession session) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        TermView emulatorView = new TermView(this, session, metrics);

        emulatorView.setExtGestureListener(new EmulatorViewGestureListener(emulatorView));
        emulatorView.setOnKeyListener(mKeyListener);
        registerForContextMenu(emulatorView);

        return emulatorView;
    }

    private TermSession getCurrentTermSession() {
        SessionList sessions = mTermSessions;
        if (sessions == null) {
            return null;
        } else {
            return sessions.get(mViewFlipper.getDisplayedChild());
        }
    }

    private EmulatorView getCurrentEmulatorView() {
        return (EmulatorView) mViewFlipper.getCurrentView();
    }

    private void updatePrefs() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mViewFlipper.updatePrefs(mSettings);

        for (View v : mViewFlipper) {
            ((EmulatorView) v).setDensity(metrics);
            ((TermView) v).updatePrefs(mSettings);
        }

        if (mTermSessions != null) {
            for (TermSession session : mTermSessions) {
                ((ShellTermSession) session).updatePrefs(mSettings);
            }
        }

        {
            Window win = getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            final int FULLSCREEN = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            int desiredFlag = mSettings.showStatusBar() ? 0 : FULLSCREEN;
            /** SDK >= 13*/
            if (desiredFlag != (params.flags & FULLSCREEN) || mActionBarMode != mSettings.actionBarMode()) {
                if (mAlreadyStarted) {
                    // Can't switch to/from fullscreen after
                    // starting the activity.
                    restart();
                } else {
                    win.setFlags(desiredFlag, FULLSCREEN);
                    if (mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES) {
                        mActionBar.hide();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SessionList sessions = mTermSessions;
        TermViewFlipper viewFlipper = mViewFlipper;
        if (sessions != null) {
            sessions.addCallback(this);
            WindowListAdapter adapter = mWinListAdapter;
            if (adapter != null) {
                sessions.addCallback(adapter);
                sessions.addTitleChangedListener(adapter);
                viewFlipper.addCallback(adapter);
            }
        }
        if (sessions != null && sessions.size() < viewFlipper.getChildCount()) {
            for (int i = 0; i < viewFlipper.getChildCount(); ++i) {
                EmulatorView v = (EmulatorView) viewFlipper.getChildAt(i);
                if (!sessions.contains(v.getTermSession())) {
                    v.onPause();
                    viewFlipper.removeView(v);
                    --i;
                }
            }
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // the HOME dir needs to be set here since it comes from Context
        SharedPreferences.Editor editor = mPrefs.edit();
        String defValue = getDir("HOME", MODE_PRIVATE).getAbsolutePath();
        String homePath = mPrefs.getString("home_path", defValue);
        editor.putString("home_path", homePath);
        editor.commit();

        mSettings.readPrefs(mPrefs);
        updatePrefs();

        if (onResumeSelectWindow >= 0) {
            viewFlipper.setDisplayedChild(onResumeSelectWindow);
            onResumeSelectWindow = -1;
        }
        viewFlipper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        SessionList sessions = mTermSessions;
        TermViewFlipper viewFlipper = mViewFlipper;

        viewFlipper.onPause();
        if (sessions != null) {
            sessions.removeCallback(this);
            WindowListAdapter adapter = mWinListAdapter;
            if (adapter != null) {
                sessions.removeCallback(adapter);
                sessions.removeTitleChangedListener(adapter);
                viewFlipper.removeCallback(adapter);
            }
        }

        /* Explicitly close the input method
           Otherwise, the soft keyboard could cover up whatever activity takes
           our place */
        final IBinder token = viewFlipper.getWindowToken();
        new Thread() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(token, 0);
            }
        }.start();
    }

    private boolean checkHaveFullHwKeyboard(Configuration c) {
        return (c.keyboard == Configuration.KEYBOARD_QWERTY) &&
            (c.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mHaveFullHwKeyboard = checkHaveFullHwKeyboard(newConfig);

        EmulatorView v = (EmulatorView) mViewFlipper.getCurrentView();
        if (v != null) {
            v.updateSize(false);
        }

        if (mWinListAdapter != null) {
            // Force Android to redraw the label in the navigation dropdown
            mWinListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.term_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_input_tools){
            toggleFloatingView();
        }else{
            if(isFloat){
                windowManager.removeView(mFloatingView);
                isFloat = false;
            }
        }
        
        if (id == R.id.menu_preferences) {
            doPreferences();
        } else if (id == R.id.menu_new_window) {
            doCreateNewWindow();
        } else if (id == R.id.menu_close_window) {
            confirmCloseWindow();
        } else if (id == R.id.menu_window_list) {
            startActivityForResult(new Intent(this, WindowList.class), REQUEST_CHOOSE_WINDOW);
        } else if (id == R.id.menu_special_keys) {
            doDocumentKeys();
        }  
        
        if (mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES) {
            mActionBar.hide();
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void doCreateNewWindow() {
        if (mTermSessions == null) {
            Log.w(TAG, "Couldn't create new window because mTermSessions == null");
            return;
        }

        TermSession session = createTermSession();
        mTermSessions.add(session);

        TermView view = createEmulatorView(session);
        view.updatePrefs(mSettings);

        mViewFlipper.addView(view);
        mViewFlipper.setDisplayedChild(mViewFlipper.getChildCount()-1);
    }

    private void confirmCloseWindow() {
        final AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setIcon(android.R.drawable.ic_dialog_alert);
        b.setMessage(R.string.confirm_window_close_message);
        final Runnable closeWindow = new Runnable() {
            public void run() {
                doCloseWindow();
            }
        };
        b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               dialog.dismiss();
               mHandler.post(closeWindow);
           }
        });
        b.setNegativeButton(android.R.string.no, null);
        b.show();
    }

    private void doCloseWindow() {
        if (mTermSessions == null) {
            return;
        }

        EmulatorView view = getCurrentEmulatorView();
        if (view == null) {
            return;
        }
        TermSession session = mTermSessions.remove(mViewFlipper.getDisplayedChild());
        view.onPause();
        session.finish();
        mViewFlipper.removeView(view);
        if (mTermSessions.size() == 0) {
            mStopServiceOnFinish = true;
            finish();
        } else {
            mViewFlipper.showNext();
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        switch (request) {
        case REQUEST_CHOOSE_WINDOW:
            if (result == RESULT_OK && data != null) {
                int position = data.getIntExtra(EXTRA_WINDOW_ID, -2);
                if (position >= 0) {
                    // Switch windows after session list is in sync, not here
                    onResumeSelectWindow = position;
                } else if (position == -1) {
                    doCreateNewWindow();
                    onResumeSelectWindow = mTermSessions.size() - 1;
                }
            } else {
                // Close the activity if user closed all sessions
                if (mTermSessions == null || mTermSessions.size() == 0) {
                    mStopServiceOnFinish = true;
                    finish();
                }
            }
            break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
            // Don't repeat action if intent comes from history
            return;
        }

        String action = intent.getAction();
        if (action == null || !mPrivateAlias.equals(intent.getComponent())) {
            return;
        }

        if (action.equals(RemoteInterface.PRIVACT_OPEN_NEW_WINDOW)) {
            // New session was created, add an EmulatorView to match
            SessionList sessions = mTermSessions;
            if (sessions == null) {
                // Presumably populateViewFlipper() will do this later ...
                return;
            }
            int position = sessions.size() - 1;

            TermSession session = sessions.get(position);
            EmulatorView view = createEmulatorView(session);

            mViewFlipper.addView(view);
            onResumeSelectWindow = position;
        } else if (action.equals(RemoteInterface.PRIVACT_SWITCH_WINDOW)) {
            int target = intent.getIntExtra(RemoteInterface.PRIVEXTRA_TARGET_WINDOW, -1);
            if (target >= 0) {
                onResumeSelectWindow = target;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem wakeLockItem = menu.findItem(R.id.menu_toggle_wakelock);
//        MenuItem wifiLockItem = menu.findItem(R.id.menu_toggle_wifilock);
//        if (mWakeLock.isHeld()) {
//            wakeLockItem.setTitle(R.string.disable_wakelock);
//        } else {
//            wakeLockItem.setTitle(R.string.enable_wakelock);
//        }
//        if (mWifiLock.isHeld()) {
//            wifiLockItem.setTitle(R.string.disable_wifilock);
//        } else {
//            wifiLockItem.setTitle(R.string.enable_wifilock);
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.setHeaderTitle(R.string.edit_text);
      menu.add(0, SEND_CONTROL_KEY_ID, 0, R.string.send_control_key);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
          switch (item.getItemId()) {
          case SEND_CONTROL_KEY_ID:
            doSendControlKey();
            return true;
          default:
            return super.onContextItemSelected(item);
          }
        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (mActionBarMode == TermSettings.ACTION_BAR_MODE_HIDES && mActionBar.isShowing()) {
                mActionBar.hide();
                return true;
            }
            doCloseWindow();
            return true;
            
        case KeyEvent.KEYCODE_MENU:
            if (mActionBar != null && !mActionBar.isShowing()) {
                mActionBar.show();
                return true;
            } else {
                return super.onKeyUp(keyCode, event);
            }
        default:
            return super.onKeyUp(keyCode, event);
        }
    }

    // Called when the list of sessions changes
    public void onUpdate() {
        SessionList sessions = mTermSessions;
        if (sessions == null) {
            return;
        }

        if (sessions.size() == 0) {
            mStopServiceOnFinish = true;
            finish();
        } else if (sessions.size() < mViewFlipper.getChildCount()) {
            for (int i = 0; i < mViewFlipper.getChildCount(); ++i) {
                EmulatorView v = (EmulatorView) mViewFlipper.getChildAt(i);
                if (!sessions.contains(v.getTermSession())) {
                    v.onPause();
                    mViewFlipper.removeView(v);
                    --i;
                }
            }
        }
    }

    private void doPreferences() {
        startActivity(new Intent(this, TermPreferences.class));
    }

    private void doResetTerminal() {
        TermSession session = getCurrentTermSession();
        if (session != null) {
            session.reset();
        }
    }

    private void doSendControlKey() {
        getCurrentEmulatorView().sendControlKey();
    }

    private void doDocumentKeys() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        Resources r = getResources();
        dialog.setTitle(r.getString(R.string.control_key_dialog_title));
        dialog.setMessage(r.getString(R.string.control_key_dialog_control_text));
        dialog.show();
     }

    /**
     * 打开或关闭软键盘
     */
    private void doToggleSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)
            getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    private void doToggleActionBar() {
        ActionBar bar = mActionBar;
        if (bar == null) {
            return;
        }
        if (bar.isShowing()) {
            bar.hide();
        } else {
            bar.show();
        }
    }

    private void doUIToggle(int x, int y, int width, int height) {
        switch (mActionBarMode) {
        case TermSettings.ACTION_BAR_MODE_NONE:
            if (mHaveFullHwKeyboard || y < height / 2) {
                openOptionsMenu();
                return;
            } else {
                doToggleSoftKeyboard();
            }
            break;
        case TermSettings.ACTION_BAR_MODE_ALWAYS_VISIBLE:
            if (!mHaveFullHwKeyboard) {
                doToggleSoftKeyboard();
            }
            break;
        case TermSettings.ACTION_BAR_MODE_HIDES:
            if (mHaveFullHwKeyboard || y < height / 2) {
                doToggleActionBar();
                return;
            } else {
                doToggleSoftKeyboard();
            }
            break;
        }
        getCurrentEmulatorView().requestFocus();
    }

    /**
     *
     * Send a URL up to Android to be handled by a browser.
     * @param link The URL to be opened.
     */
    private void execURL(String link)
    {
        Uri webLink = Uri.parse(link);
        Intent openLink = new Intent(Intent.ACTION_VIEW, webLink);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(openLink, 0);
        if(handlers.size() > 0)
            startActivity(openLink);
    }
    
    
    /**
     * create a floating window to show input tool.
     */
    public void createFloatingWindow() {
        isFloat = false;
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.term_input_tool, null);
        windowManager = (WindowManager) getSystemService("window");
        wmParams =  new WindowManager.LayoutParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.LEFT| Gravity.TOP;
        wmParams.x = 100;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;
        mFloatingView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY() - 25;
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
    
    /**
     * update the position of floating window.
     */
    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        if (mFloatingView != null) {
            windowManager.updateViewLayout(mFloatingView, wmParams);
        }
    }
    
    private void destoryFloatingView(){
        if (windowManager != null && isFloat) {
            windowManager.removeView(mFloatingView);
            mFloatingView = null;
        }
    }
    
    public void toggleFloatingView(){
        if (windowManager == null) return;
            
        if(isFloat){
            windowManager.removeView(mFloatingView);
            isFloat = false;
        }else{
            windowManager.addView(mFloatingView, wmParams);
            isFloat = true;
        }
    }
    
    public void termToolHandler(View v){
        TermSession  currentSession= getCurrentTermSession();
        switch(v.getId()){
            case R.id.button1:
                currentSession.write("/"); break;
            case R.id.button2:
                currentSession.write("|");break;
            case R.id.button3:
                currentSession.write("-");break;
            case R.id.button4:
                currentSession.write("_");break;
            case R.id.button5:
                currentSession.write("*");break;
                
            case R.id.button_piperight:
                currentSession.write(">");break;
                
            case R.id.button_up:
                currentSession.write(TermKeyListener.MY_KEYCODE_UP);break;
            case R.id.button_down:
                currentSession.write(TermKeyListener.MY_KEYCODE_DOWN);break;
            case R.id.button_left:
                currentSession.write(TermKeyListener.MY_KEYCODE_LEFT);break;
            case R.id.button_right:
                currentSession.write(TermKeyListener.MY_KEYCODE_RIGHT);break;
            case R.id.button_tab:
                currentSession.write(TermKeyListener.MY_KEYCODE_TAB);break;
                
        }
    }
}
