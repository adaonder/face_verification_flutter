package com.example.test_face_verification.common.interfaces;


public interface OnResultListener<T> {

    void onResult(T message);

    void onSuccess(String message);

    void onFail(String error);
}
