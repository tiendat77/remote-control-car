package com.dathuynh.rc.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Authors:
 * https://github.com/controlwear/virtual-joystick-android/issues
 * */

import com.dathuynh.rc.R;

public class JoystickView extends View implements Runnable {


    /*
    INTERFACES
    */


    /**
     * Interface definition for a callback to be invoked when a
     * JoystickView's button is moved
     */
    public interface OnMoveListener {

        /**
         * Called when a JoystickView's button has been moved
         * @param angle current angle
         * @param strength current strength
         */
        void onMove(int angle, int strength);
    }


    /**
     * Interface definition for a callback to be invoked when a JoystickView
     * is touched and held by multiple pointers.
     */
    public interface OnMultipleLongPressListener {
        /**
         * Called when a JoystickView has been touch and held enough time by multiple pointers.
         */
        void onMultipleLongPress();
    }


    /*
    CONSTANTS
    */

    /**
     * Default refresh rate as a time in milliseconds to send move values through callback
     */
    private static final int DEFAULT_LOOP_INTERVAL = 50; // in milliseconds

    /**
     * Used to allow a slight move without cancelling MultipleLongPress
     */
    private static final int MOVE_TOLERANCE = 10;

    /**
     * Default color for button
     */
    private static final int DEFAULT_COLOR_BUTTON = Color.BLACK;

    /**
     * Default color for border
     */
    private static final int DEFAULT_COLOR_BORDER = Color.TRANSPARENT;

    /**
     * Default alpha for border
     */
    private static final int DEFAULT_ALPHA_BORDER = 255;

    /**
     * Default background color
     */
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    /**
     * Default View's size
     */
    private static final int DEFAULT_SIZE = 200;

    /**
     * Default border's width
     */
    private static final int DEFAULT_WIDTH_BORDER = 3;

    /**
     * Default behavior to fixed center (not auto-defined)
     */
    private static final boolean DEFAULT_FIXED_CENTER = true;


    /**
     * Default behavior to auto re-center button (automatically recenter the button)
     */
    private static final boolean DEFAULT_AUTO_RECENTER_BUTTON = true;


    /**
     * Default behavior to button stickToBorder (button stay on the border)
     */
    private static final boolean DEFAULT_BUTTON_STICK_TO_BORDER = false;


    // DRAWING
    private Paint mPaintCircleButton;
    private Paint mPaintCircleBorder;
    private Paint mPaintBackground;

    private Paint mPaintBitmapButton;
    private Bitmap mButtonBitmap;


    /**
     * Ratio use to define the size of the button
     */
    private float mButtonSizeRatio;


    /**
     * Ratio use to define the size of the background
     *
     */
    private float mBackgroundSizeRatio;


    // COORDINATE
    private int mPosX = 0;
    private int mPosY = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;

    private int mFixedCenterX = 0;
    private int mFixedCenterY = 0;

    /**
     * Used to adapt behavior whether it is auto-defined center (false) or fixed center (true)
     */
    private boolean mFixedCenter;


    /**
     * Used to adapt behavior whether the button is automatically re-centered (true)
     * when released or not (false)
     */
    private boolean mAutoReCenterButton;


    /**
     * Used to adapt behavior whether the button is stick to border (true) or
     * could be anywhere (when false - similar to regular behavior)
     */
    private boolean mButtonStickToBorder;


    /**
     * Used to enabled/disabled the Joystick. When disabled (enabled to false) the joystick button
     * can't move and onMove is not called.
     */
    private boolean mEnabled;


    // SIZE
    private int mButtonRadius;
    private int mBorderRadius;


    /**
     * Based on mBorderRadius but a bit smaller (minus half the stroke size of the border)
     */
    private float mBackgroundRadius;


    /**
     * Listener used to dispatch OnMove event
     */
    private OnMoveListener mCallback;

    private long mLoopInterval = DEFAULT_LOOP_INTERVAL;
    private Thread mThread = new Thread(this);


    /**
     * Listener used to dispatch MultipleLongPress event
     */
    private OnMultipleLongPressListener mOnMultipleLongPressListener;

    private final Handler mHandlerMultipleLongPress = new Handler();
    private Runnable mRunnableMultipleLongPress;
    private int mMoveTolerance;


    /**
     * Default value.
     * Both direction correspond to horizontal and vertical movement
     */
    public static int BUTTON_DIRECTION_BOTH = 0;

    /**
     * The allowed direction of the button is define by the value of this parameter:
     * - a negative value for horizontal axe
     * - a positive value for vertical axe
     * - zero for both axes
     */
    private int mButtonDirection = 0;


