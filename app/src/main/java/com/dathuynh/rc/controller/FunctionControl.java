package com.dathuynh.rc.controller;

import android.view.View;
import android.widget.RelativeLayout;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FunctionControl {

    private final View view;
    private final OnClickListener onClickListener;

    private RelativeLayout layoutContainer;
    private FloatingActionButton triangleButton;
    private FloatingActionButton circleButton;
    private FloatingActionButton crossesButton;
    private FloatingActionButton squareButton;

    public FunctionControl(View view, OnClickListener onClickListener) {
        this.view = view;
        this.onClickListener = onClickListener;
        setUIRef();
        setListener();
    }

    private void setUIRef() {
        layoutContainer = view.findViewById(R.id.control_layout_function);
        triangleButton = view.findViewById(R.id.control_button_function1);
        circleButton = view.findViewById(R.id.control_button_function2);
        crossesButton = view.findViewById(R.id.control_button_function3);
        squareButton = view.findViewById(R.id.control_button_function4);
    }

    private void setListener() {
        if (triangleButton != null) {
            triangleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onActionClick(Constants.CMD_TRIANGLE);
                }
            });
        }

        if (circleButton != null) {
            circleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onActionClick(Constants.CMD_CIRCLE);
                }
            });
        }

        if (crossesButton != null) {
            crossesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onActionClick(Constants.CMD_CROSSES);
                }
            });
        }

        if (squareButton != null) {
            squareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onActionClick(Constants.CMD_SQUARE);
                }
            });
        }
    }

    private void hideFunctionPad() {
        if (layoutContainer != null) {
            layoutContainer.setVisibility(View.GONE);
        }
    }

    private void showFunctionPad() {
        if (layoutContainer != null) {
            layoutContainer.setVisibility(View.VISIBLE);
        }
    }

    private void onActionClick(String command) {
        if (onClickListener != null) {
            onClickListener.onClick(command);
        }
    }

    public interface OnClickListener {
        void onClick(String command);
    }
}
