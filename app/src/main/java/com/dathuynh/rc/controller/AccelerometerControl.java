package com.dathuynh.rc.controller;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.dathuynh.rc.R;

public class AccelerometerControl {

    private final View view;
    private OnMoveListener onMoveListener;

    private RelativeLayout layoutContainer;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Vibrator vibrator;

    public AccelerometerControl(@NonNull View view, OnMoveListener onMoveListener) {
        this.view = view;
        this.onMoveListener = onMoveListener;

        setUIRef();
        setListener();
    }

    private void setUIRef() {
        layoutContainer = view.findViewById(R.id.control_layout_accelerometer);
    }

    private void setListener() {

    }

    private void onMoveEvent(String command) {
        if (onMoveListener != null) {
            onMoveListener.onMove(command);
        }
    }

    public void showAccelerometer() {
        if (layoutContainer != null) {
            layoutContainer.setVisibility(View.VISIBLE);
        }
    }

    public void hideAccelerometer() {
        if (layoutContainer != null) {
            layoutContainer.setVisibility(View.GONE);
        }
    }

    public interface OnMoveListener {
        void onMove(String command);
    }
}
