package com.example.test_face_verification.fv.asynctask;

import android.os.AsyncTask;

import com.example.test_face_verification.fv.NFV;
import com.example.test_face_verification.fv.config.SettingsUtil;
import com.neurotec.face.verification.client.NCapturePreviewEvent;
import com.neurotec.face.verification.client.NFaceVerificationClient;

public class InitializationTask extends AsyncTask<NFaceVerificationClient, Boolean, Boolean> {
    private OnListener listener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStart();
    }

    @Override
    protected Boolean doInBackground(NFaceVerificationClient... params) {
        try {
            // get NFV for the first time
            params[0] = NFV.getInstance();

            // load settings
            SettingsUtil.loadSettings();

            listener.onNFV(params[0]);

            params[0].setCapturePreviewListener(nCapturePreviewEvent -> listener.onCapturePreviewListener(nCapturePreviewEvent));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        listener.onFinish();
    }

    public interface OnListener {
        void onStart();

        void onNFV(NFaceVerificationClient nfv);

        void onCapturePreviewListener(NCapturePreviewEvent nCapturePreviewEvent);

        void onFinish();
    }


    public OnListener getListener() {
        return listener;
    }

    public void setListener(OnListener listener) {
        this.listener = listener;
    }
}
