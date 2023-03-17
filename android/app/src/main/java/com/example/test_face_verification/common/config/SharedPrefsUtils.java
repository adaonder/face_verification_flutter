package com.example.test_face_verification.common.config;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;


public final class SharedPrefsUtils {

    private static final SharedPreferences SHARED_PREFERENCES = AppConfig.getInstance().getSharedPreferences("timeware", Context.MODE_PRIVATE);

    private SharedPrefsUtils() {
    }

    public static String getString(String key) {
        return SHARED_PREFERENCES.getString(key, null);
    }

    public static Set<String> getStringSet(String key, Set<String> defValue) {
        return SHARED_PREFERENCES.getStringSet(key, defValue);
    }
    public static String getString(String key, String defValue) {
        return SHARED_PREFERENCES.getString(key, defValue);
    }

    public static boolean setString(String key, String value) {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static float getFloat(String key, float defaultValue) {
        return SHARED_PREFERENCES.getFloat(key, defaultValue);
    }

    public static boolean setFloat(String key, float value) {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static long getLong(String key) {
        return SHARED_PREFERENCES.getLong(key, 0);
    }

    public static long getLong(String key, long defaultValue) {
        return SHARED_PREFERENCES.getLong(key, defaultValue);
    }

    public static boolean setLong(String key, long value) {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static int getInt(String key) {
        return SHARED_PREFERENCES.getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        return SHARED_PREFERENCES.getInt(key, defaultValue);
    }

    public static boolean setInt(String key, int value) {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(String key) {
        return SHARED_PREFERENCES.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return SHARED_PREFERENCES.getBoolean(key, defaultValue);
    }

    public static boolean setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean isExists(String key) {
        return SHARED_PREFERENCES.contains(key);
    }

    public static void clear(String... keys) {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        for (String key : keys) {
            editor.remove(key).apply();
        }
    }

    public static void clearAll() {
        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
        editor.clear().apply();
    }
}
