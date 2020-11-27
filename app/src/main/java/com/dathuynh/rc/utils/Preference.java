package com.dathuynh.rc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.dathuynh.rc.Constants;

public class Preference {

    final private SharedPreferences preferences;

    public static final int GAME_PAD = 0;
    public static final int JOYSTICK = 1;
    public static final int ACCELEROMETER = 2;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    public Preference(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getDeviceAddress() {
        return preferences.getString("device_ip", Constants.SERVER_IP);
    }

    public int getDevicePort() {
        String portInString = preferences.getString("device_port", String.valueOf(Constants.SERVER_PORT));

        try {
            return Integer.parseInt(portInString);

        } catch (Exception e) {
            return Constants.SERVER_PORT;
        }
    }

    /*
     * Control method
     * 0 - Game-pad button
     * 1 - Virtual Joystick
     * 2 - Tilt To Turn
     * */
    public int getControlMethod() {
        String METHOD = preferences.getString("control_method", Constants.CONTROL_GAME_PAD);

        if (METHOD.equals(Constants.CONTROL_GAME_PAD)) {
            return GAME_PAD;
        }

        if (METHOD.equals(Constants.CONTROL_JOYSTICK)) {
            return JOYSTICK;
        }

        return ACCELEROMETER;
    }

    public int getControlPosition() {
        String POSITION = preferences.getString("control_position", "right");

        if (POSITION.equals("right")) {
            return RIGHT;
        }

        return LEFT;
    }
}
