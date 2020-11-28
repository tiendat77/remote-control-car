package com.dathuynh.rc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickControl extends RelativeLayout {

    private OnCommandListener onCommandListener;
    private JoystickView joystickView;

    public JoystickControl(Context context) {
        super(context);
        init(context);
    }

    public JoystickControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.ui_joystick_control, this);
        setUIRef();
        setListener();
    }

    private void setUIRef() {
        joystickView = (JoystickView) getChildAt(0);
    }

    private void setListener() {
        if (joystickView != null) {
            joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
                @Override
                public void onMove(int angle, int strength) {
                    if (strength > 50) {
                        String command = onJoystickMove(angle);
                        onCommand(command);
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
