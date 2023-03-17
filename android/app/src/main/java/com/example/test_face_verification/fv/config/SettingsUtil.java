package com.example.test_face_verification.fv.config;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;


import com.example.test_face_verification.common.config.SharedPrefsUtils;
import com.example.test_face_verification.fv.NFV;
import com.example.test_face_verification.fv.data.FaceVerificationType;
import com.neurotec.face.verification.client.NFaceVerificationClient;
import com.neurotec.face.verification.client.NIcaoWarnings;
import com.neurotec.face.verification.client.NLivenessMode;
import com.neurotec.face.verification.client.NVideoFormat;
import com.neurotec.face.verification.server.rest.ApiClient;
import com.squareup.okhttp.Protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsUtil {

    private static final String TAG = SettingsUtil.class.getName();

    public static final String KEY_PREF_LIVENESS_TH = "key_pref_liveness_th";
    public static final String KEY_PREF_PASSIVE_LIVENESS_SENSITIVITY_TH = "key_pref_passive_liveness_sensitivity_th";
    public static final String KEY_PREF_PASSIVE_LIVENESS_QUALITY_TH = "key_pref_passive_liveness_quality_th";
    public static final String KEY_PREF_QUALITY_TH = "key_pref_quality_th";
    public static final String KEY_PREF_USE_MMOC = "key_pref_use_mmoc";
    public static final String KEY_PREF_USE_MMABIS = "key_pref_use_mmabis";
    public static final String KEY_PREF_MATCHING_TH = "key_pref_matching_th";
    public static final String KEY_PREF_LIVENESS_MODE = "key_pref_liveness_mode";
    public static final String KEY_PREF_LIVENESS_BLINK_TIMEOUT = "key_pref_liveness_blink_timeout";
    public static final String KEY_PREF_CHECK_ICAO = "key_pref_check_icao";
    public static final String KEY_PREF_MANUAL_CAPTURING = "key_pref_manual_capturing";
    public static final String KEY_PREF_SATURATION_TH = "key_pref_saturation_th";
    public static final String KEY_PREF_SHARPNESS_TH = "key_pref_sharpness_th";
    public static final String KEY_PREF_BACKGROUD_UNIFORMITY_TH = "key_pref_background_uniformity_th";
    public static final String KEY_PREF_GRAYSCALE_DENSITY_TH = "key_pref_grayscale_density_th";
    public static final String KEY_PREF_LOOKING_AWAY_TH = "key_pref_looking_away_th";
    public static final String KEY_PREF_RED_EYE_TH = "key_pref_red_eye_th";
    public static final String KEY_PREF_FACE_DARKNESS_TH = "key_pref_face_darkness_th";
    public static final String KEY_PREF_UNNATURAL_SKIN_TONE_TH = "key_pref_unnatural_skin_tone_th";
    public static final String KEY_PREF_WASHED_OUT_TH = "key_pref_washed_out_th";
    public static final String KEY_PREF_PIXELATION_TH = "key_pref_pixelation_th";
    public static final String KEY_PREF_SKIN_REFLECTION_TH = "key_pref_skin_reflection_th";
    public static final String KEY_PREF_GLASSES_REFLECTION_TH = "key_pref_glasses_reflection_th";
    public static final String KEY_PREF_EXPRESSION_TH = "key_pref_expression_th";
    public static final String KEY_PREF_DARK_GLASSES_TH = "key_pref_dark_glasses_th";
    public static final String KEY_PREF_BLINK_TH = "key_pref_blink_th";
    public static final String KEY_PREF_MOUTH_OPEN_TH = "key_pref_mouth_open_th";
    public static final String KEY_PREF_ICAO_FILTER = "key_pref_icao_filter";
    public static final String KEY_PREF_CAMERA = "key_pref_camera";
    public static final String KEY_PREF_VIDEO_FORMAT = "key_pref_video_format_1";
    public static final String KEY_PREF_AUTHENTICATION_IP = "key_pref_authentication_ip";
    public static final String KEY_PREF_AUTHENTICATION_KEY = "key_pref_authentication_key";
    public static final String KEY_PREF_LIVENESS_CUSTOM_ACTIONS = "key_pref_custom_actions";
    public static final String KEY_PREF_SAVE_PREVIEW_IMAGES = "key_pref_save_preview_images";
    public static final String KEY_PREF_PREVIEW_IMAGE_BUFFER_SIZE = "key_pref_preview_image_buffer_size";

    public static final int PREF_LIVENESS_TH_DEFAULT_VALUE = 50;
    public static final int PREF_PASSIVE_LIVENESS_SENSITIVITY_TH_DEFAULT_VALUE = 30;
    public static final int PREF_PASSIVE_LIVENESS_QUALITY_TH_DEFAULT_VALUE = 40;
    public static final int PREF_LIVENESS_BLINK_TIMEOUT_DEFAULT_VALUE = 2;
    public static final int PREF_QUALITY_TH_DEFAULT_VALUE = 50;
    public static final int PREF_MATCHING_TH_DEFAULT_VALUE = 48;
    public static final String PREF_LIVENESS_MODE_DEFAULT_VALUE = "PASSIVE";
    public static final boolean PREF_CHECK_ICAO_DEFAULT_VALUE = false;
    public static final boolean PREF_MANUAL_CAPTURING_DEFAULT_VALUE = false;
    public static final String PREF_USE_MMOC_DEFAULT_VALUE = "OFF";
    public static final boolean PREF_ENROLL_TO_MMABIS_DEFAULT_VALUE = false;
    public static final int PREF_SATURATION_TH_DEFAULT_VALUE = 50;
    public static final int PREF_SHARPNESS_TH_DEFAULT_VALUE = 50;
    public static final int PREF_BACKGROUND_UNIFORMITY_TH_DEFAULT_VALUE = 0;
    public static final int PREF_GRAYSCALE_DENSITY_TH_DEFAULT_VALUE = 50;
    public static final int PREF_LOOKING_AWAY_TH_DEFAULT_VALUE = 50;
    public static final int PREF_RED_EYE_TH_DEFAULT_VALUE = 50;
    public static final int PREF_FACE_DARKNESS_TH_DEFAULT_VALUE = 50;
    public static final int PREF_UNNATURAL_SKIN_TONE_TH_DEFAULT_VALUE = 30;
    public static final int PREF_WASHED_OUT_TH_DEFAULT_VALUE = 50;
    public static final int PREF_PIXELATION_TH_DEFAULT_VALUE = 50;
    public static final int PREF_SKIN_REFLECTION_TH_DEFAULT_VALUE = 30;
    public static final int PREF_GLASSES_REFLECTION_TH_DEFAULT_VALUE = 50;
    public static final int PREF_EXPRESSION_TH_DEFAULT_VALUE = 5;
    public static final int PREF_DARK_GLASSES_TH_DEFAULT_VALUE = 5;
    public static final int PREF_BLINK_TH_DEFAULT_VALUE = 5;
    public static final int PREF_MOUTH_OPEN_TH_DEFAULT_VALUE = 5;
    public static final Set<String> PREF_ICAO_FILTER_DEFAULT_VALUE = new HashSet<String>(Collections.singletonList("BACKGROUND_UNIFORMITY"));
    public static final String PREF_DEFAULT_CAMERA = "Front";
    public static final String PREF_DEFAULT_VIDEO_FORMAT = "UNKNOWN 1280x720:30000";
    public static final String PREF_DEFAULT_AUTHENTICATION_IP = "https://licensing.faceverification.online/rs/";
    public static final String PREF_DEFAULT_AUTHENTICATION_KEY = "ijb476bil6eit7864bqkp802c5";
    public static final String PREF_LIVENESS_CUSTOM_ACTIONS_DEFAULT_VALUE = " ";
    public static final boolean PREF_SAVE_PREVIEW_IMAGES_DEFAULT_VALUE = false;
    public static final int PREF_PREVIEW_IMAGE_BUFFER_SIZE_DEFAULT_VALUE = 1000;

    private static final boolean DEBUG = false;
    private static final String PREVIEW_SIZE_SEPARATOR = "x";
    private static final String FPS_SEPARATOR = ":";
    private static final String NAME_SEPARATOR = " ";
    private static final String REGEX_FORMAT_PATTERN = "(.+)" + NAME_SEPARATOR + "(\\d+)" + PREVIEW_SIZE_SEPARATOR + "(\\d+)" + FPS_SEPARATOR + "(\\d+)";

    private static boolean mClientUpdateNeeded = true;
    private static boolean mSavePreviewImages = PREF_SAVE_PREVIEW_IMAGES_DEFAULT_VALUE;
    private static int mPreviewImageBufferSize = PREF_PREVIEW_IMAGE_BUFFER_SIZE_DEFAULT_VALUE;

    private ListPreference mCameraPreference = null;
    private ListPreference mVideoFormatPreference = null;


    public static synchronized boolean isUpdateClientNeeded() {
        return mClientUpdateNeeded;
    }

    public static synchronized boolean isSavePreviewImagesEnabled() {
        return mSavePreviewImages;
    }

    public static synchronized int getPreviewImageBufferSize() {
        return mPreviewImageBufferSize;
    }

    public static synchronized void updateClientAuthentification(ApiClient client) {
        try {
            String path = SharedPrefsUtils.getString(KEY_PREF_AUTHENTICATION_IP, PREF_DEFAULT_AUTHENTICATION_IP);
            if (DEBUG) Log.i("TEST", "Path: " + path);
            client.setBasePath(path);
            String key = SharedPrefsUtils.getString(KEY_PREF_AUTHENTICATION_KEY, PREF_DEFAULT_AUTHENTICATION_KEY);
            if (DEBUG) Log.i("TEST", "Key: " + key);
            client.setApiKey(key);
            if (DEBUG) Log.i("TEST", "Key++: " + key);

            client.getHttpClient().setProtocols(new ArrayList<Protocol>() {
                {
                    add(Protocol.HTTP_1_1);
                }
            });

            mClientUpdateNeeded = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeFaceVerificationType(String fvType) {
        NLivenessMode nLivenessMode;
        switch (FaceVerificationType.valueOf(fvType)) {
            case BASIC:
                nLivenessMode = NLivenessMode.PASSIVE;
                break;
            case EYE_BLINK:
                nLivenessMode = NLivenessMode.PASSIVE_WITH_BLINK;
                break;
            case HEAD_MOVE:
                nLivenessMode = NLivenessMode.SIMPLE;
                break;
            case EYE_BLINK_HEAD_MOVE:
                nLivenessMode = NLivenessMode.ACTIVE;
                break;
            default:
                nLivenessMode = NLivenessMode.PASSIVE_AND_ACTIVE;
                break;
        }

        SharedPrefsUtils.setString(SettingsUtil.KEY_PREF_LIVENESS_MODE, nLivenessMode.name());
    }

    public static void loadSettings() {
        NFaceVerificationClient instance = NFV.getInstance();

        String value = SharedPrefsUtils.getString(KEY_PREF_LIVENESS_MODE, PREF_LIVENESS_MODE_DEFAULT_VALUE);
        NLivenessMode livenessMode = NLivenessMode.valueOf(value);
        instance.setLivenessMode(livenessMode);

        value = SharedPrefsUtils.getString(KEY_PREF_LIVENESS_CUSTOM_ACTIONS, PREF_LIVENESS_CUSTOM_ACTIONS_DEFAULT_VALUE);
        instance.setLivenessCustomActionSequence(value);

        byte byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_LIVENESS_TH, PREF_LIVENESS_TH_DEFAULT_VALUE);
        instance.setLivenessThreshold(byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_PASSIVE_LIVENESS_SENSITIVITY_TH, PREF_PASSIVE_LIVENESS_SENSITIVITY_TH_DEFAULT_VALUE);
        instance.setPassiveLivenessSensitivityThreshold(byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_PASSIVE_LIVENESS_QUALITY_TH, PREF_PASSIVE_LIVENESS_QUALITY_TH_DEFAULT_VALUE);
        instance.setPassiveLivenessQualityThreshold(byteTh);

        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_QUALITY_TH, PREF_QUALITY_TH_DEFAULT_VALUE);
        instance.setQualityThreshold(byteTh);

        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_SATURATION_TH, PREF_SATURATION_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.SATURATION, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_SHARPNESS_TH, PREF_SHARPNESS_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.SHARPNESS, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_BACKGROUD_UNIFORMITY_TH, PREF_BACKGROUND_UNIFORMITY_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.BACKGROUND_UNIFORMITY, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_GRAYSCALE_DENSITY_TH, PREF_GRAYSCALE_DENSITY_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.GRAYSCALE_DENSITY, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_LOOKING_AWAY_TH, PREF_LOOKING_AWAY_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.LOOKING_AWAY, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_RED_EYE_TH, PREF_RED_EYE_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.RED_EYE, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_FACE_DARKNESS_TH, PREF_FACE_DARKNESS_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.FACE_DARKNESS, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_UNNATURAL_SKIN_TONE_TH, PREF_UNNATURAL_SKIN_TONE_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.UNNATURAL_SKIN_TONE, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_WASHED_OUT_TH, PREF_WASHED_OUT_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.WASHED_OUT, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_PIXELATION_TH, PREF_PIXELATION_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.PIXELATION, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_SKIN_REFLECTION_TH, PREF_SKIN_REFLECTION_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.SKIN_REFLECTION, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_GLASSES_REFLECTION_TH, PREF_GLASSES_REFLECTION_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.GLASSES_REFLECTION, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_EXPRESSION_TH, PREF_EXPRESSION_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.EXPRESSION, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_DARK_GLASSES_TH, PREF_DARK_GLASSES_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.DARK_GLASSES, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_BLINK_TH, PREF_BLINK_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.BLINK, byteTh);
        byteTh = (byte) SharedPrefsUtils.getInt(KEY_PREF_MOUTH_OPEN_TH, PREF_MOUTH_OPEN_TH_DEFAULT_VALUE);
        instance.setIcaoWarningThreshold(NIcaoWarnings.MOUTH_OPEN, byteTh);

        instance.setIcaoWarningFilter(icaoStringSetToEnumSet(SharedPrefsUtils.getStringSet(KEY_PREF_ICAO_FILTER, PREF_ICAO_FILTER_DEFAULT_VALUE)));

        int intTh = SharedPrefsUtils.getInt(KEY_PREF_MATCHING_TH, PREF_MATCHING_TH_DEFAULT_VALUE);
        instance.setMatchingThreshold(intTh);
        intTh = SharedPrefsUtils.getInt(KEY_PREF_LIVENESS_BLINK_TIMEOUT, PREF_LIVENESS_BLINK_TIMEOUT_DEFAULT_VALUE);
        instance.setLivenessBlinkTimeout(Math.max(intTh, 1) * 1000);

        boolean boolValue = SharedPrefsUtils.getBoolean(KEY_PREF_CHECK_ICAO, PREF_CHECK_ICAO_DEFAULT_VALUE);
        instance.setCheckIcaoCompliance(boolValue);
        boolValue = SharedPrefsUtils.getBoolean(KEY_PREF_MANUAL_CAPTURING, PREF_MANUAL_CAPTURING_DEFAULT_VALUE);
        instance.setUseManualExtraction(boolValue);
        boolValue = SharedPrefsUtils.getBoolean(KEY_PREF_USE_MMABIS, PREF_ENROLL_TO_MMABIS_DEFAULT_VALUE);
        instance.setEnrollToMMAbis(boolValue);

        mSavePreviewImages = SharedPrefsUtils.getBoolean(KEY_PREF_SAVE_PREVIEW_IMAGES, PREF_SAVE_PREVIEW_IMAGES_DEFAULT_VALUE);
        mPreviewImageBufferSize = SharedPrefsUtils.getInt(KEY_PREF_PREVIEW_IMAGE_BUFFER_SIZE, PREF_PREVIEW_IMAGE_BUFFER_SIZE_DEFAULT_VALUE);

        updateCamera(instance);
        updateVideoFormat(instance, true);
    }


    public static Boolean useMMABIS() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NFaceVerificationClient.getContext());
        return SharedPrefsUtils.getBoolean(KEY_PREF_USE_MMABIS, PREF_ENROLL_TO_MMABIS_DEFAULT_VALUE);
    }

    public static Boolean useFaceOval() {
        String value = SharedPrefsUtils.getString(KEY_PREF_LIVENESS_MODE, PREF_LIVENESS_MODE_DEFAULT_VALUE);
        NLivenessMode livenessMode = NLivenessMode.valueOf(value);
        return livenessMode == NLivenessMode.PASSIVE || livenessMode == NLivenessMode.PASSIVE_WITH_BLINK;
    }

    private static void updateCamera(NFaceVerificationClient instance) {
        String camera = SharedPrefsUtils.getString(KEY_PREF_CAMERA, PREF_DEFAULT_CAMERA);
        String[] cameras = instance.getAvailableCameraNames();
        for (String n : cameras) {
            if (n.contains(camera)) {
                if (DEBUG) Log.d(TAG, "Setting camera: " + n);
                instance.setCurrentCamera(n);
                break;
            }
        }
    }

    private static void updateVideoFormat(NFaceVerificationClient instance, boolean loadDefaultValue) {
        String videoFormat = loadDefaultValue ? PREF_DEFAULT_VIDEO_FORMAT : SharedPrefsUtils.getString(KEY_PREF_VIDEO_FORMAT, videoFormatToString(instance.getCurrentVideoFormat()));
        if (DEBUG) Log.d(TAG, "videoFormat: " + videoFormat);
        if (DEBUG) Log.d(TAG, "currentVideoFormat: " + videoFormatToString(instance.getCurrentVideoFormat()));
        boolean validFormat = false;
        Object[] formatValues = stringToVideoFormatValues(videoFormat);
        for (NVideoFormat format : instance.getAvailableVideoFormats()) {
            if (((loadDefaultValue || format.getMediaSubTypeAsString().equals(formatValues[0]))
                    && format.getWidth() == (int) formatValues[1])
                    && (format.getHeight() == (int) formatValues[2])
                    && (format.getFrameRate()[0] == (int) formatValues[3])) {
                instance.setCurrentVideoFormat(format);
                if (DEBUG) Log.d(TAG, "Setting format n: " + format.getMediaSubTypeAsString()
                        + " w: " + format.getWidth() + " h: " + format.getHeight() + " fps: " + format.getFrameRate()[0]);
                validFormat = true;
                break;
            }
        }

        if (!validFormat) {
            if (DEBUG)
                Log.d(TAG, "not valid setting: " + videoFormatToString(instance.getCurrentVideoFormat()) + " " + instance.getCurrentVideoFormat().getMediaSubType());
            SharedPrefsUtils.setString(KEY_PREF_VIDEO_FORMAT, videoFormatToString(instance.getCurrentVideoFormat()));
        }
    }

    private static String videoFormatToString(NVideoFormat format) {
        StringBuilder sb = new StringBuilder();
        sb.append(format.getMediaSubTypeAsString());
        sb.append(NAME_SEPARATOR);
        sb.append(format.getWidth());
        sb.append(PREVIEW_SIZE_SEPARATOR);
        sb.append(format.getHeight());
        sb.append(FPS_SEPARATOR);
        sb.append(format.getFrameRate()[0]);
        return sb.toString();
    }

    private static Object[] stringToVideoFormatValues(String vformat) {
        Pattern pattern = Pattern.compile(REGEX_FORMAT_PATTERN);
        Matcher matcher = pattern.matcher(vformat);
        if (matcher.find()) {
            Object[] result = new Object[4];
            result[0] = matcher.group(1); // name
            result[1] = Integer.parseInt(matcher.group(2)); // width
            result[2] = Integer.parseInt(matcher.group(3)); // height
            result[3] = Integer.parseInt(matcher.group(4)); // fps
            return result;
        } else {
            throw new IllegalArgumentException("Unable to parse video format " + vformat);
        }
    }

    private static EnumSet<NIcaoWarnings> icaoStringSetToEnumSet(Set<String> icaoWarnings) {
        EnumSet<NIcaoWarnings> icaoFilter = EnumSet.noneOf(NIcaoWarnings.class);
        for (String icaoWarning : icaoWarnings) {
            icaoFilter.add(NIcaoWarnings.valueOf(icaoWarning));
        }
        return icaoFilter;
    }
}