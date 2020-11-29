package com.dathuynh.rc.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;

public class AccelerometerControl extends RelativeLayout implements SensorEventListener {

    private OnCommandListener onCommandListener;
    private Activity activity;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Vibrator vibrator;

    /* Calc sensor change */
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float lastX;
    private float lastY;
    private float lastZ;

    /* Default sensor position */
    private boolean defaultSet = false;
    private float defaultX = 0;
    private float defaultY = 0;

    private long lastChangeTime = 0;

    private ImageView accelerometerImage;

    public AccelerometerControl(Context context) {
        super(context);
        init(context);
    }

    public AccelerometerControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.ui_accelerometer_control, this);
        setUIRef();
        setListener();
    }

    private void setUIRef() {
        accelerometerImage = (ImageView) getChildAt(0);
    }

    private void setListener() {
        if (accelerometerImage != null) {
            accelerometerImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Reset default position
                    defaultSet = false;
                }
            });
        }
    }

    private void initSensor() {
        if (activity != null) {
            sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
            vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }
    }

    private void onCommand(String command) {
        if (onCommandListener != null) {
            onCommandListener.onCommand(command);
        }

        // if (vibrator != null) {
        //     vibrator.vibrate(40);
        // }
    }

    public void hide() {
        this.setVisibility(View.GONE);
        unregister();
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
        register();
    }

    public void register() {
        if (sensorManager != null && activity != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }
    }

    public void unregister() {
        if (sensorManager != null && activity != null) {
            sensorManager.unregisterListener(this);
            defaultSet = false;

            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        initSensor();
    }

    public void setOnCommandListener(OnCommandListener onCommandListener) {
        this.onCommandListener = onCommandListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentChangeTime = System.currentTimeMillis();

        // Debounce time: 600ms
        if ((currentChangeTime - lastChangeTime) < 600) {
            return;
        }

        if (!defaultSet) { // Set default x-axis
            defaultX = event.values[0];
            defaultY = event.values[1];

            defaultSet = true;
        }

        // set the last know values of the accelerometer
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        // get changes of the accelerometer
        deltaX = lastX - defaultX;
        deltaY = lastY - defaultY;
        deltaZ = lastZ - event.values[2];

        lastChangeTime = currentChangeTime;

        if (deltaX < -3) {
            onCommand(Constants.CMD_GO);
            return;
        }

        if (deltaX > 3) {
            onCommand(Constants.CMD_BACK);
            return;
        }

        if (deltaY > 3) {
            onCommand(Constants.CMD_RIGHT);
            return;
        }

        if (deltaY < -3) {
            onCommand(Constants.CMD_LEFT);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Empty listener */
    }

    public interface OnCommandListener {
        void onCommand(String command);
    }
}
