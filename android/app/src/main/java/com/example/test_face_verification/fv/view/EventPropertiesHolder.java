package com.example.test_face_verification.fv.view;

import android.graphics.Rect;

import com.neurotec.face.verification.client.NCapturePreview;
import com.neurotec.face.verification.client.NIcaoWarnings;
import com.neurotec.face.verification.client.NLivenessAction;

import java.util.EnumSet;

public class EventPropertiesHolder {

    private EnumSet<NIcaoWarnings> mIcaoWarnings = null;
    private EnumSet<NLivenessAction> mLivenessAction = null;
    private float mLivenessTargetYaw = 0;
    private float mLivenessYaw = 0;
    private byte mLivenessScore = 0;
    private Rect mBoundingRect = null;
    private float mRoll = 0;

    public EventPropertiesHolder(NCapturePreview info) {
        mIcaoWarnings = info.getIcaoWarnings();
        mLivenessAction = info.getLivenessAction();
        mLivenessTargetYaw = info.getLivenessTargetYaw();
        mLivenessYaw = info.getYaw();
        mLivenessScore = info.getLivenessScore();
        mBoundingRect = info.getBoundingRectangle();
        mRoll = info.getRoll();
    }

    public EnumSet<NIcaoWarnings> getIcaoWarnings() {
        return mIcaoWarnings;
    }

    public EnumSet<NLivenessAction> getLivenessAction() {
        return mLivenessAction;
    }

    public float getLivenessTargetYaw() {
        return mLivenessTargetYaw;
    }

    public float getYaw() {
        return mLivenessYaw;
    }

    public byte getLivenessScore() {
        return mLivenessScore;
    }

    public Rect getBoundingRect() {
        return mBoundingRect;
    }

    public float getRoll() {
        return mRoll;
    }
}
