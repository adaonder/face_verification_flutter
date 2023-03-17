package com.example.test_face_verification.fv.activity;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.test_face_verification.R;
import com.example.test_face_verification.common.AbstractActivity;
import com.example.test_face_verification.common.interfaces.OnResultListener;
import com.example.test_face_verification.common.util.ControlUtil;
import com.example.test_face_verification.common.util.LogUtil;
import com.example.test_face_verification.common.util.StringUtil;
import com.example.test_face_verification.databinding.ActivityFaceVerificationBinding;
import com.example.test_face_verification.fv.NFV;
import com.example.test_face_verification.fv.asynctask.InitializationTask;
import com.example.test_face_verification.fv.config.SettingsUtil;
import com.example.test_face_verification.fv.data.FVData;
import com.example.test_face_verification.fv.data.FVStatus;
import com.example.test_face_verification.fv.helper.FVDatabaseHelper;
import com.neurotec.face.verification.client.NCapturePreviewEvent;
import com.neurotec.face.verification.client.NFaceVerificationClient;
import com.neurotec.face.verification.client.NOperationResult;
import com.neurotec.face.verification.client.NStatus;
import com.neurotec.face.verification.server.rest.ApiClient;
import com.neurotec.face.verification.server.rest.api.OperationApi;


import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class FaceVerificationActivity extends AbstractActivity<ActivityFaceVerificationBinding> {

    public static final String TAG = FaceVerificationActivity.class.getName();

    private int API_CLIENT_TIMEOUT = 60000;

    private FVDatabaseHelper mDBHelper;
    private NFaceVerificationClient mNFV = null;
    private byte[] mTemplateBuffer = null;

    private OperationApi mOperationApi;
    private boolean mAppClosing;

    InitializationTask initializationTask;


    public static FVData selectedFVData;
    public static OnResultListener<Boolean> onResultListener;


    public static Class<FaceVerificationActivity> newInstance(FVData selectedFVData, String faceVerificationType, OnResultListener<Boolean> listener) {
        SettingsUtil.changeFaceVerificationType(faceVerificationType);
        FaceVerificationActivity.onResultListener = listener;
        FaceVerificationActivity.selectedFVData = selectedFVData;
        return FaceVerificationActivity.class;
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        NFaceVerificationClient.setEnableLogging(true);

        // on application start you must set NCore context
        NFaceVerificationClient.setContext(this);
        mDBHelper = new FVDatabaseHelper(this);

        getViews().faceView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void postInitViews() {
        super.postInitViews();
        initializationTask = new InitializationTask();
        initializationTask.setListener(new InitializationTask.OnListener() {
            @Override
            public void onStart() {
                LogUtil.e("Test initializationTask onStart");
                new Handler(Looper.getMainLooper()).post(() -> getViews().tvInfo.setText(R.string.message_fv_initialising));
                getViews().progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNFV(NFaceVerificationClient nfv) {
                LogUtil.e("Test onNFV");
                checkUserFromDB();
            }

            @Override
            public void onCapturePreviewListener(NCapturePreviewEvent nCapturePreviewEvent) {
                getViews().faceView.setEvent(nCapturePreviewEvent);
            }

            @Override
            public void onFinish() {
                getViews().progress.setVisibility(View.GONE);
            }
        });
        initializationTask.execute(mNFV);
    }


    @Override
    protected void onResumeMethod() {
        super.onResumeMethod();
        mAppClosing = false;
        try {
            if ((mOperationApi == null) || (SettingsUtil.isUpdateClientNeeded())) {
                LogUtil.e("Update client");
                ApiClient client = new ApiClient();
                client.setConnectTimeout(API_CLIENT_TIMEOUT);
                SettingsUtil.updateClientAuthentification(client);
                mOperationApi = new OperationApi(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUserFromDB() {
        LogUtil.e("Test checkUserFromDB");

        FVData currentFVData = null;
        Set<FVData> fvDataList = mDBHelper.listData();
        if (ControlUtil.isData(fvDataList)) {
            for (FVData data : fvDataList) {
                LogUtil.e("Test data: " + data.getEmployeeId() + ", data: " + data.getImageUrl());
                if (data.getEmployeeId().equals(selectedFVData.getEmployeeId()) && data.getImageUrl().equals(selectedFVData.getImageUrl())) {
                    LogUtil.e("Selected data: " + data.getEmployeeId() + ", data: " + data.getImageUrl());
                    currentFVData = data;
                    break;
                }
            }
        }

        if (currentFVData != null) {
            LogUtil.e("Test checkUserFromDB var");
            new Handler(Looper.getMainLooper()).post(() -> {
                getViews().tvInfo.setText(R.string.message_fv_set_your_face_camera);
                getViews().faceView.setVisibility(View.VISIBLE);
            });

            byte[] template = mDBHelper.getTemplate(currentFVData);
            verify(template);
        } else {
            LogUtil.e("Test checkUserFromDB yok");
            new Handler(Looper.getMainLooper()).post(() -> {
                getViews().tvInfo.setText(R.string.message_fv_face_registration_progress);
            });
            //Kişinin datası yok ve ekle
            if (StringUtil.isNotEmpty(selectedFVData.getImageUrl())) {
                try {
                    URL imageUrl = new URL(selectedFVData.getImageUrl());
                    URLConnection urlConnection = imageUrl.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    createTemplateImportImage(IOUtils.toByteArray(is));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    finish();
                    onResultListener.onFail(ex.getMessage());
                }
            } else {
                LogUtil.e("Test checkUserFromDB showDialog");
                //Resim bulunamazsa uyarı verip kapatılıcaktır.
                finish();
                onResultListener.onFail(getString(R.string.message_employee_image_url_is_empty));
            }
        }
    }

    public void createTemplateImportImage(final byte[] data) {
        Log.i(TAG, "createTemplateImportImage");
        new Thread(() -> {
            try {
                Log.e(TAG, "createTemplate 1");

                // cancel in there are any other operations in progress
                NFV.getInstance().cancel();
                Log.e(TAG, "createTemplate 2 startImportImage");
                byte[] registrationKey = NFV.getInstance().startImportImage(data);

                Log.e(TAG, "createTemplate 3 - send server - internet required");
                byte[] serverKey = mOperationApi.validate(registrationKey);

                Log.e(TAG, "createTemplate 4");
                NOperationResult result = NFV.getInstance().finishOperation(serverKey);

                Log.e(TAG, "createTemplate result");

                if (!mAppClosing) {
                    getViews().faceView.setEventInfo(result);
                    if (result.getStatus() == NStatus.SUCCESS) {
                        mTemplateBuffer = result.getTemplate();//Yüz
                        boolean insertResultSucceeded = mDBHelper.insert(selectedFVData.getEmployeeId(), selectedFVData.getImageUrl(), mTemplateBuffer);

                        checkUserFromDB();

                        String message = String.format(getString(R.string.message_fv_operation_status), insertResultSucceeded ?
                                (String.format(getString(R.string.message_fv_enrollment_to_db_succeeded))) :
                                (String.format(getString(R.string.message_fv_enrollment_to_db_failed))));

                        LogUtil.d(TAG, message);
                        //DialogUtil.showDialog(getBaseActivity(), getString(R.string.face_verification), message);
                    } else {
                        finish();
                        onResultListener.onFail(String.format(getString(R.string.message_fv_operation_status),
                                getString(FVStatus.valueOf(result.getStatus().name()).getDescription())));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    //
                    finish();
                    onResultListener.onFail("FV_1: " + e.getMessage());
                });
            }
        }).start();
    }


    public void verify(final byte[] template) {
        Log.e(TAG, "verify 1");
        new Thread(() -> {
            try {
                Log.e(TAG, "verify 2");
                // cancel in there are any other operations in progress
                NFV.getInstance().cancel();
                if (template == null) {
                    if (!mAppClosing) {
                        finish();
                        onResultListener.onFail(getString(R.string.message_fv_buffer_is_null));
                    }
                    return;
                }

                LogUtil.e(TAG, "verify 3");

                NOperationResult result = NFV.getInstance().verify(template);
                if (!mAppClosing) {
                    getViews().faceView.setEventInfo(result);
                    if (result.getStatus() == NStatus.SUCCESS) {
                        // Bitir ve success dön login başarılı
                        new Handler(Looper.getMainLooper()).post(() -> {
                            finish();
                            onResultListener.onSuccess("");
                        });
                    } else {
                        String message = String.format(getString(R.string.message_fv_operation_status), String.format(getString(R.string.message_fv_verification_failed) + " \n\n" + getString(FVStatus.valueOf(result.getStatus().name()).getDescription())));
                        LogUtil.e(TAG, "verify 5, message: " + message);
                        finish();
                        onResultListener.onFail(getString(FVStatus.valueOf(result.getStatus().name()).getDescription()));
                    }
                }
            } catch (Throwable e) {
                finish();
                LogUtil.e(TAG, "verify 6, message: " + e.getMessage());
                onResultListener.onFail(e.getMessage());
            }
        }).start();
    }
}