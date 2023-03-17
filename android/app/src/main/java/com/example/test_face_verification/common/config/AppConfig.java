package com.example.test_face_verification.common.config;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class AppConfig extends Application {

    public static final String TAG = Application.class.getName();

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    private List<String> readPayrolls = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
