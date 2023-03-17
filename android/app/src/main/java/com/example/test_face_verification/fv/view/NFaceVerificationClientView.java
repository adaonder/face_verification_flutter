package com.example.test_face_verification.fv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.os.ConditionVariable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.test_face_verification.R;
import com.example.test_face_verification.fv.config.SettingsUtil;
import com.neurotec.face.verification.client.NCapturePreview;
import com.neurotec.face.verification.client.NCapturePreviewEvent;
import com.neurotec.face.verification.client.NIcaoWarnings;
import com.neurotec.face.verification.client.NLivenessAction;
import com.neurotec.face.verification.client.NOperationResult;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class NFaceVerificationClientView extends ViewGroup {

    // ===========================================================
    // Public static fields
    // ===========================================================

    public static String DEFAULT_LIVENESS_TEXT_TURN_TO_TARGET = "Hedefe dönün";//Turn to target
    public static String DEFAULT_LIVENESS_TEXT_TURN_UP = "Yukarı Bakın";//Turn up
    public static String DEFAULT_LIVENESS_TEXT_TURN_DOWN = "Aşağı bakın";//Turn down
    public static String DEFAULT_LIVENESS_TEXT_TURN_LEFT = "Sola dönün";//Turn left
    public static String DEFAULT_LIVENESS_TEXT_TURN_RIGHT = "Sağa dönün";//Turn right
    public static String DEFAULT_LIVENESS_TEXT_TURN_TO_CENTER = "Merkeze dön";//Turn to center
    public static String DEFAULT_LIVENESS_TEXT_KEEP_ROTATING = "Başınızı çevirin";//Keep rotating yaw
    public static String DEFAULT_LIVENESS_TEXT_KEEP_ROTATING_WITH_SCORE = "Başınızı yavaşça çeviriniz, seviye: %d";//Keep rotating yaw, score: %d
    public static String DEFAULT_LIVENESS_TEXT_KEEP_STILL = "Kımıldama";//Keep still
    public static String DEFAULT_LIVENESS_TEXT_KEEP_STILL_WITH_SCORE = "Kımıldamadan durunuz, seviye: %d";//Keep still, score: %d
    public static String DEFAULT_LIVENESS_TEXT_BLINK = "Göz Kırpma";//Blink

    public static final String DEFAULT_LIVENESS_TEXT_MOVE_CLOSER = "Move closer";
    public static final String DEFAULT_LIVENESS_TEXT_MOVE_BACK = "Move back";

    public static final String KEY_LIVENESS_TEXT_TURN_TO_TARGET = "KeyTurnToTarget";
    public static final String KEY_LIVENESS_TEXT_TURN_UP = "KeyTurnUp";
    public static final String KEY_LIVENESS_TEXT_TURN_DOWN = "KeyTurnDown";
    public static final String KEY_LIVENESS_TEXT_TURN_LEFT = "KeyTurnLeft";
    public static final String KEY_LIVENESS_TEXT_TURN_RIGHT = "KeyTurnRight";
    public static final String KEY_LIVENESS_TEXT_TURN_TO_CENTER = "KeyTurnToCenter";
    public static final String KEY_LIVENESS_TEXT_KEEP_ROTATING = "KeyKeepRotating";
    public static final String KEY_LIVENESS_TEXT_KEEP_ROTATING_WITH_SCORE = "KeyKeepRotatingWithScore";
    public static final String KEY_LIVENESS_TEXT_KEEP_STILL = "KeyKeepStill";
    public static final String KEY_LIVENESS_TEXT_KEEP_STILL_WITH_SCORE = "KeyKeepStillWithScore";
    public static final String KEY_LIVENESS_TEXT_BLINK = "KeyBlink";
    public static final String KEY_LIVENESS_TEXT_MOVE_CLOSER = "KeyMoveCloser";
    public static final String KEY_LIVENESS_TEXT_MOVE_BACK = "KeyMoveBack";

    // ===========================================================
    // Private static fields
    // ===========================================================

    private static final String TAG = "NFaceVerificationView";
    private static final boolean DEBUG = false;

    private static final boolean DEFAULT_ROTATE_FACE_RECTANGLE = true;
    private static final boolean DEFAULT_SHOW_FACE_RECTANGLE = true;

    private static final int DEFAULT_FACE_RECTANGLE_WIDTH = 2;
    private static final int DEFAULT_LIVENESS_AREA_WIDTH = 2;
    private static final int DEFAULT_PAINT_TEXT_SIZE = 15;
    private static final int DEFAULT_LIVENESS_TEXT_SIZE = 15;
    private static final int DEFAULT_PAINT_COLOR = 0xFF33FF33;
    private static final int DEFAULT_LIVENESS_TEXT_COLOR = Color.YELLOW;
    private static final int DEFAULT_PAINT_TEXT_STROKE_WIDTH = 0;
    private static final int DEFAULT_LIVENESS_STATUS_SPACE = 10;

    private static final int DEFAULT_GREEN_FACE_OVAL_COLOR = 0xFF00FF00;
    private static final int DEFAULT_GREY_FACE_OVAL_COLOR = 0xFF808080;
    private static final int DEFAULT_FACE_OVAL_STROKE_WIDTH = 14;
    private static final float DEFAULT_FACE_OVAL_BORDER_PERCENTAGE = 0.10f; // 10%
    private static final float DEFAULT_FACE_OVAL_SIZE_PERCENTAGE = 1.5f; // 150%
    private static final boolean DEFAULT_SHOW_ICAO_ARROWS = true;
    private static final int DEFAULT_ICAO_ARROWS_COLOR = Color.RED;
    private static final int DEFAULT_ICAO_TEXT_COLOR = Color.RED;
    private static final int DEFAULT_ICAO_TEXT_SIZE = 20;
    private static final int DEFAULT_ICAO_ARROWS_STROKE_WIDTH = 4;

    // ===========================================================
    // Private fields
    // ===========================================================

    private Map<NIcaoWarnings, String> mIcaoWarningMap;
    private Map<String, String> mTextMap;

    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mFeaturePointPaint;
    private Paint mLivenessTextPaint;
    private Paint mLivenessAreaPaint;
    private Paint mGreenOvalPaint;
    private Paint mGreyOvalPaint;
    private Paint mStatusPaint;
    private float mScale = 1;
    private int mRotation = 0;
    private int mOrientation = 0;
    private Display mDisplay;

    private int mImageWidth;
    private int mImageHeight;
    private int mImageRotateFlipType;

    private String mStatusMsg = "";
    private int mCounterEvenStop = 0;
    private CountDownTimer mStatusTimer;

    private EventPropertiesHolder mEventProperties;
    private NImageTextureView mImageView;
    private NFaceAttributesView mAttributesView;

    private Bitmap mBitmap = null;
    private Bitmap mImageToRender = null;
    private EventPropertiesHolder mEventPropertiesToRender = null;

    private Matrix mImageTransformationMatrix = null;
    private Matrix mAttributeMatrix = null;
    private final Object lockObject = new Object();

    private OrientationEventListener mOrientationListener;

    private boolean mShowFaceRectangle = DEFAULT_SHOW_FACE_RECTANGLE;
    private boolean mRotateFaceRectangle = DEFAULT_ROTATE_FACE_RECTANGLE;

    private boolean mShowIcaoArrows = DEFAULT_SHOW_ICAO_ARROWS;
    private boolean mShowIcaoText = DEFAULT_SHOW_ICAO_ARROWS;
    private int mIcaoArrowsColor = DEFAULT_ICAO_ARROWS_COLOR;
    private int mIcaoTextColor = DEFAULT_ICAO_TEXT_COLOR;
    private int mIcaoTextSize = DEFAULT_ICAO_TEXT_SIZE;
    private int mIcaoArrowsWidth = DEFAULT_ICAO_ARROWS_STROKE_WIDTH;

    // ===========================================================
    // Public constructors
    // ===========================================================

    public NFaceVerificationClientView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initComponents(context);
    }

    public NFaceVerificationClientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponents(context);
    }

    public NFaceVerificationClientView(Context context) {
        super(context);
        initComponents(context);
    }

    // ===========================================================
    // Private methods
    // ===========================================================

    private void initComponents(Context context) {
        updateText(context);
        mOrientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int rotation) {
                int newRotation = 0;
                if (rotation >= 315 || ((rotation >= 0) && (rotation < 45))) {
                    newRotation = 0;
                } else if (rotation >= 45 && rotation < 135) {
                    newRotation = 90;
                } else if (rotation >= 135 && rotation < 225) {
                    newRotation = 180;
                } else if (rotation >= 225 && rotation < 315) {
                    newRotation = 270;
                }

                if (newRotation != mOrientation) {
                    mOrientation = newRotation;
//					updateView();
                }
            }
        };

        mTextMap = new HashMap<>();
        mTextMap.put(KEY_LIVENESS_TEXT_BLINK, DEFAULT_LIVENESS_TEXT_BLINK);
        mTextMap.put(KEY_LIVENESS_TEXT_KEEP_ROTATING, DEFAULT_LIVENESS_TEXT_KEEP_ROTATING);
        mTextMap.put(KEY_LIVENESS_TEXT_KEEP_ROTATING_WITH_SCORE, DEFAULT_LIVENESS_TEXT_KEEP_ROTATING_WITH_SCORE);
        mTextMap.put(KEY_LIVENESS_TEXT_KEEP_STILL, DEFAULT_LIVENESS_TEXT_KEEP_STILL);
        mTextMap.put(KEY_LIVENESS_TEXT_KEEP_STILL_WITH_SCORE, DEFAULT_LIVENESS_TEXT_KEEP_STILL_WITH_SCORE);
        mTextMap.put(KEY_LIVENESS_TEXT_TURN_DOWN, DEFAULT_LIVENESS_TEXT_TURN_DOWN);
        mTextMap.put(KEY_LIVENESS_TEXT_TURN_LEFT, DEFAULT_LIVENESS_TEXT_TURN_LEFT);
        mTextMap.put(KEY_LIVENESS_TEXT_TURN_TO_CENTER, DEFAULT_LIVENESS_TEXT_TURN_TO_CENTER);
        mTextMap.put(KEY_LIVENESS_TEXT_TURN_RIGHT, DEFAULT_LIVENESS_TEXT_TURN_RIGHT);
        mTextMap.put(KEY_LIVENESS_TEXT_TURN_UP, DEFAULT_LIVENESS_TEXT_TURN_UP);
        mTextMap.put(KEY_LIVENESS_TEXT_TURN_TO_TARGET, DEFAULT_LIVENESS_TEXT_TURN_TO_TARGET);
        mTextMap.put(KEY_LIVENESS_TEXT_MOVE_CLOSER, DEFAULT_LIVENESS_TEXT_MOVE_CLOSER);
        mTextMap.put(KEY_LIVENESS_TEXT_MOVE_BACK, DEFAULT_LIVENESS_TEXT_MOVE_BACK);

        mIcaoWarningMap = new HashMap<>();
        mIcaoWarningMap.put(NIcaoWarnings.FACE_NOT_DETECTED, context.getString(R.string.fv_face_not_detected));//"Yüz algılanmadı");//Face not detected
        mIcaoWarningMap.put(NIcaoWarnings.EXPRESSION, context.getString(R.string.fv_expression));//"İfade");//Expression
        mIcaoWarningMap.put(NIcaoWarnings.DARK_GLASSES, context.getString(R.string.fv_dark_glasses));//"Koyu gözlükler");//Dark glasses
        mIcaoWarningMap.put(NIcaoWarnings.BLINK, context.getString(R.string.fv_blink));//"Göz Kırpma");//Blink
        mIcaoWarningMap.put(NIcaoWarnings.MOUTH_OPEN, context.getString(R.string.fv_mouth_open));//"Mouth open");
        mIcaoWarningMap.put(NIcaoWarnings.LOOKING_AWAY, context.getString(R.string.fv_looking_away));//"Looking away");
        mIcaoWarningMap.put(NIcaoWarnings.RED_EYE, context.getString(R.string.fv_red_eye));//"Red eye");
        mIcaoWarningMap.put(NIcaoWarnings.FACE_DARKNESS, context.getString(R.string.fv_face_darkness));//"Face darkness");
        mIcaoWarningMap.put(NIcaoWarnings.UNNATURAL_SKIN_TONE, context.getString(R.string.fv_unnatural_skin_tone));//"Unnatural skin tone");
        mIcaoWarningMap.put(NIcaoWarnings.WASHED_OUT, context.getString(R.string.fv_colors_washed_out));//"Colors washed out");
        mIcaoWarningMap.put(NIcaoWarnings.PIXELATION, context.getString(R.string.fv_pixelation));//"Pixelation");
        mIcaoWarningMap.put(NIcaoWarnings.SKIN_REFLECTION, context.getString(R.string.fv_skin_reflection));//"Skin reflection");
        mIcaoWarningMap.put(NIcaoWarnings.GLASSES_REFLECTION, context.getString(R.string.fv_glasses_reflection));//"Glasses reflection");
        mIcaoWarningMap.put(NIcaoWarnings.ROLL_LEFT, context.getString(R.string.fv_roll_left));//"Sola Yuvarla");//Roll left
        mIcaoWarningMap.put(NIcaoWarnings.ROLL_RIGHT, context.getString(R.string.fv_roll_right));//"Sağa Yuvarla");//Roll right
        mIcaoWarningMap.put(NIcaoWarnings.YAW_LEFT, context.getString(R.string.fv_yaw_left));//"Yaw left");
        mIcaoWarningMap.put(NIcaoWarnings.YAW_RIGHT, context.getString(R.string.fv_yaw_right));//"Yaw right");
        mIcaoWarningMap.put(NIcaoWarnings.PITCH_UP, context.getString(R.string.fv_pitch_up));//"Pitch up");
        mIcaoWarningMap.put(NIcaoWarnings.PITCH_DOWN, context.getString(R.string.fv_pitch_down));//"Pitch down");
        mIcaoWarningMap.put(NIcaoWarnings.TOO_NEAR, context.getString(R.string.fv_too_near));//"Too near");
        mIcaoWarningMap.put(NIcaoWarnings.TOO_FAR, context.getString(R.string.fv_too_far));//"Too far");
        mIcaoWarningMap.put(NIcaoWarnings.TOO_NORTH, context.getString(R.string.fv_too_north));//"Too north");
        mIcaoWarningMap.put(NIcaoWarnings.TOO_SOUTH, context.getString(R.string.fv_too_south));//"Too south");
        mIcaoWarningMap.put(NIcaoWarnings.TOO_WEST, context.getString(R.string.fv_too_west));//"Too west");
        mIcaoWarningMap.put(NIcaoWarnings.TOO_EAST, context.getString(R.string.fv_too_east));//"Too east");
        mIcaoWarningMap.put(NIcaoWarnings.SHARPNESS, context.getString(R.string.fv_sharpness));//"Keskinlik");//Sharpness
        mIcaoWarningMap.put(NIcaoWarnings.GRAYSCALE_DENSITY, context.getString(R.string.fv_grayscale_density));//"Grayscale density");
        mIcaoWarningMap.put(NIcaoWarnings.SATURATION, context.getString(R.string.fv_saturation));//"Canlılık");//Saturation
        mIcaoWarningMap.put(NIcaoWarnings.BACKGROUND_UNIFORMITY, context.getString(R.string.fv_background_uniformity));//"Arka plan tekdüzeliği");//Background uniformity
        mIcaoWarningMap.put(NIcaoWarnings.HEAVY_FRAME, "Heavy frame");

        mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(DEFAULT_PAINT_COLOR);
        mLinePaint.setStrokeWidth(dipToPx(DEFAULT_FACE_RECTANGLE_WIDTH));

        mTextPaint = new Paint();
        mTextPaint.setColor(DEFAULT_PAINT_COLOR);
        mTextPaint.setTextSize(dipToPx(DEFAULT_PAINT_TEXT_SIZE));
        mTextPaint.setStrokeWidth(dipToPx(DEFAULT_PAINT_TEXT_STROKE_WIDTH));

        mFeaturePointPaint = new Paint();
        mFeaturePointPaint.setColor(DEFAULT_PAINT_COLOR);
        mFeaturePointPaint.setStyle(Paint.Style.FILL);

        mLivenessTextPaint = new Paint();
        mLivenessTextPaint.setColor(DEFAULT_LIVENESS_TEXT_COLOR);
        mLivenessTextPaint.setTextSize(dipToPx(DEFAULT_LIVENESS_TEXT_SIZE));
        mLivenessTextPaint.setStrokeWidth(dipToPx(DEFAULT_PAINT_TEXT_STROKE_WIDTH));

        mLivenessAreaPaint = new Paint();
        mLivenessAreaPaint.setStyle(Paint.Style.STROKE);
        mLivenessAreaPaint.setColor(DEFAULT_LIVENESS_TEXT_COLOR);
        mLivenessAreaPaint.setStrokeWidth(dipToPx(DEFAULT_LIVENESS_AREA_WIDTH));

        mGreenOvalPaint = new Paint();
        mGreenOvalPaint.setColor(DEFAULT_GREEN_FACE_OVAL_COLOR);
        mGreenOvalPaint.setStyle(Paint.Style.STROKE);
        mGreenOvalPaint.setStrokeWidth(DEFAULT_FACE_OVAL_STROKE_WIDTH);

        mGreyOvalPaint = new Paint();
        mGreyOvalPaint.setColor(DEFAULT_GREY_FACE_OVAL_COLOR);
        mGreyOvalPaint.setStyle(Paint.Style.STROKE);
        mGreyOvalPaint.setStrokeWidth(DEFAULT_FACE_OVAL_STROKE_WIDTH);

        mStatusPaint = new Paint();
        mStatusPaint.setColor(DEFAULT_LIVENESS_TEXT_COLOR);
        mStatusPaint.setStyle(Paint.Style.FILL);

        mImageView = new NImageTextureView(context);
        addView(mImageView);

        mAttributesView = new NFaceAttributesView(context);
        mAttributesView.setOpaque(false);
        addView(mAttributesView);

        mStatusTimer = new CountDownTimer(2000, 2000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                clearStatusText();
                updateView();
                cancel();
            }

        };

        if (DEBUG) Log.i(TAG, "NFaceVerificationView components initialised");
        //mFace = new NFace();
    }

    private void updateText(Context context) {
        DEFAULT_LIVENESS_TEXT_TURN_TO_TARGET = context.getString(R.string.fv_turn_to_target); //"Hedefe dönün";//Turn to target
        DEFAULT_LIVENESS_TEXT_TURN_UP = context.getString(R.string.fv_turn_up); //"Yukarı Bakın";//Turn up
        DEFAULT_LIVENESS_TEXT_TURN_DOWN = context.getString(R.string.fv_turn_down); //"Aşağı bakın";//Turn down
        DEFAULT_LIVENESS_TEXT_TURN_LEFT = context.getString(R.string.fv_turn_left); //"Sola dönün";//Turn left
        DEFAULT_LIVENESS_TEXT_TURN_RIGHT = context.getString(R.string.fv_turn_right); //"Sağa dönün";//Turn right
        DEFAULT_LIVENESS_TEXT_TURN_TO_CENTER = context.getString(R.string.fv_turn_to_center); //"Merkeze dön";//Turn to center
        DEFAULT_LIVENESS_TEXT_KEEP_ROTATING = context.getString(R.string.fv_keep_rotating_yaw); //"Başınızı çevirin";//Keep rotating yaw
        DEFAULT_LIVENESS_TEXT_KEEP_ROTATING_WITH_SCORE = context.getString(R.string.fv_keep_rotating_yaw_score); //"Başınızı yavaşça çeviriniz, seviye: %d";//Keep rotating yaw, score: %d
        DEFAULT_LIVENESS_TEXT_KEEP_STILL = context.getString(R.string.fv_keep_still); //"Kımıldama";//Keep still
        DEFAULT_LIVENESS_TEXT_KEEP_STILL_WITH_SCORE = context.getString(R.string.fv_keep_still_score); //"Kımıldamadan durunuz, seviye: %d";//Keep still, score: %d
        DEFAULT_LIVENESS_TEXT_BLINK = context.getString(R.string.fv_blink); //"Göz Kırpma";//Blink
    }

    private Path getArrowPath() {
        Path arrow = new Path();
        arrow.moveTo(32, 322);
        arrow.lineTo(31, 315);
        arrow.lineTo(29, 308);
        arrow.lineTo(34, 302);
        arrow.lineTo(63, 259);
        arrow.lineTo(92, 216);
        arrow.lineTo(120, 173);
        arrow.lineTo(90, 129);
        arrow.lineTo(61, 84);
        arrow.lineTo(31, 39);
        arrow.lineTo(32, 35);
        arrow.lineTo(30, 28);
        arrow.lineTo(33, 25);
        arrow.lineTo(40, 25);
        arrow.lineTo(47, 22);
        arrow.lineTo(53, 27);
        arrow.lineTo(145, 73);
        arrow.lineTo(236, 118);
        arrow.lineTo(327, 165);
        arrow.lineTo(337, 181);
        arrow.lineTo(317, 187);
        arrow.lineTo(306, 192);
        arrow.lineTo(220, 236);
        arrow.lineTo(134, 279);
        arrow.lineTo(47, 322);
        arrow.lineTo(42, 322);
        arrow.lineTo(37, 323);
        arrow.lineTo(32, 322);
        return arrow;
    }

    private Path getBlinkPath() {
        Path blink = new Path();
        blink.moveTo(135, 129);
        blink.lineTo(135, 129);
        blink.lineTo(135, 126);
        blink.lineTo(135, 122);
        blink.lineTo(135, 119);
        blink.lineTo(131, 119);
        blink.lineTo(127, 118);
        blink.lineTo(122, 118);
        blink.lineTo(120, 123);
        blink.lineTo(119, 129);
        blink.lineTo(115, 133);
        blink.lineTo(111, 132);
        blink.lineTo(102, 132);
        blink.lineTo(103, 126);
        blink.lineTo(104, 122);
        blink.lineTo(107, 117);
        blink.lineTo(107, 112);
        blink.lineTo(103, 110);
        blink.lineTo(100, 108);
        blink.lineTo(97, 106);
        blink.lineTo(93, 110);
        blink.lineTo(90, 115);
        blink.lineTo(85, 118);
        blink.lineTo(81, 116);
        blink.lineTo(71, 111);
        blink.lineTo(77, 106);
        blink.lineTo(79, 102);
        blink.lineTo(87, 97);
        blink.lineTo(81, 92);
        blink.lineTo(79, 88);
        blink.lineTo(73, 85);
        blink.lineTo(74, 80);
        blink.lineTo(78, 76);
        blink.lineTo(84, 73);
        blink.lineTo(90, 75);
        blink.lineTo(97, 78);
        blink.lineTo(101, 85);
        blink.lineTo(108, 90);
        blink.lineTo(124, 101);
        blink.lineTo(147, 103);
        blink.lineTo(166, 96);
        blink.lineTo(176, 92);
        blink.lineTo(183, 85);
        blink.lineTo(191, 78);
        blink.lineTo(195, 74);
        blink.lineTo(201, 73);
        blink.lineTo(205, 76);
        blink.lineTo(210, 78);
        blink.lineTo(213, 82);
        blink.lineTo(208, 86);
        blink.lineTo(206, 89);
        blink.lineTo(203, 93);
        blink.lineTo(200, 96);
        blink.lineTo(204, 101);
        blink.lineTo(208, 105);
        blink.lineTo(211, 110);
        blink.lineTo(208, 113);
        blink.lineTo(202, 123);
        blink.lineTo(198, 116);
        blink.lineTo(194, 113);
        blink.lineTo(192, 106);
        blink.lineTo(187, 107);
        blink.lineTo(182, 110);
        blink.lineTo(175, 113);
        blink.lineTo(180, 119);
        blink.lineTo(182, 123);
        blink.lineTo(186, 130);
        blink.lineTo(179, 132);
        blink.lineTo(175, 133);
        blink.lineTo(169, 136);
        blink.lineTo(168, 130);
        blink.lineTo(166, 126);
        blink.lineTo(166, 118);
        blink.lineTo(161, 118);
        blink.lineTo(158, 119);
        blink.lineTo(154, 119);
        blink.lineTo(150, 119);
        blink.lineTo(150, 125);
        blink.lineTo(150, 132);
        blink.lineTo(150, 138);
        blink.lineTo(145, 138);
        blink.lineTo(140, 138);
        blink.lineTo(135, 138);
        blink.lineTo(135, 135);
        blink.lineTo(135, 132);
        blink.lineTo(135, 129);
        return blink;
    }

    private Path getTargetPath() {
        Path target = new Path();
        target.addCircle(40, 40, 40, Direction.CW);
        target.addCircle(40, 40, 30, Direction.CW);
        target.addCircle(40, 40, 20, Direction.CW);
        target.addCircle(40, 40, 10, Direction.CW);
        return target;
    }

    protected static Path getRollPath() {
        Path roll = new Path();
        roll.moveTo(10, 297);
        roll.lineTo(0, 301);
        roll.lineTo(2, 290);
        roll.lineTo(5, 282);
        roll.lineTo(24, 210);
        roll.lineTo(72, 147);
        roll.lineTo(134, 106);
        roll.lineTo(189, 68);
        roll.lineTo(254, 46);
        roll.lineTo(321, 42);
        roll.lineTo(329, 41);
        roll.lineTo(354, 43);
        roll.lineTo(333, 36);
        roll.lineTo(322, 27);
        roll.lineTo(295, 30);
        roll.lineTo(296, 11);
        roll.lineTo(293, 1);
        roll.lineTo(299, 3);
        roll.lineTo(306, 6);
        roll.lineTo(340, 21);
        roll.lineTo(375, 34);
        roll.lineTo(408, 49);
        roll.lineTo(373, 68);
        roll.lineTo(338, 87);
        roll.lineTo(302, 104);
        roll.lineTo(300, 99);
        roll.lineTo(297, 86);
        roll.lineTo(305, 84);
        roll.lineTo(320, 76);
        roll.lineTo(334, 69);
        roll.lineTo(349, 61);
        roll.lineTo(300, 61);
        roll.lineTo(250, 69);
        roll.lineTo(205, 89);
        roll.lineTo(118, 124);
        roll.lineTo(45, 199);
        roll.lineTo(22, 291);
        roll.lineTo(22, 299);
        roll.lineTo(16, 297);
        roll.lineTo(10, 297);
        roll.close();
        return roll;
    }

    protected static Path getYawPath() {
        Path yaw = new Path();
        yaw.moveTo(21, 102);
        yaw.lineTo(14, 95);
        yaw.lineTo(7, 89);
        yaw.lineTo(1, 81);
        yaw.lineTo(9, 70);
        yaw.lineTo(20, 61);
        yaw.lineTo(29, 51);
        yaw.lineTo(33, 48);
        yaw.lineTo(38, 41);
        yaw.lineTo(42, 40);
        yaw.lineTo(44, 47);
        yaw.lineTo(43, 55);
        yaw.lineTo(43, 62);
        yaw.lineTo(64, 62);
        yaw.lineTo(85, 59);
        yaw.lineTo(106, 53);
        yaw.lineTo(117, 50);
        yaw.lineTo(128, 45);
        yaw.lineTo(136, 36);
        yaw.lineTo(142, 30);
        yaw.lineTo(139, 44);
        yaw.lineTo(140, 48);
        yaw.lineTo(139, 57);
        yaw.lineTo(140, 67);
        yaw.lineTo(134, 73);
        yaw.lineTo(126, 83);
        yaw.lineTo(113, 89);
        yaw.lineTo(101, 92);
        yaw.lineTo(82, 98);
        yaw.lineTo(63, 100);
        yaw.lineTo(43, 101);
        yaw.lineTo(43, 108);
        yaw.lineTo(44, 115);
        yaw.lineTo(42, 121);
        yaw.lineTo(38, 120);
        yaw.lineTo(33, 113);
        yaw.lineTo(29, 110);
        yaw.lineTo(26, 107);
        yaw.lineTo(23, 105);
        yaw.lineTo(21, 102);

        yaw.moveTo(120, 34);
        yaw.lineTo(109, 26);
        yaw.lineTo(96, 23);
        yaw.lineTo(83, 21);
        yaw.lineTo(83, 14);
        yaw.lineTo(83, 7);
        yaw.lineTo(83, 0);
        yaw.lineTo(98, 2);
        yaw.lineTo(114, 6);
        yaw.lineTo(126, 14);
        yaw.lineTo(132, 18);
        yaw.lineTo(134, 27);
        yaw.lineTo(130, 32);
        yaw.lineTo(127, 36);
        yaw.lineTo(124, 38);
        yaw.lineTo(120, 34);
        return yaw;
    }

    protected static Path getMovePath() {
        Path move = new Path();
        move.moveTo(90, 105);
        move.lineTo(90, 100);
        move.lineTo(90, 95);
        move.lineTo(90, 90);
        move.lineTo(60, 90);
        move.lineTo(30, 90);
        move.lineTo(0, 90);
        move.lineTo(0, 70);
        move.lineTo(0, 50);
        move.lineTo(0, 30);
        move.lineTo(30, 30);
        move.lineTo(60, 30);
        move.lineTo(90, 29);
        move.lineTo(91, 20);
        move.lineTo(91, 10);
        move.lineTo(91, 0);
        move.lineTo(121, 20);
        move.lineTo(152, 39);
        move.lineTo(182, 59);
        move.lineTo(180, 64);
        move.lineTo(170, 68);
        move.lineTo(165, 72);
        move.lineTo(140, 88);
        move.lineTo(116, 104);
        move.lineTo(91, 120);
        move.lineTo(91, 115);
        move.lineTo(91, 110);
        move.lineTo(90, 105);

        move.moveTo(136, 88);
        move.lineTo(150, 79);
        move.lineTo(165, 71);
        move.lineTo(178, 61);
        move.lineTo(176, 55);
        move.lineTo(166, 52);
        move.lineTo(161, 48);
        move.lineTo(138, 33);
        move.lineTo(115, 18);
        move.lineTo(92, 3);
        move.lineTo(91, 12);
        move.lineTo(92, 22);
        move.lineTo(92, 31);
        move.lineTo(62, 31);
        move.lineTo(32, 31);
        move.lineTo(2, 31);
        move.lineTo(2, 50);
        move.lineTo(2, 70);
        move.lineTo(2, 89);
        move.lineTo(32, 89);
        move.lineTo(62, 89);
        move.lineTo(92, 89);
        move.lineTo(92, 98);
        move.lineTo(91, 108);
        move.lineTo(93, 117);
        move.lineTo(107, 108);
        move.lineTo(122, 98);
        move.lineTo(136, 88);
        move.close();
        return move;
    }

    protected static Path getPitchPath() {
        Path pitch = new Path();
        pitch.moveTo(92, 45);
        pitch.lineTo(89, 45);
        pitch.lineTo(87, 45);
        pitch.lineTo(84, 45);
        pitch.lineTo(83, 72);
        pitch.lineTo(80, 100);
        pitch.lineTo(72, 126);
        pitch.lineTo(69, 134);
        pitch.lineTo(64, 141);
        pitch.lineTo(57, 147);
        pitch.lineTo(50, 151);
        pitch.lineTo(42, 149);
        pitch.lineTo(34, 149);
        pitch.lineTo(29, 150);
        pitch.lineTo(27, 146);
        pitch.lineTo(32, 144);
        pitch.lineTo(41, 132);
        pitch.lineTo(43, 117);
        pitch.lineTo(46, 102);
        pitch.lineTo(49, 84);
        pitch.lineTo(51, 65);
        pitch.lineTo(51, 45);
        pitch.lineTo(46, 45);
        pitch.lineTo(37, 47);
        pitch.lineTo(34, 43);
        pitch.lineTo(36, 37);
        pitch.lineTo(42, 32);
        pitch.lineTo(46, 26);
        pitch.lineTo(53, 17);
        pitch.lineTo(59, 8);
        pitch.lineTo(67, 0);
        pitch.lineTo(78, 12);
        pitch.lineTo(88, 26);
        pitch.lineTo(98, 40);
        pitch.lineTo(103, 46);
        pitch.lineTo(97, 45);
        pitch.lineTo(92, 45);

        pitch.moveTo(51, 22);
        pitch.lineTo(45, 30);
        pitch.lineTo(40, 37);
        pitch.lineTo(35, 44);
        pitch.lineTo(40, 44);
        pitch.lineTo(46, 44);
        pitch.lineTo(52, 44);
        pitch.lineTo(52, 72);
        pitch.lineTo(49, 100);
        pitch.lineTo(42, 127);
        pitch.lineTo(40, 135);
        pitch.lineTo(36, 143);
        pitch.lineTo(30, 148);
        pitch.lineTo(38, 148);
        pitch.lineTo(47, 149);
        pitch.lineTo(55, 146);
        pitch.lineTo(65, 140);
        pitch.lineTo(69, 128);
        pitch.lineTo(73, 117);
        pitch.lineTo(80, 93);
        pitch.lineTo(81, 68);
        pitch.lineTo(83, 44);
        pitch.lineTo(88, 44);
        pitch.lineTo(94, 44);
        pitch.lineTo(99, 44);
        pitch.lineTo(89, 30);
        pitch.lineTo(78, 15);
        pitch.lineTo(67, 1);
        pitch.lineTo(62, 8);
        pitch.lineTo(56, 15);
        pitch.lineTo(51, 22);

        pitch.moveTo(28, 138);
        pitch.lineTo(23, 146);
        pitch.lineTo(12, 140);
        pitch.lineTo(11, 133);
        pitch.lineTo(4, 120);
        pitch.lineTo(2, 106);
        pitch.lineTo(1, 92);
        pitch.lineTo(3, 86);
        pitch.lineTo(13, 89);
        pitch.lineTo(17, 90);
        pitch.lineTo(20, 95);
        pitch.lineTo(19, 102);
        pitch.lineTo(22, 108);
        pitch.lineTo(24, 116);
        pitch.lineTo(27, 124);
        pitch.lineTo(31, 132);
        pitch.lineTo(3, 135);
        pitch.lineTo(29, 137);
        pitch.lineTo(28, 138);

        pitch.moveTo(27, 128);
        pitch.lineTo(21, 117);
        pitch.lineTo(20, 104);
        pitch.lineTo(17, 92);
        pitch.lineTo(13, 89);
        pitch.lineTo(6, 90);
        pitch.lineTo(2, 91);
        pitch.lineTo(4, 106);
        pitch.lineTo(7, 123);
        pitch.lineTo(15, 137);
        pitch.lineTo(18, 144);
        pitch.lineTo(31, 139);
        pitch.lineTo(29, 132);
        pitch.lineTo(28, 130);
        pitch.lineTo(27, 129);
        pitch.lineTo(27, 128);
        return pitch;
    }

    private Path preparePath(Path path, Rect area, boolean mirror) {
        Matrix matrix = new Matrix();
        RectF pathRect = new RectF();
        path.computeBounds(pathRect, true);
        path.offset(-pathRect.left, -pathRect.top);

        if (mirror) {
            matrix.postConcat(getMirrorMatrix());
            matrix.postTranslate(pathRect.width(), 0);
        }

        float scale = Math.min(area.width() / pathRect.width(), area.height() / pathRect.height());
        matrix.postScale(scale, scale);

        matrix.postTranslate(area.left, area.top);

        Path result = new Path();
        result.addPath(path, matrix);
        return result;
    }

    private void setEventInfoInternal(NCapturePreview eventInfo) {
        NOperationResult result = eventInfo instanceof NOperationResult ? (NOperationResult) eventInfo : null;
        if (result != null && result.getTokenImage() != null) {
            mBitmap = result.getTokenImage();
            mEventProperties = null;
        } else {
            mEventProperties = new EventPropertiesHolder(eventInfo);
            mBitmap = eventInfo.getImage();
        }
        updateView();
    }

    private synchronized void updateView() {
        if (DEBUG) Log.d(TAG, "Updating view");
        updateImage();
        updateAttributes();
    }

    private void updateAttributes() {
        if (DEBUG) Log.d(TAG, "Updating attributes");
        mAttributesView.postAttributesAndEvent(mEventProperties);
    }

    private int getRotation(int rotation) {
        if (rotation != -1) {
            return (rotation & 3) * 90;
        } else {
            return 0;
        }
    }

    private boolean isFlipY(int rotation) {
        if (rotation != -1) {
            return (rotation & 4) == 4;
        } else {
            return false;
        }
    }

    private synchronized void updateImage() {
        if (DEBUG) Log.d(TAG, "Updating image");
        int imageWidth = 0;
        int imageHeight = 0;
        int rotation = getRotation(mImageRotateFlipType);
        Bitmap imageToDraw = null;

        if (mBitmap != null) {
            imageToDraw = Bitmap.createBitmap(mBitmap);
            imageWidth = imageToDraw.getWidth();
            imageHeight = imageToDraw.getHeight();
        }

        if (imageWidth != mImageWidth || imageHeight != mImageHeight || rotation != mRotation) {
            mImageWidth = imageWidth;
            mImageHeight = imageHeight;
            mRotation = rotation;
            post(this::requestLayout);
        }
        if (DEBUG) Log.d(TAG, "mImageView Width: " + mImageView.getWidth() + "Image Height: " + mImageView.getHeight());
        mImageView.postImage(imageToDraw);
    }


    private float dipToPx(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private Matrix getMirrorMatrix() {
        float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
        Matrix matrixMirrorY = new Matrix();
        matrixMirrorY.setValues(mirrorY);
        return matrixMirrorY;
    }

    private int displayRotationToAngle(int displayRotation) {
        int angle = 0;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                angle = 0;
                break;
            case Surface.ROTATION_90:
                angle = 90;
                break;
            case Surface.ROTATION_180:
                angle = 180;
                break;
            case Surface.ROTATION_270:
                angle = 270;
                break;
            default:
                throw new AssertionError("Not recognised display rotation");
        }
        return angle;
    }

    private void setStatusText(String status) {
        mStatusMsg = status.replace("_", " ");
    }

    private void clearStatusText() {
        mStatusMsg = "";
    }

    private void startClearStatusTimer() {
        mStatusTimer.start();
    }

    // ===========================================================
    // Protected methods
    // ===========================================================

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mOrientationListener.enable();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOrientationListener.disable();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (DEBUG) Log.d(TAG, "layoutSurface");
        int rotation = 0;
        RectF parentBounds = new RectF(l, t, r, b);
        RectF imageBounds = new RectF(0, 0, mImageWidth, mImageHeight);
        RectF rotatedImageBounds = new RectF();

        RectF childBoundsF = new RectF();
        RectF rotatedScaledChildLayout = new RectF();

        Matrix layoutMatrix = new Matrix();
        Matrix attributesViewTransformationMatrix = new Matrix();

        layoutMatrix.postRotate(rotation, imageBounds.width() / 2, imageBounds.height() / 2);
        layoutMatrix.mapRect(rotatedImageBounds, imageBounds);

        if (imageBounds.width() != 0 && imageBounds.height() != 0) {
            mScale = Math.min((float) parentBounds.width() / rotatedImageBounds.width(), (float) parentBounds.height() / rotatedImageBounds.height());
        } else {
            mScale = 1;
        }

        layoutMatrix.postScale(mScale, mScale);
        layoutMatrix.mapRect(childBoundsF, imageBounds);

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout((int) (parentBounds.width() - childBoundsF.width()) / 2,
                    (int) (parentBounds.height() - childBoundsF.height()) / 2,
                    (int) (parentBounds.width() + childBoundsF.width()) / 2,
                    (int) (parentBounds.height() + childBoundsF.height()) / 2);
        }

        Matrix imageViewTransformationMatrix = new Matrix();
        imageViewTransformationMatrix.postRotate(rotation, childBoundsF.width() / 2, childBoundsF.height() / 2);
        imageViewTransformationMatrix.mapRect(rotatedScaledChildLayout, childBoundsF);
        imageViewTransformationMatrix.postScale((float) childBoundsF.width() / rotatedScaledChildLayout.width(), (float) childBoundsF.height() / rotatedScaledChildLayout.height(),
                (float) childBoundsF.width() / 2, (float) childBoundsF.height() / 2);

        attributesViewTransformationMatrix.postRotate(rotation, imageBounds.width() / 2, imageBounds.height() / 2);
        attributesViewTransformationMatrix.postTranslate((rotatedImageBounds.width() - imageBounds.width()) / 2, (rotatedImageBounds.height() - imageBounds.height()) / 2);
        attributesViewTransformationMatrix.postScale(mScale, mScale);

        mAttributeMatrix = attributesViewTransformationMatrix;
    }

    // ===========================================================
    // Public methods
    // ===========================================================

    public synchronized void setEvent(NCapturePreviewEvent event) {
        setEventInfoInternal(event.getCapturePreview());
    }

    public synchronized void setEventInfo(NCapturePreview eventInfo) {
        setStatusText(eventInfo.getStatus().toString());
        setEventInfoInternal(eventInfo);
        startClearStatusTimer();
    }

    /**
     * Gets liveness text color.
     *
     * @return Color code.
     */
    public int getLivenessTextColor() {
        return mLivenessTextPaint.getColor();
    }


    /**
     * Sets liveness text color.
     *
     * @param livenessTextColor Color code.
     */
    public void setLivenessTextColor(int livenessTextColor) {
        int oldLivenessTextColor = getLivenessTextColor();
        if (oldLivenessTextColor != livenessTextColor) {
            mLivenessTextPaint.setColor(livenessTextColor);
            updateAttributes();
        }
    }

    /**
     * Gets action text.
     *
     * @param key Action key.
     * @return Action text.
     */
    public String getLivenessText(String key) {
        if (mTextMap.containsKey(key)) {
            return mTextMap.get(key);
        }
        throw new RuntimeException("Key is not defined");
    }

    /**
     * Sets action text.
     *
     * @param key  Action key.
     * @param text Action text.
     */
    public void setLivenessText(String key, String text) {
        if (mTextMap.containsKey(key)) {
            mTextMap.put(key, text);
            return;
        }
        throw new RuntimeException("Key is not defined");
    }

    public boolean isShowIcaoArrows() {
        return mShowIcaoArrows;
    }

    public void setShowIcaoArrows(boolean showIcaoArrows) {
        boolean oldShowIcaoArrows = isShowIcaoArrows();
        mShowIcaoArrows = showIcaoArrows;
        if (showIcaoArrows != oldShowIcaoArrows) {
            updateAttributes();
        }
    }

    public boolean isShowIcaoTextWarnings() {
        return mShowIcaoText;
    }

    public void setShowIcaoTextWarnings(boolean showIcaoTextWarnings) {
        boolean oldShowIcaoTextWarnings = isShowIcaoTextWarnings();
        mShowIcaoText = showIcaoTextWarnings;
        if (showIcaoTextWarnings != oldShowIcaoTextWarnings) {
            updateAttributes();
        }
    }

    public int getIcaoArrowsColor() {
        return mIcaoArrowsColor;
    }

    public void setIcaoArrowsColor(int icaoArrowsColor) {
        int oldIcaoArrowsColor = getIcaoArrowsColor();
        mIcaoArrowsColor = icaoArrowsColor;
        if (icaoArrowsColor != oldIcaoArrowsColor) {
            updateAttributes();
        }
    }

    public int getIcaoArrowsWidth() {
        return mIcaoArrowsWidth;
    }

    public void setIcaoArrowsWidth(int icaoArrowsWidth) {
        int oldIcaoArrowsWidth = getIcaoArrowsWidth();
        mIcaoArrowsWidth = icaoArrowsWidth;
        if (icaoArrowsWidth != oldIcaoArrowsWidth) {
            updateAttributes();
        }
    }

    // ===========================================================
    // Inner classes
    // ===========================================================

    static class ResolutionParams {
        public float fontSize;
        public float ovalMultiplier;
    }

    private class NImageTextureView extends TextureView implements SurfaceTextureListener {

        private static final String TAG = "NImageTextureView";

        // ===========================================================
        // Private fields
        // ===========================================================

        private boolean mNImageTextureViewAvailable = false;
        private Surface mSurface;
        private ConditionVariable mImageWaitingLock;
        private Semaphore mNImageTextureViewAvailabilityLock;

        // ===========================================================
        // Public constructors
        // ===========================================================

        public NImageTextureView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initComponents();
        }

        public NImageTextureView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initComponents();
        }

        public NImageTextureView(Context context) {
            super(context);
            initComponents();
        }

        // ===========================================================
        // Private methods
        // ===========================================================

        private void initComponents() {
            this.setSurfaceTextureListener(this);
            mImageWaitingLock = new ConditionVariable();
            mNImageTextureViewAvailabilityLock = new Semaphore(1);
        }

        private void internalPostImage() {
            if (DEBUG) Log.i(TAG, "internalPostImage mImageWaitingLock.open");
            mImageWaitingLock.open();
        }

        private void startRendering(SurfaceTexture surface) {
            if (DEBUG) Log.i(TAG, "NImageTextureView startRendering");
            try {
                mNImageTextureViewAvailabilityLock.acquire();
                mNImageTextureViewAvailable = true;
                mNImageTextureViewAvailabilityLock.release();
                mSurface = new Surface(surface);
                new ImageRenderThread().start();
                if (DEBUG) Log.i(TAG, "startRendering mImageWaitingLock.open");
                mImageWaitingLock.open();
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString(), e);
            }
        }

        private void stopRendering(SurfaceTexture surface) {
            if (DEBUG) Log.i(TAG, "NImageTextureView stopRendering");
            try {
                mNImageTextureViewAvailabilityLock.acquire();
                mNImageTextureViewAvailable = false;
                mNImageTextureViewAvailabilityLock.release();
                if (DEBUG) Log.i(TAG, "stopRendering mImageWaitingLock.open");
                mImageWaitingLock.open();
                if (DEBUG) Log.i(TAG, "stopRendering mSurface.release()");
                mSurface.release();
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString(), e);
            }
        }

        // ===========================================================
        // Public methods
        // ===========================================================

        public void postImage(Bitmap image) {
            if (DEBUG) Log.i(TAG, "postImage entering lockObject sync block");
            synchronized (lockObject) {
                mImageToRender = image;
                internalPostImage();
            }
            if (DEBUG) Log.i(TAG, "postImage leaving lockObject sync block");
        }

        // ===========================================================
        // Listener methods
        // ===========================================================

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (DEBUG) Log.i(TAG, "Surface Texture Available");
            if (mImageTransformationMatrix != null) {
                this.setTransform(mImageTransformationMatrix);
            }
            startRendering(surface);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (DEBUG) Log.i(TAG, "Surface Texture Destroyed");
            stopRendering(surface);
            mRotation = 0;
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (DEBUG) Log.i(TAG, "Surface Texture Size Changed");
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (DEBUG) Log.i(TAG, "Surface Texture Updated");
        }

        // ===========================================================
        // Image rendering thread
        // ===========================================================

        private class ImageRenderThread extends Thread {

            @Override
            public void run() {
                if (DEBUG) Log.i(TAG, "ImageRenderThread runing task");
                while (mNImageTextureViewAvailable) {
                    if (DEBUG) Log.i(TAG, "ImageRenderThread mImageWaitingLock.block");
                    mImageWaitingLock.block();
                    mImageWaitingLock.close();
                    if (DEBUG) Log.i(TAG, "ImageRenderThread mImageWaitingLock continuing");
                    if (DEBUG) Log.i(TAG, "ImageRenderThread entering lockObject sync block");
                    synchronized (lockObject) {
                        try {
                            mNImageTextureViewAvailabilityLock.acquire();
                            if (!mNImageTextureViewAvailable) {
                                mNImageTextureViewAvailabilityLock.release();
                                break;
                            }

                            if (mImageToRender != null) {
                                // Point of measuring frames
                                if (DEBUG)
                                    Log.i(TAG, "ImageRenderThread NFaceVerificationView drawImage, surface: " + mSurface);
                                // Possible java level implementation:
                                try {
                                    Canvas canvas = lockCanvas();
                                    if (canvas != null && mImageToRender != null) {
                                        Bitmap bitmap = mImageToRender;
                                        canvas.drawBitmap(bitmap, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);
                                        unlockCanvasAndPost(canvas);
                                    }
                                } catch (IllegalArgumentException e) {
                                    Log.e(TAG, e.toString(), e);
                                }
                            } else {
                                Canvas canvas = lockCanvas();
                                if (canvas != null) {
                                    canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                                    unlockCanvasAndPost(canvas);
                                }
                            }
                            mNImageTextureViewAvailabilityLock.release();
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.toString(), e);
                            mNImageTextureViewAvailabilityLock.release();
                            break;
                        }
                    }
                    if (DEBUG) Log.i(TAG, "ImageRenderThread leaving lockObject sync block");
                }
            }
        }

    }

    private class NFaceAttributesView extends TextureView implements SurfaceTextureListener {

        private static final String TAG = "NFaceAttributesView";

        // ===========================================================
        // Private fields
        // ===========================================================

        private boolean mAttributesTextureViewAvailable = false;
        private ConditionVariable mAttributesWaitingLock;
        private Semaphore mTextureViewAvailabilityLock;

        // ===========================================================
        // Public constructors
        // ===========================================================

        public NFaceAttributesView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initComponents();
        }

        public NFaceAttributesView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initComponents();
        }

        public NFaceAttributesView(Context context) {
            super(context);
            initComponents();
        }

        // ===========================================================
        // Attribute rendering thread
        // ===========================================================

        private class AttributeRenderThread extends Thread {

            @Override
            public void run() {
                if (DEBUG) Log.d(TAG, "AttributeRenderThread runing task");
                while (mAttributesTextureViewAvailable) {
                    if (DEBUG) Log.d(TAG, "AttributeRenderThread mAttributesWaitingLock.block");
                    mAttributesWaitingLock.block();
                    mAttributesWaitingLock.close();
                    if (DEBUG) Log.d(TAG, "AttributeRenderThread mAttributesWaitingLock continue");
                    try {
                        mTextureViewAvailabilityLock.acquire();
                        if (!mAttributesTextureViewAvailable) {
                            mTextureViewAvailabilityLock.release();
                            break;
                        }

                        if (DEBUG) Log.d(TAG, "AttributeRenderThread lockCanvas()");
                        Canvas canvas = lockCanvas();
                        if (DEBUG) Log.d(TAG, "Canvas: " + canvas);
                        if (canvas != null) {
                            if (DEBUG) Log.d(TAG, "AttributeRenderThread clearing color");
                            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                            if (mEventPropertiesToRender != null) {
                                int state = canvas.save();
                                canvas.concat(mAttributeMatrix);
                                Rect rect = mEventPropertiesToRender.getBoundingRect();
                                Matrix rotatedMatrix = new Matrix();

                                rotatedMatrix.postRotate(mEventPropertiesToRender.getRoll(),
                                        (float) (rect.left * 2 + rect.width()) / 2.0f,
                                        (float) (rect.top * 2 + rect.height()) / 2.0f);

                                if (DEBUG) Log.d(TAG, "AttributeRenderThread drawLivenessComponent");
                                drawFaceRectangle(canvas, mEventPropertiesToRender, rotatedMatrix);
                                drawFaceOval(canvas, mEventPropertiesToRender);
                                drawIcaoArrows(canvas, mEventPropertiesToRender, rotatedMatrix);
                                drawStatusText(canvas);
                                canvas.restoreToCount(state);
                                drawLivenessComponent(canvas, mEventPropertiesToRender);
                                drawIcaoText(canvas, mEventPropertiesToRender);
                            }
                            if (DEBUG) Log.d(TAG, "AttributeRenderThread unlockCanvasAndPost");
                            unlockCanvasAndPost(canvas);
                            if (DEBUG) Log.d(TAG, "AttributeRenderThread unlockCanvasAndPost sucess");
                        }
                        clearStatusText();
                        mTextureViewAvailabilityLock.release();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage(), e);
                        mTextureViewAvailabilityLock.release();
                        break;
                    }
                }
            }
        }

        // ===========================================================
        // Private methods
        // ===========================================================

        private void initComponents() {
            setSurfaceTextureListener(this);
            mAttributesWaitingLock = new ConditionVariable();
            mTextureViewAvailabilityLock = new Semaphore(1);
        }

        private void internalPostImageAndEvent(EventPropertiesHolder eventProperties) {
            mEventPropertiesToRender = eventProperties;
            if (DEBUG) Log.d(TAG, "internalPostImageAndEvent mAttributesWaitingLock.open");
            mAttributesWaitingLock.open();
        }

        private void startRendering(SurfaceTexture surface) {
            if (DEBUG) Log.d(TAG, "AttributeRenderThread startRendering");
            try {
                mTextureViewAvailabilityLock.acquire();
                mAttributesTextureViewAvailable = true;
                mTextureViewAvailabilityLock.release();
                new AttributeRenderThread().start();
                if (DEBUG) Log.d(TAG, "AttributeRenderThread mAttributesWaitingLock.open()");
                mAttributesWaitingLock.open();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void stopRendering(SurfaceTexture surface) {
            if (DEBUG) Log.d(TAG, "AttributeRenderThread stopRendering");
            try {
                mTextureViewAvailabilityLock.acquire();
                mAttributesTextureViewAvailable = false;
                mTextureViewAvailabilityLock.release();
                if (DEBUG) Log.d(TAG, "AttributeRenderThread mAttributesWaitingLock.open()");
                mAttributesWaitingLock.open();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // ===========================================================
        // Public methods
        // ===========================================================

        public void postAttributesAndEvent(EventPropertiesHolder eventProperties) {
            internalPostImageAndEvent(eventProperties);
        }

        // ===========================================================
        // Point drawing methods
        // ===========================================================

        private void drawFaceRectangle(Canvas canvas, EventPropertiesHolder properties, Matrix rotationMatrix) {
            if (!SettingsUtil.useFaceOval() && mShowFaceRectangle) {
                int preRotatedMatrix = canvas.save();
                if (mRotateFaceRectangle) {
                    canvas.concat(rotationMatrix);
                }

                Rect rect = properties.getBoundingRect();
                Path path = new Path();
                path.moveTo(rect.left, rect.top);
                path.lineTo(rect.right, rect.top);
                if (properties.getYaw() < 0) {
                    path.lineTo(rect.right - (rect.width() / 5 * properties.getYaw()) / 45, rect.top + (rect.height() / 2));
                }
                path.lineTo(rect.right, rect.bottom);
                path.lineTo(rect.left, rect.bottom);
                if (properties.getYaw() > 0) {
                    path.lineTo(rect.left - (rect.width() / 5 * properties.getYaw()) / 45, rect.top + (rect.height() / 2));
                }
                path.lineTo(rect.left, rect.top);
                path.lineTo(rect.right, rect.top);

                canvas.drawPath(path, mLinePaint);
                canvas.restoreToCount(preRotatedMatrix);
            }
        }

        private void drawFaceOval(Canvas canvas, EventPropertiesHolder properties) {
            if (SettingsUtil.useFaceOval()) {
                Path faceOvalPath = new Path();
                Path clipOvalPath = new Path();

                int width = mImageWidth;
                int height = mImageHeight;

                boolean needsCentering = false;
                if ((float) height / (float) width >= 1.5f) {
                    height = (int) (1.33f * width);
                    needsCentering = true;
                }

                int width_sides = (int) (DEFAULT_FACE_OVAL_BORDER_PERCENTAGE * width);
                int height_sides = (int) (DEFAULT_FACE_OVAL_BORDER_PERCENTAGE * height);

                float centering_y = needsCentering ? 1.7f * height_sides : 0;

                int stroke = (int) (DEFAULT_FACE_OVAL_STROKE_WIDTH * calculateResolutionParameters().ovalMultiplier);
                int halfStroke = stroke / 2;

                mGreenOvalPaint.setStrokeWidth(stroke);
                mGreyOvalPaint.setStrokeWidth(stroke);

                int minSize = Math.min(width, height);
                int faceWidth = (int) (minSize / 1.8 * DEFAULT_FACE_OVAL_SIZE_PERCENTAGE);
                int faceHeight = (int) (minSize / 1.4 * DEFAULT_FACE_OVAL_SIZE_PERCENTAGE);

                int x = (width - faceWidth) / 2 + width_sides;
                int y = (height - faceHeight) / 2 + (int) centering_y;

                faceOvalPath.addOval(
                        x + halfStroke,
                        y + halfStroke,
                        faceWidth - halfStroke,
                        faceHeight - halfStroke + (int) centering_y,
                        Direction.CW
                );
                clipOvalPath.addOval(
                        x,
                        y,
                        faceWidth,
                        faceHeight + (int) centering_y,
                        Direction.CW
                );

                boolean drawGreen = properties.getLivenessAction().contains(NLivenessAction.KEEP_STILL) || properties.getLivenessAction().contains(NLivenessAction.BLINK);
                canvas.drawPath(faceOvalPath, drawGreen ? mGreenOvalPaint : mGreyOvalPaint);
                canvas.clipPath(clipOvalPath, Region.Op.DIFFERENCE);
                canvas.drawARGB(242, 255, 255, 255); // white color with 5% transparency
            }
        }

        private void drawIcaoArrows(Canvas canvas, EventPropertiesHolder properties, Matrix rotationMatrix) {
            if (properties.getIcaoWarnings().isEmpty() || !isShowIcaoArrows()) {
                return;
            }
            int icaoMatrix = canvas.save();
            if (mRotateFaceRectangle) {
                canvas.concat(rotationMatrix);
            }
            EnumSet<NIcaoWarnings> warnings = properties.getIcaoWarnings();

            Paint icaoArrowsPaint = new Paint();
            icaoArrowsPaint.setColor(mIcaoArrowsColor);
            icaoArrowsPaint.setStyle(Paint.Style.STROKE);
            icaoArrowsPaint.setStrokeWidth(mIcaoArrowsWidth);

            Matrix arrowTransformMatrix = new Matrix();
            Matrix scaleMatrix = new Matrix();

            // Roll
            Rect attributesBounds = properties.getBoundingRect();

            if (warnings.contains(NIcaoWarnings.ROLL_LEFT) || warnings.contains(NIcaoWarnings.ROLL_RIGHT)) {
                RectF rollPathBounds = new RectF();
                Path rollArrowPath = getRollPath();
                rollArrowPath.computeBounds(rollPathBounds, true);
                float scale = attributesBounds.width() / 5 / rollPathBounds.width();
                scaleMatrix.postScale(scale, scale);
                rollArrowPath.transform(scaleMatrix);
                arrowTransformMatrix.postTranslate(attributesBounds.left, attributesBounds.top);
                if (isFlipY(mImageRotateFlipType) ^ warnings.contains(NIcaoWarnings.ROLL_LEFT)) {
                    rollArrowPath.transform(getMirrorMatrix());
                    arrowTransformMatrix.postTranslate(attributesBounds.width() + rollPathBounds.width() * scale, 0);
                }
                arrowTransformMatrix.postTranslate(-(float) (rollPathBounds.width() * scale / 2), -(float) (rollPathBounds.height() * scale / 2));
                int canvasSave = canvas.save();
                canvas.concat(arrowTransformMatrix);
                canvas.drawPath(rollArrowPath, icaoArrowsPaint);
                canvas.restoreToCount(canvasSave);
            }

            arrowTransformMatrix.reset();
            scaleMatrix.reset();

            //Yaw
            if (warnings.contains(NIcaoWarnings.YAW_LEFT) || warnings.contains(NIcaoWarnings.YAW_RIGHT)) {
                if ((warnings.contains(NIcaoWarnings.YAW_LEFT) && !warnings.contains(NIcaoWarnings.TOO_EAST))
                        || (warnings.contains(NIcaoWarnings.YAW_RIGHT) && !warnings.contains(NIcaoWarnings.TOO_WEST))
                        || warnings.contains(NIcaoWarnings.TOO_NEAR)) {
                    RectF yawPathBounds = new RectF();
                    Path yawArrowPath = getYawPath();
                    yawArrowPath.computeBounds(yawPathBounds, true);
                    float scale = attributesBounds.width() / 5 / yawPathBounds.width();
                    scaleMatrix.postScale(-scale, scale);
                    scaleMatrix.postTranslate(yawPathBounds.width() * scale, 0);
                    yawArrowPath.transform(scaleMatrix);
                    float centerX = (yawPathBounds.left + yawPathBounds.width()) / 2 * scale;
                    float centerY = (yawPathBounds.top + yawPathBounds.height()) / 2 * scale;

                    float offset = (attributesBounds.width() / 5 * properties.getYaw() / 45);

                    arrowTransformMatrix.postTranslate(attributesBounds.left, attributesBounds.top);
                    if (isFlipY(mImageRotateFlipType) ^ warnings.contains(NIcaoWarnings.YAW_LEFT)) {
                        yawArrowPath.transform(getMirrorMatrix());
                        arrowTransformMatrix.postTranslate(attributesBounds.width() + yawPathBounds.width() * scale, 0);
                    }
                    arrowTransformMatrix.postTranslate(offset - centerX, attributesBounds.height() / 2 - centerY);

                    int canvasSave = canvas.save();
                    canvas.concat(arrowTransformMatrix);
                    canvas.drawPath(yawArrowPath, icaoArrowsPaint);
                    canvas.restoreToCount(canvasSave);
                }
            }

            arrowTransformMatrix.reset();
            scaleMatrix.reset();

            //Move
            if (warnings.contains(NIcaoWarnings.TOO_SOUTH) || warnings.contains(NIcaoWarnings.TOO_NORTH)
                    || warnings.contains(NIcaoWarnings.TOO_EAST) || warnings.contains(NIcaoWarnings.TOO_WEST)) {
                Paint p = new Paint();
                p.setColor(Color.RED);
                p.setStyle(Paint.Style.FILL);
                p.setStrokeWidth(5);

                RectF movePathBounds = new RectF();
                Path moveArrowPath = getMovePath();

                moveArrowPath.computeBounds(movePathBounds, true);
                float scale = attributesBounds.width() / 5 / movePathBounds.width();
                scaleMatrix.postScale(-scale, scale);
                moveArrowPath.transform(scaleMatrix);

                float midX = movePathBounds.width() / 2 * scale;
                float midY = movePathBounds.height() / 2 * scale;

                int startingCanvas = canvas.save();
                float cx = attributesBounds.width() / 2;
                float cy = attributesBounds.height() / 2;
                arrowTransformMatrix.postTranslate(attributesBounds.left, attributesBounds.top);

                float dx = dipToPx(10);
                float dy = cy - midY;
                arrowTransformMatrix.postTranslate(-dx, dy);
                canvas.concat(arrowTransformMatrix);

                if (warnings.contains(isFlipY(mImageRotateFlipType) ? NIcaoWarnings.TOO_EAST : NIcaoWarnings.TOO_WEST)) {
                    int tCanvas = canvas.save();
                    float offset = warnings.contains(NIcaoWarnings.YAW_LEFT) ? (attributesBounds.width() / 5 * properties.getYaw() / 45) : 0;
                    arrowTransformMatrix.reset();
                    arrowTransformMatrix.postTranslate(offset, 0);
                    canvas.concat(arrowTransformMatrix);
                    canvas.drawPath(moveArrowPath, icaoArrowsPaint);
                    canvas.restoreToCount(tCanvas);
                }
                if (warnings.contains(isFlipY(mImageRotateFlipType) ? NIcaoWarnings.TOO_WEST : NIcaoWarnings.TOO_EAST)) {
                    int tCanvas = canvas.save();
                    float offset2 = warnings.contains(NIcaoWarnings.YAW_RIGHT) ? (attributesBounds.width() / 5 * properties.getYaw() / 45) : 0;
                    arrowTransformMatrix.reset();
                    arrowTransformMatrix.postRotate(180, (dx + cx), midY);
                    arrowTransformMatrix.postTranslate(offset2, 0);
                    canvas.concat(arrowTransformMatrix);
                    canvas.drawPath(moveArrowPath, icaoArrowsPaint);
                    canvas.restoreToCount(tCanvas);
                }
                if (warnings.contains(NIcaoWarnings.TOO_NORTH)) {
                    int tCanvas = canvas.save();
                    arrowTransformMatrix.reset();
                    arrowTransformMatrix.postRotate(270, (dx + cx), midY);
                    canvas.concat(arrowTransformMatrix);
                    canvas.drawPath(moveArrowPath, icaoArrowsPaint);
                    canvas.restoreToCount(tCanvas);
                }
                if (warnings.contains(NIcaoWarnings.TOO_SOUTH)) {
                    int tCanvas = canvas.save();
                    arrowTransformMatrix.reset();
                    arrowTransformMatrix.postRotate(90, (dx + cx), midY);
                    canvas.concat(arrowTransformMatrix);
                    canvas.drawPath(moveArrowPath, icaoArrowsPaint);
                    canvas.restoreToCount(tCanvas);
                }

                canvas.restoreToCount(startingCanvas);
            }

            arrowTransformMatrix.reset();
            scaleMatrix.reset();

            //Pitch
            if (warnings.contains(NIcaoWarnings.PITCH_DOWN) || warnings.contains(NIcaoWarnings.PITCH_UP)) {
                if ((warnings.contains(NIcaoWarnings.PITCH_DOWN) && !warnings.contains(NIcaoWarnings.TOO_SOUTH))
                        || (warnings.contains(NIcaoWarnings.PITCH_UP) && !warnings.contains(NIcaoWarnings.TOO_NORTH))
                        || warnings.contains(NIcaoWarnings.TOO_NEAR)) {

                    float cx = attributesBounds.width() / 2;
                    float cy = attributesBounds.height() / 2;

                    int canvasSave = canvas.save();

                    Path pitchPath = getPitchPath();
                    RectF pitchPathBounds = new RectF();
                    pitchPath.computeBounds(pitchPathBounds, true);
                    float scale = attributesBounds.width() / 5 / pitchPathBounds.width();

                    scaleMatrix.postScale(scale, -scale);
                    scaleMatrix.postTranslate(0, pitchPathBounds.height() * scale);
                    pitchPath.transform(scaleMatrix);

                    arrowTransformMatrix.postTranslate(attributesBounds.left, attributesBounds.top);

                    float centerX = (pitchPathBounds.left + pitchPathBounds.width()) / 2 * scale;
                    float centerY = (pitchPathBounds.top + pitchPathBounds.height()) / 2 * scale;

                    if (warnings.contains(NIcaoWarnings.PITCH_DOWN)) {
                        Matrix temp = new Matrix();
                        temp.postScale(1, -1);
                        temp.postTranslate(0, pitchPathBounds.height() * scale);
                        pitchPath.transform(temp);
                        arrowTransformMatrix.postTranslate(0, attributesBounds.height());
                    }

                    arrowTransformMatrix.postTranslate(cx - centerX, -centerY);
                    canvas.concat(arrowTransformMatrix);
                    canvas.drawPath(pitchPath, icaoArrowsPaint);
                    canvas.restoreToCount(canvasSave);
                }
            }
            canvas.restoreToCount(icaoMatrix);
        }

        private ResolutionParams calculateResolutionParameters() {
            ResolutionParams resParams = new ResolutionParams();

            // 1920 x 1080 = mImageHeight x mImageWidth
            int ratio = Math.round(1080f / mImageWidth);
            switch (ratio) {
                case 0: // 1080+
                case 1: // 1080 and less
                    resParams.fontSize = 60;
                    resParams.ovalMultiplier = 2;
                    break;
                case 2: // 720 and less
                    resParams.fontSize = 35;
                    resParams.ovalMultiplier = 1.2f;
                    break;
                case 3: // 432 and less
                case 4: // 308 and less
                case 5: // 240 and less
                case 6: // 196 and less
                    resParams.fontSize = 15;
                    resParams.ovalMultiplier = 0.7f;
                    break;
                case 7: // 166 and less
                case 8: // 144 and less
                default:
                    resParams.fontSize = 8;
                    resParams.ovalMultiplier = 0.2f;
            }

            return resParams;
        }

        private void drawStatusText(Canvas canvas) {
            mStatusPaint.setColor(SettingsUtil.useFaceOval() ? Color.BLACK : Color.YELLOW);
            mStatusPaint.setTextSize(calculateResolutionParameters().fontSize);
            Rect textBounds = new Rect();
            mStatusPaint.getTextBounds(mStatusMsg, 0, mStatusMsg.length(), textBounds);

            int height = textBounds.height();
            if (mImageWidth < 360) height = 0;
            if (mImageWidth < 150) height = -15;

            Rect area = new Rect(0, height, mImageWidth, textBounds.height() * 2);
            drawText(canvas, area, mStatusMsg, mStatusPaint, true);
        }

        private void drawIcaoText(Canvas canvas, EventPropertiesHolder properties) {
            if (mShowIcaoText) {
                Paint icaoTextPaint = new Paint();
                icaoTextPaint.setColor(mIcaoTextColor);
                icaoTextPaint.setStyle(Paint.Style.FILL);
                icaoTextPaint.setTextSize(dipToPx(mIcaoTextSize));

                String icaoWarningText = getIcaoWarningText(properties.getIcaoWarnings());
                if (icaoWarningText != null) {
                    android.text.StaticLayout layout = new android.text.StaticLayout(icaoWarningText, new android.text.TextPaint(icaoTextPaint), (int) android.text.StaticLayout.getDesiredWidth(icaoWarningText, new android.text.TextPaint(icaoTextPaint)) + 1, android.text.Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    layout.draw(canvas);
                }
            }
        }

        private String getIcaoWarningText(EnumSet<NIcaoWarnings> warnings) {
            if (warnings.iterator().hasNext()) {
                return mIcaoWarningMap.get(warnings.iterator().next());
            }
            return null;
        }

        private void drawLivenessComponent(Canvas canvas, EventPropertiesHolder properties) {
            if (!properties.getLivenessAction().isEmpty()) {
                int oldCanvas = canvas.save();

                int rotationFix = displayRotationToAngle(mDisplay.getRotation()) + mOrientation;
                if (isFlipY(mImageRotateFlipType)) {
                    rotationFix = (360 - rotationFix) % 360;
                }
                if (rotationFix != 0) {
                    Matrix liveness = new Matrix();
                    liveness.postRotate(rotationFix, getWidth() / 2, getHeight() / 2);
                    liveness.postTranslate((float) (Math.sin(Math.toRadians(rotationFix)) * (getHeight() - getWidth()) / 2), 0);
                    canvas.concat(liveness);
                }

                boolean blink = properties.getLivenessAction().contains(NLivenessAction.BLINK);
                boolean rotate = properties.getLivenessAction().contains(NLivenessAction.ROTATE_YAW);
                boolean keepStill = properties.getLivenessAction().contains(NLivenessAction.KEEP_STILL);
                boolean keepRotating = properties.getLivenessAction().contains(NLivenessAction.KEEP_ROTATING_YAW);
                boolean toCenter = properties.getLivenessAction().contains(NLivenessAction.TURN_TO_CENTER);
                boolean toLeft = properties.getLivenessAction().contains(NLivenessAction.TURN_RIGHT);
                boolean toRight = properties.getLivenessAction().contains(NLivenessAction.TURN_LEFT);
                boolean toUp = properties.getLivenessAction().contains(NLivenessAction.TURN_UP);
                boolean toDown = properties.getLivenessAction().contains(NLivenessAction.TURN_DOWN);
                boolean moveCloser = properties.getLivenessAction().contains(NLivenessAction.MOVE_CLOSER);
                boolean moveBack = properties.getLivenessAction().contains(NLivenessAction.MOVE_BACK);

                byte score = properties.getLivenessScore();
                StringBuilder status = new StringBuilder();
                String actionKey = null;

                int maxYaw = 35;

                float yaw = properties.getYaw() * -1;
                float targetYaw = properties.getLivenessTargetYaw() * -1;

                int x = getWidth() / 10;
                int width = x * 8;
                int height = x;

                Rect area;
                if (SettingsUtil.useFaceOval()) {
                    area = new Rect(x, height / 2, x + width, getHeight());
                } else {
                    area = new Rect(x, getHeight() - height * 2, x + width, getHeight());
                }

                if (keepStill) {
                    if (score <= 100) {
                        actionKey = DEBUG ? KEY_LIVENESS_TEXT_KEEP_STILL_WITH_SCORE : KEY_LIVENESS_TEXT_KEEP_STILL;
                    } else {
                        actionKey = KEY_LIVENESS_TEXT_KEEP_STILL;
                    }
                } else if (rotate) {
                    if (!blink) {
                        actionKey = KEY_LIVENESS_TEXT_TURN_TO_TARGET;

                        int targetOffset = (int) ((targetYaw / maxYaw) * (width / 2));
                        Rect targetArea = new Rect(area.centerX() - (height / 2) + targetOffset,
                                area.top,
                                area.centerX() - (height / 2) + targetOffset + height,
                                area.top + height);

                        canvas.drawPath(preparePath(getTargetPath(), targetArea, targetYaw < yaw), mLivenessAreaPaint);

                        int curentYawOffset = (int) ((yaw / maxYaw) * (width / 2));

                        int left = area.centerX() - (height / 2);

                        Rect curentYawArea = new Rect(left + curentYawOffset,
                                area.top,
                                left + curentYawOffset + height,
                                area.top + height);

                        canvas.drawPath(preparePath(getArrowPath(), curentYawArea, targetYaw < yaw), mLivenessAreaPaint);
                    } else {
                        actionKey = KEY_LIVENESS_TEXT_BLINK;

                        int curentYawOffset = (int) ((yaw / maxYaw) * (width / 2));
                        int left = area.centerX() - (height / 2);

                        Rect curentYawArea = new Rect(left + curentYawOffset,
                                area.top,
                                left + curentYawOffset + height,
                                area.top + height);
                        canvas.drawPath(preparePath(getBlinkPath(), curentYawArea, targetYaw < yaw), mLivenessAreaPaint);
                    }
                } else if (blink) {
                    actionKey = KEY_LIVENESS_TEXT_BLINK;
                } else if (keepRotating) {
                    if (score <= 100) {
                        actionKey = DEBUG ? KEY_LIVENESS_TEXT_KEEP_ROTATING_WITH_SCORE : KEY_LIVENESS_TEXT_KEEP_ROTATING;
                    } else {
                        actionKey = KEY_LIVENESS_TEXT_KEEP_ROTATING;
                    }
                } else if (toCenter) {
                    actionKey = KEY_LIVENESS_TEXT_TURN_TO_CENTER;
                } else if (toLeft) {
                    actionKey = KEY_LIVENESS_TEXT_TURN_LEFT;
                } else if (toRight) {
                    actionKey = KEY_LIVENESS_TEXT_TURN_RIGHT;
                } else if (toUp) {
                    actionKey = KEY_LIVENESS_TEXT_TURN_UP;
                } else if (toDown) {
                    actionKey = KEY_LIVENESS_TEXT_TURN_DOWN;
                } else if (moveCloser) {
                    actionKey = KEY_LIVENESS_TEXT_MOVE_CLOSER;
                } else if (moveBack) {
                    actionKey = KEY_LIVENESS_TEXT_MOVE_BACK;
                }
                // if actionsKey string will not have format specifications (%d), score will be ignored
                status.append(String.format(getLivenessText(actionKey), score));
                boolean useFaceOval = SettingsUtil.useFaceOval();
                mLivenessTextPaint.setColor(useFaceOval ? Color.BLACK : Color.YELLOW);
                drawText(canvas, area, status.toString(), mLivenessTextPaint, useFaceOval);
                canvas.restoreToCount(oldCanvas);
            }
        }

        private void drawText(Canvas canvas, Rect area, String text, Paint paint, boolean drawAboveFace) {
            int multiplier = drawAboveFace ? 1 : -1;
            Rect textBounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, area.centerX() - (textBounds.width() / 2), area.top + multiplier * dipToPx(DEFAULT_LIVENESS_STATUS_SPACE), paint);
        }

        private boolean isCenter(float angle) {
            return Math.abs(angle) < 9;
        }
        // ===========================================================
        // Listener methods
        // ===========================================================

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (DEBUG) Log.d(TAG, "onSurfaceTextureAvailable");
            startRendering(surface);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (DEBUG) Log.d(TAG, "onSurfaceTextureDestroyed");
            stopRendering(surface);
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (DEBUG) Log.d(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (DEBUG) Log.d(TAG, "onSurfaceTextureUpdated");
        }

    }
}

