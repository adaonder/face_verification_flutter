package com.example.test_face_verification.common.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.test_face_verification.BuildConfig;


public class LogUtil {

    public static void e(String text) {
        if (BuildConfig.DEBUG)
            Log.e("**RED LOG**", text);
    }

    public static void e(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.d("**RED LOG**", tag + " : " + text);
    }

    public static void d(String text) {
        if (BuildConfig.DEBUG)
            Log.d("**DEBUG**", text);
    }

    public static void d(String tag, String text) {
        if (BuildConfig.DEBUG)
            Log.d("**DEBUG**", tag + " : " + text);
    }

    public static void i(String text) {
        if (BuildConfig.DEBUG)
            Log.v("**ALWAYS DEBUG**", text);
    }

    public static void toast(Context context, String content, boolean important) {
        int length = important ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(context, content, length).show();
    }
}
