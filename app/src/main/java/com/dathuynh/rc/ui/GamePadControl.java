package com.dathuynh.rc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GamePadControl extends RelativeLayout {

    private OnCommandListener onCommandListener;

    private FloatingActionButton upButton;
    private FloatingActionButton downButton;
    private FloatingActionButton leftButton;
    private FloatingActionButton rightButton;

    public GamePadControl(Context context) {
        super(context);
        init(context);
    }

    public GamePadControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.ui_game_pad_control, this);
        setUIRef();
        setListener();
    }

    private void setUIRef() {
        upButton = (FloatingActionButton) getChildAt(0);
        downButton = (FloatingActionButton) getChildAt(1);
        leftButton = (FloatingActionButton) getChildAt(2);
        rightButton = (FloatingActionButton) getChildAt(3);
    }

    private void setListener() {
        if (upButton != null) {
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_GO);
                }
            });
        }

        if (downButton != null) {
            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_BACK);
                }
            });
        }

        if (leftButton != null) {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_LEFT);
                }
            });
        }

        if (rightButton != null) {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_RIGHT);
                }
            });
        }
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