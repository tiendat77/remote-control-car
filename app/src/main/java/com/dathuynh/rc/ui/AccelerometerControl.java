package com.dathuynh.rc.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dathuynh.rc.R;

public class AccelerometerControl extends RelativeLayout {

    private OnCommandListener onCommandListener;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Vibrator vibrator;

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

    }

    private void setListener() {

    }

    private void onCommand(String command) {
        if (onCommandListener != null) {
            onCommandListener.onCommand(command);
        }
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void setOnCommandListener(OnCommandListener onCommandListener) {
        this.onCommandListener = onCommandListener;
    }

    public interface OnCommandListener {
        void onCommand(String command);
    }
}
