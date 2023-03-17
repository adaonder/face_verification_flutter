package com.example.test_face_verification;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test_face_verification.common.interfaces.OnResultListener;
import com.example.test_face_verification.common.interfaces.OnResultListenerAdapter;
import com.example.test_face_verification.common.util.DataUtil;
import com.example.test_face_verification.common.util.LogUtil;
import com.example.test_face_verification.common.util.StringUtil;
import com.example.test_face_verification.fv.activity.FaceVerificationActivity;
import com.example.test_face_verification.fv.data.FVData;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String CHANNEL = "faceDetection";
    private static final String FV_DATA = "FV_DATA";
    private static final String FV_TYPE = "FV_TYPE";
    private static final String METHOD_VERIFY = "METHOD_VERIFY";

    private boolean isFailResponse = true;


    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        BinaryMessenger messenger = flutterEngine.getDartExecutor().getBinaryMessenger();

        MethodChannel channel = new MethodChannel(messenger, CHANNEL);

        channel.setMethodCallHandler((call, result) -> {

            Log.e("**RED_LOG**", "TST call: " + call.method);
            Log.e("**RED_LOG**", "TST FV_DATA: " + call.argument(FV_DATA));
            Log.e("**RED_LOG**", "TST FV_TYPE: " + call.argument(FV_TYPE));

            if (METHOD_VERIFY.equals(call.method)) {

                isFailResponse = true;
                faceVerification(call.argument(FV_DATA), call.argument(FV_TYPE), new OnResultListenerAdapter<Boolean>() {
                    @Override
                    public void onSuccess(String message) {
                        LogUtil.e("onSuccess: " + message);
                        result.success(message);
                    }

                    @Override
                    public void onFail(String error) {
                        LogUtil.e(TAG, "error top: " + error);
                        if (isFailResponse) {
                            isFailResponse = false;
                            result.error("0", error, error);
                        }

                    }
                });
            } else {
                result.notImplemented();
            }
        });
    }

    private void faceVerification(String data, String fvType, OnResultListener<Boolean> listener) {

        try {
            FVData fvData = DataUtil.getModelStringData(data, FVData.class);

            if (StringUtil.isNotEmptyLink(fvData.getImageUrl())) {
                Intent intent = new Intent(getContext(), FaceVerificationActivity.newInstance(fvData, fvType, listener));
                startActivity(intent);
            } else {
                listener.onFail(getString(R.string.message_employee_image_url_is_empty));
            }
        } catch (Exception ex) {
            LogUtil.e("Test : " + ex.getMessage());
            listener.onFail(ex.getMessage());
        }
    }
}
