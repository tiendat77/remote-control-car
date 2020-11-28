package com.dathuynh.rc.service;

import android.app.Activity;
import android.view.View;

public class Loader {

    Activity activity;
    View view;

    public Loader(Activity activity) {
        this.activity = activity;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void show() {
        if (activity != null && view != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void dismiss() {
        if (activity != null && view != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        view.setVisibility(View.GONE);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
