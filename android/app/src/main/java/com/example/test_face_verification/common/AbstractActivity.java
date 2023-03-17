package com.example.test_face_verification.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.example.test_face_verification.common.interfaces.OnResultListener;
import com.example.test_face_verification.common.util.LogUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import io.flutter.embedding.android.FlutterActivity;


public abstract class AbstractActivity<T extends ViewBinding> extends FlutterActivity {
    private final String TAG = AbstractActivity.class.getSimpleName();

    private T currentLayoutViewBind;

    private boolean backButtonEnabled;
    private boolean showing;

    public OnResultListener<Boolean> permissionListener;

    public AbstractActivity() {
        this(true);
    }

    public AbstractActivity(boolean backButtonEnabled) {
        this.backButtonEnabled = backButtonEnabled;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preInitViews(savedInstanceState);
        initContentView();
        setupViews();
        registerViewEvents();
        postInitViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeMethod();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseMethod();
    }


    @Override
    protected void onStart() {
        super.onStart();
        showing = true;
        onStartMethod();
    }

    @Override
    protected void onStop() {
        super.onStop();
        showing = false;
        onStopMethod();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    protected void preInitViews(Bundle savedInstanceState) {
    }

    private void initContentView() {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        try {
            Class<T> classTypeForVB = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            Method method = classTypeForVB.getMethod("inflate", LayoutInflater.class);
            ViewBinding viewBinding = (ViewBinding) method.invoke(classTypeForVB, getLayoutInflater());
            currentLayoutViewBind = classTypeForVB.cast(viewBinding);
            setContentView(currentLayoutViewBind.getRoot());
            System.out.println("Class instance name: " + classTypeForVB.getName());
        } catch (Exception e) {
            System.out.println("ClassNotFound!! Something wrong! " + e.getMessage());
        }
    }

    protected void setupViews() {
    }

    protected void registerViewEvents() {
    }

    protected void postInitViews() {
    }

    protected void onResumeMethod() {
    }

    protected void onPauseMethod() {
    }

    protected void onStartMethod() {
    }

    protected void onStopMethod() {
    }

    public void startActivity(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    public void startActivityAndFinish(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void startActivityAndFinish(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }


    public T getViews() {
        return currentLayoutViewBind;
    }


    public ActivityResultLauncher<String> requestPermissionLauncher = (new ComponentActivity()).registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        LogUtil.e("ActivityResultCallback callback isGranted: " + isGranted);
        if (permissionListener != null) {
            permissionListener.onResult(isGranted);
        }
    });
}
