package com.dathuynh.rc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Preference {
    final private SharedPreferences preferences;
    final private Context context;

    public Preference(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getDeviceAddress() {
        return preferences.getString("device_ip", Constants.SERVER_IP);
    }

    public int getDevicePort() {
        String portInString = preferences.getString("device_port", String.valueOf(Constants.SERVER_PORT));

        try {
            int PORT = Integer.parseInt(portInString);
            return PORT;

        } catch (Exception e) {
            return Constants.SERVER_PORT;
        }
    }

    /*
     * Control method
     * 0 - Game-pad button
     * 1 - Virtual Joystick
     * */
    public int getControlMethod() {
        String METHOD = preferences.getString("control_method", "game_pad");

        if (METHOD.equals("game_pad")) {
            return 0;
        }

        return 1;
    }
}
