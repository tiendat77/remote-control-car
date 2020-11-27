package com.dathuynh.rc.controller;

import android.view.View;
import android.widget.RelativeLayout;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GamePadControl {

    private final View view;
    private final OnMoveListener onMoveListener;

    private RelativeLayout layoutContainer;
    private FloatingActionButton upButton;
    private FloatingActionButton downButton;
    private FloatingActionButton leftButton;
    private FloatingActionButton rightButton;

    public GamePadControl(View view, OnMoveListener onMoveListener) {
        this.view = view;
        this.onMoveListener = onMoveListener;
        setUIRef();
        setListener();
    }

    private void setUIRef() {
        layoutContainer = view.findViewById(R.id.control_layout_game_pad);
        upButton = view.findViewById(R.id.control_button_top);
        downButton = view.findViewById(R.id.control_button_bottom);
        leftButton = view.findViewById(R.id.control_button_left);
        rightButton = view.findViewById(R.id.control_button_right);
    }

    private void setListener() {
        if (upButton != null) {
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGamePadPress(Constants.CMD_GO);
                }
            });
        }

        if (downButton != null) {
            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGamePadPress(Constants.CMD_BACK);
                }
            });
        }

        if (leftButton != null) {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGamePadPress(Constants.CMD_LEFT);
                }
            });
        }

        if (rightButton != null) {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGamePadPress(Constants.CMD_RIGHT);
                }
            });
        }
    }

    private void onGamePadPress(String command) {
        if (onMoveListener != null) {
            onMoveListener.onMove(command);
        }
    }

    public void hideGamePad() {
        if (layoutContainer != null) {
            layoutContainer.setVisibility(View.GONE);
        }
    }

    public void showGamePad() {
        if (layoutContainer != null) {
            layoutContainer.setVisibility(View.VISIBLE);
        }
    }

    public interface OnMoveListener {
        void onMove(String command);
    }
}
