package com.example.test_face_verification.fv;

import com.neurotec.face.verification.client.NFaceVerificationClient;

public final class NFV {

    private static NFaceVerificationClient instance;

    protected NFV() {
    }

    public static synchronized NFaceVerificationClient getInstance() {
        if (instance == null) {
            instance = new NFaceVerificationClient(1);
        }
        return instance;
    }
}
