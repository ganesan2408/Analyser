package com.yhh.terminal.util;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.KeyEvent;

import com.yhh.analyser.R;

/**
 * Terminal emulator settings
 */
public class TermSettings {
    private SharedPreferences mPrefs;

    private int mStatusBar;
    private int mActionBarMode;
    
    private int mCursorStyle;
    private int mCursorBlink;
    private int mFontSize;
    private int mColorId;
    private String mHomePath;

    private static final String STATUSBAR_KEY = "statusbar";
    private static final String ACTIONBAR_KEY = "actionbar";
    private static final String FONTSIZE_KEY = "fontsize";
    private static final String COLOR_KEY = "color";
    private static final String HOMEPATH_KEY = "home_path";

    public static final int WHITE               = 0xffffffff;
    public static final int BLACK               = 0xff000000;
    public static final int BLUE                = 0xff344ebd;
    public static final int GREEN               = 0xff00ff00;
    public static final int AMBER               = 0xffffb651;
    public static final int RED                 = 0xffff0113;
    public static final int HOLO_BLUE           = 0xff33b5e5;
    public static final int SOLARIZED_FG        = 0xff657b83;
    public static final int SOLARIZED_BG        = 0xfffdf6e3;
    public static final int SOLARIZED_DARK_FG   = 0xff839496;
    public static final int SOLARIZED_DARK_BG   = 0xff002b36;
    public static final int LINUX_CONSOLE_WHITE = 0xffaaaaaa;

    // foreground color, background color
    public static final int[][] COLOR_SCHEMES = {
        {BLACK,             WHITE},
        {WHITE,             BLACK},
        {WHITE,             BLUE},
        {GREEN,             BLACK},
        {AMBER,             BLACK},
        {RED,               BLACK},
        {HOLO_BLUE,         BLACK},
        {SOLARIZED_FG,      SOLARIZED_BG},
        {SOLARIZED_DARK_FG, SOLARIZED_DARK_BG},
        {LINUX_CONSOLE_WHITE, BLACK}
    };

    public static final int ACTION_BAR_MODE_NONE = 0;
    public static final int ACTION_BAR_MODE_ALWAYS_VISIBLE = 1;
    public static final int ACTION_BAR_MODE_HIDES = 2;
    private static final int ACTION_BAR_MODE_MAX = 2;

    /** An integer not in the range of real key codes. */
    public static final int KEYCODE_NONE = -1;

    public static final int[] CONTROL_KEY_SCHEMES = {
        KeyEvent.KEYCODE_VOLUME_UP,
        KeyEvent.KEYCODE_VOLUME_DOWN,
    };

    public TermSettings(Resources res, SharedPreferences prefs) {
        readDefaultPrefs(res);
        readPrefs(prefs);
    }

    private void readDefaultPrefs(Resources res) {
        mStatusBar = Integer.parseInt(res.getString(R.string.pref_statusbar_default));
        mActionBarMode = res.getInteger(R.integer.pref_actionbar_default);
        mCursorStyle = Integer.parseInt(res.getString(R.string.pref_cursorstyle_default));
        mCursorBlink = Integer.parseInt(res.getString(R.string.pref_cursorblink_default));
        mFontSize = Integer.parseInt(res.getString(R.string.pref_fontsize_default));
        mColorId = Integer.parseInt(res.getString(R.string.pref_color_default));
    }

    public void readPrefs(SharedPreferences prefs) {
        mPrefs = prefs;
        mStatusBar = readIntPref(STATUSBAR_KEY, mStatusBar, 1);
        mActionBarMode = readIntPref(ACTIONBAR_KEY, mActionBarMode, ACTION_BAR_MODE_MAX);
        mFontSize = readIntPref(FONTSIZE_KEY, mFontSize, 288);
        mColorId = readIntPref(COLOR_KEY, mColorId, COLOR_SCHEMES.length - 1);
        mHomePath = readStringPref(HOMEPATH_KEY, mHomePath);
        mPrefs = null;  // we leak a Context if we hold on to this
    }

    private int readIntPref(String key, int defaultValue, int maxValue) {
        int val;
        try {
            val = Integer.parseInt(
                mPrefs.getString(key, Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        val = Math.max(0, Math.min(val, maxValue));
        return val;
    }

    private String readStringPref(String key, String defaultValue) {
        return mPrefs.getString(key, defaultValue);
    }

    private boolean readBooleanPref(String key, boolean defaultValue) {
        return mPrefs.getBoolean(key, defaultValue);
    }

    public boolean showStatusBar() {
        return (mStatusBar != 0);
    }

    public int actionBarMode() {
        return mActionBarMode;
    }

    public int getCursorStyle() {
        return mCursorStyle;
    }

    public int getCursorBlink() {
        return mCursorBlink;
    }

    public int getFontSize() {
        return mFontSize;
    }

    public int[] getColorScheme() {
        return COLOR_SCHEMES[mColorId];
    }

    public void setHomePath(String homePath) {
        mHomePath = homePath;
    }

    public String getHomePath() {
        return mHomePath;
    }
}
