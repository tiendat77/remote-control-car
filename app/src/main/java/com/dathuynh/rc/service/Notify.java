package com.dathuynh.rc.service;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class Notify {

    Activity activity;
    Context context;

    public Notify(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void pushNotify(String message) {
        if (activity != null && context != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
