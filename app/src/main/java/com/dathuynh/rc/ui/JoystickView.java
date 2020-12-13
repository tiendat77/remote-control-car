package com.dathuynh.rc.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dathuynh.rc.R;

/**
 * Authors: Damien Brun
 * Github: https://github.com/controlwear/virtual-joystick-android/issues
 * Editor: Tien Dat Huynh
 **/

public class JoystickView extends View implements Runnable {

    /**
     * Interface definition for a callback to be invoked when a
     * JoystickView's button is moved
     */
    public interface OnMoveListener {
        void onMove(int angle, int strength);
    }

    // CONSTANTS
    /* Default refresh rate as a time in milliseconds to send move values through callback */
    private static final int DEFAULT_LOOP_INTERVAL = 50; // in milliseconds
    /* Default View's size */
    private static final int DEFAULT_SIZE = 200;

    // DRAWING
    private Paint circleButtonPaint;
    private Paint circleBitmapPaint;
    private Bitmap circleBitmap;

    // COORDINATE
    private int posX = 0;
    private int posY = 0;
    private int centerX = 0;
    private int centerY = 0;

    private int fixedCenterX = 0;
    private int fixedCenterY = 0;

    // SIZE
    private int radius = (int) (DEFAULT_SIZE / 2);
    private int buttonRadius;
    private int borderRadius;

    /**
     * Listener used to dispatch OnMove event
     */
    private OnMoveListener moveListener;
    private long loopInterval = DEFAULT_LOOP_INTERVAL;
    private Thread thread = new Thread(this);

    /* CONSTRUCTORS */

    /**
     * Simple constructor to use when creating a JoystickView from code.
     * Call another constructor passing null to Attribute.
     *
     * @param context The Context the JoystickView is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public JoystickView(Context context) {
        this(context, null);
    }


    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }


    /**
     * Constructor that is called when inflating a JoystickView from XML. This is called
     * when a JoystickView is being constructed from an XML file
     *
     * @param context The Context the JoystickView is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the JoystickView.
     */
    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.JoystickView,
                0, 0
        );

        init(attributes);
    }

    private void init(TypedArray attributes) {
        circleButtonPaint = new Paint();
        circleButtonPaint.setAntiAlias(true);
        circleButtonPaint.setColor(Color.BLACK);
        circleButtonPaint.setStyle(Paint.Style.FILL);
        circleBitmapPaint = new Paint();

        Drawable iconDrawable;
        try {
            iconDrawable = attributes.getDrawable(R.styleable.JoystickView_icon);
        } finally {
            attributes.recycle();
        }

        if (iconDrawable != null) {
            if (iconDrawable instanceof BitmapDrawable) {
                circleBitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            }
        }
    }

    /**
     * Draw the background, the border and the button
     * @param canvas the canvas on which the shapes will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the button from image
        if (circleBitmap != null) {
            canvas.drawBitmap(
                    circleBitmap,
                    posX + fixedCenterX - centerX - buttonRadius,
                    posY + fixedCenterY - centerY - buttonRadius,
                    circleBitmapPaint
            );
        }
        // Draw the button as simple circle
        else {
            canvas.drawCircle(
                    posX + fixedCenterX - centerX,
                    posY + fixedCenterY - centerY,
                    buttonRadius,
                    circleButtonPaint
            );
        }
    }

    private void drawArrow(Canvas canvas) {
        float angle = (float) Math.atan2(posY - centerY, posX - centerX);
        float xPos = (float) ((float) centerX + Math.cos(angle) * radius);
        float yPos = (float) ((float) centerX + Math.sin(angle) * radius);

        canvas.drawCircle(
                xPos,
                yPos,
                buttonRadius,
                circleButtonPaint
        );
    }


    /**
     * This is called during layout when the size of this view has changed.
     * Here we get the center of the view and the radius to draw all the shapes.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        // get the center of view to position circle
        fixedCenterX = centerX = posX = getWidth() / 2;
        fixedCenterY = centerY = posY = getWidth() / 2;

        // radius based on smallest size : height OR width
        int d = Math.min(w, h);
        radius = (int) (d / 2);
        buttonRadius = (int) (d / 2 * 0.25f);
        borderRadius = (int) (d / 2 * 0.75f);

        if (circleBitmap != null) {
            circleBitmap = Bitmap.createScaledBitmap(circleBitmap, buttonRadius * 2, buttonRadius * 2, true);
        }
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


    /**
     * Handle touch screen motion event. Move the button according to the
     * finger coordinate and detect longPress by multiple pointers only.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // to move the button according to the finger coordinate
        // (or limited to one axe according to direction option
        posY = (int) event.getY();
        posX = (int) event.getX();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // stop listener because the finger left the touch screen
            thread.interrupt();

            // re-center the button
            resetButtonPosition();
            onMove();
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }

            thread = new Thread(this);
            thread.start();

            onMove();
        }

        double abs = Math.sqrt((posX - centerX) * (posX - centerX)
                + (posY - centerY) * (posY - centerY));

        // (abs > mBorderRadius) means button is too far therefore we limit to border
        // (buttonStickBorder && abs != 0) means wherever is the button we stick it to the border except when abs == 0
        if (abs > borderRadius) {
            posX = (int) ((posX - centerX) * borderRadius / abs + centerX);
            posY = (int) ((posY - centerY) * borderRadius / abs + centerY);
        }

        // TO FORCE A NEW DRAW
        invalidate();

        return true;
    }


    /* GETTERS */

    /**
     * Process the angle following the 360° counter-clock protractor rules.
     *
     * @return the angle of the button
     */
    private int getAngle() {
        int angle = (int) Math.toDegrees(Math.atan2(centerY - posY, posX - centerX));
        return angle < 0 ? angle + 360 : angle; // make it as a regular counter-clock protractor
    }

    /**
     * Process the strength as a percentage of the distance between the center and the border.
     *
     * @return the strength of the button
     */
    private int getStrength() {
        return (int) (100 * Math.sqrt((posX - centerX)
                * (posX - centerX) + (posY - centerY)
                * (posY - centerY)) / borderRadius);
    }

    /**
     * Dispatch onMove event if moveListener declared
     */
    private void onMove() {
        if (moveListener != null) {
            moveListener.onMove(getAngle(), getStrength());
        }
    }


    /**
     * Reset the button position to the center.
     */
    public void resetButtonPosition() {
        posX = centerX;
        posY = centerY;
    }

    /**
     * Register a callback to be invoked when this JoystickView's button is moved
     *
     * @param l            The callback that will run
     * @param loopInterval Refresh rate to be invoked in milliseconds
     */
    public void setOnMoveListener(OnMoveListener l, int loopInterval) {
        moveListener = l;
        this.loopInterval = loopInterval;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    onMove();
                }
            });

            try {
                Thread.sleep(loopInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
