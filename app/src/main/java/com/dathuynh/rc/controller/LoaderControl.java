package com.dathuynh.rc.controller;

import android.app.Activity;
import android.view.View;

import com.dathuynh.rc.R;

public class LoaderControl {

    private Activity activity;
    private View view;
    private View loaderContainer;

    public LoaderControl(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        setUIRef();
    }

    private void setUIRef() {
        loaderContainer = view.findViewById(R.id.loading_layout);
    }

    public void showLoading() {
        if (loaderContainer != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loaderContainer.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void dismissLoading() {
        if (loaderContainer != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        loaderContainer.setVisibility(View.GONE);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
