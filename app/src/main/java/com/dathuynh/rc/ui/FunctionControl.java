package com.dathuynh.rc.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dathuynh.rc.Constants;
import com.dathuynh.rc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FunctionControl extends RelativeLayout {

    private OnCommandListener onCommandListener;

    private FloatingActionButton triangleButton;
    private FloatingActionButton circleButton;
    private FloatingActionButton crossesButton;
    private FloatingActionButton squareButton;

    public static final int CLASSIC = 0;
    public static final int GAME_PAD = 1;

    public FunctionControl(Context context) {
        super(context);
        init(context);
    }

    public FunctionControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.ui_function_control, this);
        setUIRef();
        setListener();
    }

    private void setUIRef() {
        triangleButton = (FloatingActionButton) getChildAt(0);
        circleButton = (FloatingActionButton) getChildAt(1);
        crossesButton = (FloatingActionButton) getChildAt(2);
        squareButton = (FloatingActionButton) getChildAt(3);
    }

    private void setListener() {
        if (triangleButton != null) {
            triangleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_TRIANGLE);
                }
            });
        }

        if (circleButton != null) {
            circleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_CIRCLE);
                }
            });
        }

        if (crossesButton != null) {
            crossesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_CROSSES);
                }
            });
        }

        if (squareButton != null) {
            squareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCommand(Constants.CMD_SQUARE);
                }
            });
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setGamePadTheme() {
        if (triangleButton != null) {
            triangleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_triangle));
        }

        if (circleButton != null) {
            circleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle));
        }

        if (crossesButton != null) {
            crossesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_crosses));
        }

        if (squareButton != null) {
            squareButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_square));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setClassicTheme() {
        if (triangleButton != null) {
            triangleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_triangle_lighting));
        }

        if (circleButton != null) {
            circleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_signal_right));
        }

        if (crossesButton != null) {
            crossesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_crosses_bell));
        }

        if (squareButton != null) {
            squareButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_square_signal_left));
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

    public void setTheme(int THEME) {
        if (THEME == CLASSIC) {
            setClassicTheme();
            return;
        }

        if (THEME == GAME_PAD) {
            setGamePadTheme();
        }
    }

    public void setOnCommandListener(OnCommandListener onCommandListener) {
        this.onCommandListener = onCommandListener;
    }

    public interface OnCommandListener {
        void onCommand(String command);
    }

}
