package com.example.test_face_verification.fv.data;



import com.example.test_face_verification.R;

import java.io.Serializable;

public enum FVStatus implements Serializable {
    NONE(0, R.string.none),
    SUCCESS(1, R.string.success),
    TIMEOUT(2, R.string.timeout),
    CANCELED(3, R.string.cancelled),
    BAD_QUALITY(4, R.string.fv_bad_quality),
    MATCH_NOT_FOUND(5, R.string.fv_match_not_found),
    CAMERA_NOT_FOUND(6, R.string.fv_camera_not_found),
    FACE_NOT_FOUND(7, R.string.fc_face_not_found),
    LIVENESS_CHECK_FAILED(8, R.string.fv_liveness_check_failed),
    BAD_SHARPNESS(9, R.string.fv_bad_sharpness),
    TOO_NOISY(10, R.string.fv_too_noisy),
    BAD_LIGHTINING(11, R.string.fv_bad_lightining),
    OCCLUSION(12, R.string.fv_occlusion),
    BAD_POSE(13, R.string.fv_bad_pose),
    TOO_MANY_OBJECTS(14, R.string.fv_too_many_objects),
    MASK_DETECTED(15, R.string.fv_mask_detected),
    DUPLICATE_FOUND(16, R.string.fv_duplicate_found),
    DUPLICATE_ID(17, R.string.fv_duplicate_id),
    MOTION_BLUR(18, R.string.fv_motion_blur),
    COMPRESSION_ARTIFACTS(19, R.string.fv_compression_artifacts),
    TOO_FAR(20, R.string.fv_too_far),
    TOO_CLOSE(21, R.string.fv_too_close),
    INTERNAL_ERROR(999, R.string.internal_error);

    private int code, description;

    private FVStatus(int code, int description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public int getDescription() {
        return description;
    }
}
