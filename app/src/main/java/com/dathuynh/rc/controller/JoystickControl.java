package com.dathuynh.rc.controller;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickControl {

    private final View view;
    private final OnMoveListener onMoveListener;

    private RelativeLayout joystickContainer;
    private JoystickView joystick;

    public JoystickControl(@NonNull View view, OnMoveListener onMoveListener) {
        this.view = view;
        this.onMoveListener = onMoveListener;
        this.setUIRef();
        this.setListener();
    }

    private void setUIRef() {
        joystickContainer = view.findViewById(R.id.control_layout_joystick);
        joystick = (JoystickView) view.findViewById(R.id.control_joystick);
    }

    private void setListener() {
        if (joystick != null) {
            joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
                @Override
                public void onMove(int angle, int strength) {
                    if (strength > 50) {
                        String command = onJoystickMove(angle);

                        if (onMoveListener != null) {
                            onMoveListener.onMove(command);
                        }
                    }
                }
            }, 350);
        }
    }

    private String onJoystickMove(int angle) {
        if (angle > 45 && angle < 135) {
            return Constants.CMD_GO;
        }

        if (angle > 225 && angle < 315) {
            return Constants.CMD_BACK;
        }

        if (angle > 135 && angle < 225) {
            return Constants.CMD_LEFT;
        }

        if ((angle > 0 && angle < 45) || (angle < 359 && angle > 315)) {
            return Constants.CMD_RIGHT;
        }

        return "";
    }

    public void hideJoystick() {
        if (joystickContainer != null) {
            joystickContainer.setVisibility(View.GONE);
        }
    }

    public void showJoystick() {
        if (joystickContainer != null) {
            joystickContainer.setVisibility(View.VISIBLE);
        }
    }

    public interface OnMoveListener {
        void onMove(String command);
    }
}