    /*
    CONSTRUCTORS
     */


    /**
     * Simple constructor to use when creating a JoystickView from code.
     * Call another constructor passing null to Attribute.
     * @param context The Context the JoystickView is running in, through which it can
     *        access the current theme, resources, etc.
     */
    public JoystickView(Context context) {
        this(context, null);
    }


    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }


    /**
     * Constructor that is called when inflating a JoystickView from XML. This is called
     * when a JoystickView is being constructed from an XML file, supplying attributes
     * that were specified in the XML file.
     * @param context The Context the JoystickView is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the JoystickView.
     */
    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize the drawing according to attributes
        mPaintCircleButton = new Paint();
        mPaintCircleButton.setAntiAlias(true);
        mPaintCircleButton.setColor(DEFAULT_COLOR_BUTTON);
        mPaintCircleButton.setStyle(Paint.Style.FILL);

        mFixedCenter = DEFAULT_FIXED_CENTER;
        mAutoReCenterButton = DEFAULT_AUTO_RECENTER_BUTTON;
        mButtonStickToBorder = DEFAULT_BUTTON_STICK_TO_BORDER;
        mEnabled = true;
        mButtonSizeRatio = 0.25f;
        mBackgroundSizeRatio = 0.75f;
        mButtonDirection = BUTTON_DIRECTION_BOTH;

        mButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_joystick_ball);
        mPaintBitmapButton = new Paint();

        mPaintCircleBorder = new Paint();
        mPaintCircleBorder.setAntiAlias(true);
        mPaintCircleBorder.setColor(DEFAULT_COLOR_BORDER);
        mPaintCircleBorder.setStyle(Paint.Style.STROKE);
        mPaintCircleBorder.setStrokeWidth(DEFAULT_WIDTH_BORDER);

        mPaintBackground = new Paint();
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(DEFAULT_BACKGROUND_COLOR);
        mPaintBackground.setStyle(Paint.Style.FILL);


        // Init Runnable for MultiLongPress
        mRunnableMultipleLongPress = new Runnable() {
            @Override
            public void run() {
                if (mOnMultipleLongPressListener != null)
                    mOnMultipleLongPressListener.onMultipleLongPress();
            }
        };
    }


    private void initPosition() {
        // get the center of view to position circle
        mFixedCenterX = mCenterX = mPosX = getWidth() / 2;
        mFixedCenterY = mCenterY = mPosY = getWidth() / 2;
    }


    /**
     * Draw the background, the border and the button
     * @param canvas the canvas on which the shapes will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background
        canvas.drawCircle(mFixedCenterX, mFixedCenterY, mBackgroundRadius, mPaintBackground);

        // Draw the circle border
        canvas.drawCircle(mFixedCenterX, mFixedCenterY, mBorderRadius, mPaintCircleBorder);

        // Draw the button from image
        if (mButtonBitmap != null) {
            canvas.drawBitmap(
                    mButtonBitmap,
                    mPosX + mFixedCenterX - mCenterX - mButtonRadius,
                    mPosY + mFixedCenterY - mCenterY - mButtonRadius,
                    mPaintBitmapButton
            );
        }
        // Draw the button as simple circle
        else {
            canvas.drawCircle(
                    mPosX + mFixedCenterX - mCenterX,
                    mPosY + mFixedCenterY - mCenterY,
                    mButtonRadius,
                    mPaintCircleButton
            );
        }
    }


    /**
     * This is called during layout when the size of this view has changed.
     * Here we get the center of the view and the radius to draw all the shapes.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldW Old width of this view.
     * @param oldH Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        initPosition();

        // radius based on smallest size : height OR width
        int d = Math.min(w, h);
        mButtonRadius = (int) (d / 2 * mButtonSizeRatio);
        mBorderRadius = (int) (d / 2 * mBackgroundSizeRatio);
        mBackgroundRadius = mBorderRadius - (mPaintCircleBorder.getStrokeWidth() / 2);

        if (mButtonBitmap != null)
            mButtonBitmap = Bitmap.createScaledBitmap(mButtonBitmap, mButtonRadius * 2, mButtonRadius * 2, true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setting the measured values to resize the view to a certain width and height
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));
        setMeasuredDimension(d, d);
    }


    private int measure(int measureSpec) {
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            // if no bounds are specified return a default size (200)
            return DEFAULT_SIZE;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            return MeasureSpec.getSize(measureSpec);
        }
    }


    /*
    USER EVENT
     */


    /**
     * Handle touch screen motion event. Move the button according to the
     * finger coordinate and detect longPress by multiple pointers only.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if disabled we don't move the
        if (!mEnabled) {
            return true;
        }


        // to move the button according to the finger coordinate
        // (or limited to one axe according to direction option
        mPosY = mButtonDirection < 0 ? mCenterY : (int) event.getY(); // direction negative is horizontal axe
        mPosX = mButtonDirection > 0 ? mCenterX : (int) event.getX(); // direction positive is vertical axe

        if (event.getAction() == MotionEvent.ACTION_UP) {

            // stop listener because the finger left the touch screen
            mThread.interrupt();

            // re-center the button or not (depending on settings)
            if (mAutoReCenterButton) {
                resetButtonPosition();

                // update now the last strength and angle which should be zero after resetButton
                if (mCallback != null)
                    mCallback.onMove(getAngle(), getStrength());
            }

            // if mAutoReCenterButton is false we will send the last strength and angle a bit
            // later only after processing new position X and Y otherwise it could be above the border limit
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mThread != null && mThread.isAlive()) {
                mThread.interrupt();
            }

            mThread = new Thread(this);
            mThread.start();

            if (mCallback != null)
                mCallback.onMove(getAngle(), getStrength());
        }

        // handle first touch and long press with multiple touch only
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // when the first touch occurs we update the center (if set to auto-defined center)
                if (!mFixedCenter) {
                    mCenterX = mPosX;
                    mCenterY = mPosY;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                // when the second finger touch
                if (event.getPointerCount() == 2) {
                    mHandlerMultipleLongPress.postDelayed(mRunnableMultipleLongPress, ViewConfiguration.getLongPressTimeout()*2);
                    mMoveTolerance = MOVE_TOLERANCE;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
                mMoveTolerance--;
                if (mMoveTolerance == 0) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                // when the last multiple touch is released
                if (event.getPointerCount() == 2) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress);
                }
                break;
            }
        }

        double abs = Math.sqrt((mPosX - mCenterX) * (mPosX - mCenterX)
                + (mPosY - mCenterY) * (mPosY - mCenterY));

        // (abs > mBorderRadius) means button is too far therefore we limit to border
        // (buttonStickBorder && abs != 0) means wherever is the button we stick it to the border except when abs == 0
        if (abs > mBorderRadius || (mButtonStickToBorder && abs != 0)) {
            mPosX = (int) ((mPosX - mCenterX) * mBorderRadius / abs + mCenterX);
            mPosY = (int) ((mPosY - mCenterY) * mBorderRadius / abs + mCenterY);
        }

        if (!mAutoReCenterButton) {
            // Now update the last strength and angle if not reset to center
            if (mCallback != null)
                mCallback.onMove(getAngle(), getStrength());
        }


        // to force a new draw
        invalidate();

        return true;
    }


    /*
    GETTERS
     */


    /**
     * Process the angle following the 360° counter-clock protractor rules.
     * @return the angle of the button
     */
    private int getAngle() {
        int angle = (int) Math.toDegrees(Math.atan2(mCenterY - mPosY, mPosX - mCenterX));
        return angle < 0 ? angle + 360 : angle; // make it as a regular counter-clock protractor
    }


    /**
     * Process the strength as a percentage of the distance between the center and the border.
     * @return the strength of the button
     */
    private int getStrength() {
        return (int) (100 * Math.sqrt((mPosX - mCenterX)
                * (mPosX - mCenterX) + (mPosY - mCenterY)
                * (mPosY - mCenterY)) / mBorderRadius);
    }


    /**
     * Reset the button position to the center.
     */
    public void resetButtonPosition() {
        mPosX = mCenterX;
        mPosY = mCenterY;
    }

    /**
     * Return the state of the joystick. False when the button don't move.
     * @return the state of the joystick
     */
    public boolean isEnabled() {
        return mEnabled;
    }


    /**
     * Set the background color for this JoystickView.
     * @param color the color of the background
     */
    @Override
    public void setBackgroundColor(int color) {
        mPaintBackground.setColor(color);
        invalidate();
    }

    /**
     * Register a callback to be invoked when this JoystickView's button is moved
     * @param l The callback that will run
     * @param loopInterval Refresh rate to be invoked in milliseconds
     */
    public void setOnMoveListener(OnMoveListener l, int loopInterval) {
        mCallback = l;
        mLoopInterval = loopInterval;
    }

    /**
     * Enable or disable the joystick
     * @param enabled False mean the button won't move and onMove won't be called
     */
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override // Runnable
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    if (mCallback != null)
                        mCallback.onMove(getAngle(), getStrength());
                }
            });

            try {
                Thread.sleep(mLoopInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
