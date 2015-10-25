package com.yhh.androidutils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yuanhh1 on 2015/9/4.
 */
public class JSONUtils {

    public static boolean isEmpty(JSONObject jo){
        return jo == null || jo.length() <= 0;

    }

    public static boolean isEmpty(JSONArray ja){
        return ja == null || ja.length() <= 0;

    }

    public static String getString(JSONObject jo, String key, String defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static int getInt(JSONObject jo, String key, int defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static long getLong(JSONObject jo, String key, long defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static float getFloat(JSONObject jo, String key, float defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static double getDouble(JSONObject jo, String key, double defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static boolean getBoolean(JSONObject jo, String key, boolean defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static String[] getStringArray(JSONObject jo, String key, String[] defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static List<String> getStringList(JSONObject jo, String key,List<String> defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static JSONObject getJSONObject(JSONObject jo, String key, JSONObject defaultValue){
        return getValue(jo, key, defaultValue);
    }

    public static JSONArray getJSONArray(JSONObject jo, String key, JSONArray defaultValue){
        return getValue(jo, key, defaultValue);
    }


    public static <T> T getValue(JSONObject jo, String key, T defaultValue) {
        if (JSONUtils.isEmpty(jo) || defaultValue == null) {
            return defaultValue;
        }

        Object obj = null;
        try {
            obj = jo.get(key);
        } catch (JSONException e) {
            return defaultValue;
        }

        T value = defaultValue;
        if (obj != null && value.getClass().isAssignableFrom(obj.getClass())) {
            value = (T) obj;
        }
        return value;
    }

}
