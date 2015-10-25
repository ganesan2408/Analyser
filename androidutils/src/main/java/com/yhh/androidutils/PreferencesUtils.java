package com.yhh.androidutils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PreferencesUtils
 *
 */
public class PreferencesUtils {

    private static volatile PreferencesUtils instance;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String DEFAULT_NAME = "default";

    private PreferencesUtils(Context context){
        preferences = context.getSharedPreferences(DEFAULT_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static PreferencesUtils getInstance(Context context) {
        if(instance == null){
            synchronized (PreferencesUtils.class){
                if(instance == null){
                    instance = new PreferencesUtils(context);
                }
            }
        }
        return instance;
    }


    /**
     * 获取key对应的值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String get(String key, String defaultValue){
        return preferences.getString(key, defaultValue);
    }

    public int get(String key, int defaultValue){
        return preferences.getInt(key, defaultValue);
    }

    public long get(String key, long deaultValue){
        return preferences.getLong(key, deaultValue);
    }

    public float get(String key, float deaultValue){
        return preferences.getFloat(key, deaultValue);
    }

    public boolean get(String key, boolean deaultValue){
        return preferences.getBoolean(key, deaultValue);
    }

    /**
     * 给key设置对应的值
     *
     * @param key
     * @param value
     * @return
     */
    public boolean put(String key, String value){
        editor.putString(key, value);
        return editor.commit();
    }

    public boolean put(String key, int value){
        editor.putInt(key, value);
        return editor.commit();
    }

    public boolean put(String key, long value){
        editor.putLong(key, value);
        return editor.commit();
    }

    public boolean put(String key, float value){
        editor.putFloat(key, value);
        return editor.commit();
    }

    public boolean put(String key, boolean value){
        editor.putBoolean(key, value);
        return editor.commit();
    }

}
