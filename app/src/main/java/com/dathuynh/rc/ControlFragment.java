package com.dathuynh.rc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dathuynh.rc.controller.AccelerometerControl;
import com.dathuynh.rc.controller.FunctionControl;
import com.dathuynh.rc.controller.GamePadControl;
import com.dathuynh.rc.controller.JoystickControl;
import com.dathuynh.rc.controller.LoaderControl;
import com.dathuynh.rc.utils.Preference;
import com.dathuynh.rc.utils.SocketClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ControlFragment extends Fragment {

    private SocketClient socketClient;

    private Preference preference;

    /* Controller */
    private JoystickControl joystickControl;
    private GamePadControl gamePadControl;
    private AccelerometerControl accelerometerControl;
    private FunctionControl functionControl;
    private LoaderControl loaderControl;

    private RelativeLayout directionLayoutContainer;
    private RelativeLayout functionLayoutContainer;

    /* Socket connection */
    private FloatingActionButton connectionStatusDot;
    private TextView connectionStatusText;
    private TextView controlStatusText;
    private TextView serverResponseText;

    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.preference = new Preference(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        this.loadSettings();
        this.setStatus(Constants.NOT_CONNECTED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initView(view);
        this.initController(view);

        this.loadSettings();

        this.setListener(view);
        this.setStatus(Constants.NOT_CONNECTED);
    }

    private void initView(View view) {
        controlStatusText = (TextView) view.findViewById(R.id.control_status);
        connectionStatusText = (TextView) view.findViewById(R.id.connection_status);
        serverResponseText = (TextView) view.findViewById(R.id.server_response);
        connectionStatusDot = (FloatingActionButton) view.findViewById(R.id.button_connection_dot);

        directionLayoutContainer = (RelativeLayout) view.findViewById(R.id.control_direction_wrapper);
        functionLayoutContainer = (RelativeLayout) view.findViewById(R.id.control_function_wrapper);
    }

    private void initController(View view) {
        loaderControl = new LoaderControl(getActivity(), view);

        joystickControl = new JoystickControl(view, new JoystickControl.OnMoveListener() {
            @Override
            public void onMove(String command) {
                sendCommand(command);
            }
        });

        gamePadControl = new GamePadControl(view, new GamePadControl.OnMoveListener() {
            @Override
            public void onMove(String command) {
                sendCommand(command);
            }
        });

        this.accelerometerControl = new AccelerometerControl(view, new AccelerometerControl.OnMoveListener() {
            @Override
            public void onMove(String command) {
                sendCommand(command);
            }
        });

        functionControl = new FunctionControl(view, new FunctionControl.OnClickListener() {
            @Override
            public void onClick(String command) {
                sendCommand(command);
            }
        });
    }

    private void setListener(View view) {
        view.findViewById(R.id.button_connection_dot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSocket();
            }
        });

        view.findViewById(R.id.button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /* Set view by user setting */
    private void setControlVisibility(int controlMethod) {
        if (controlMethod == Preference.GAME_PAD) { // show game-pad
            joystickControl.hideJoystick();
            accelerometerControl.hideAccelerometer();
            gamePadControl.showGamePad();
            return;
        }

        if (controlMethod == Preference.JOYSTICK){ // show joystick
            gamePadControl.hideGamePad();
            accelerometerControl.hideAccelerometer();
            joystickControl.showJoystick();
            return;
        }

        if (controlMethod == Preference.ACCELEROMETER) { // show accelerometer
            // TODO: accelerometer control
            joystickControl.hideJoystick();
            accelerometerControl.hideAccelerometer();
            gamePadControl.showGamePad();
            pushNotify("Accelerometer feature is not supported yet :(");
        }
    }

    private void setControlPosition(int position) {
        RelativeLayout.LayoutParams directionLayoutParams = (RelativeLayout.LayoutParams) directionLayoutContainer.getLayoutParams();
        RelativeLayout.LayoutParams functionLayoutParams = (RelativeLayout.LayoutParams) functionLayoutContainer.getLayoutParams();

        if (position == Preference.LEFT) {
            /* Move direction control layout to the left of the screen */
            directionLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            directionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);

            /* Move function control layout to the right of the screen */
            functionLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            functionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);

            return;
        }

        if (position == Preference.RIGHT) {
            /* Move direction control layout to the left of the screen */
            directionLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            directionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);

            /* Move function control layout to the right of the screen */
            functionLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            functionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        }
    }

    /* Show connection status */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setStatus(String status) {
        if (this.connectionStatusText != null) {
            this.connectionStatusText.setText(status);
        }

        /* Connected successfully */
        if (status.equals(Constants.CONNECTED)) {
            connectionStatusDot.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_on));
            return;
        }

        /* Connect error or Not connected yet */
        connectionStatusDot.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_off));
    }

    /* Call device API get function status then update button icon */
    private void getFunctionStatus() {

    }

    private void createSocket() {
        if (socketClient == null) {
            loaderControl.showLoading();

            SocketClient.SocketEvent socketEvent = new SocketClient.SocketEvent() {
                @Override
                public void messageReceived(String message) {
                    onReceiveMessage(message);
                }

                @Override
                public void connected() {
                    pushNotify("Connected!");
                    setStatus(Constants.CONNECTED);
                    loaderControl.dismissLoading();
                }

                @Override
                public void connectError() {
                    pushNotify("Connect error!");
                    setStatus(Constants.CONNECT_ERROR);
                    socketClient = null;
                    loaderControl.dismissLoading();
                }

                @Override
                public void disconnected() {
                    pushNotify("Disconnected!");
                }
            };

            String serverAddress = preference.getDeviceAddress();
            int serverPort = preference.getDevicePort();

            socketClient = new SocketClient(serverAddress, serverPort, socketEvent);
            socketClient.start();

        }
    }

    /* On message received from socket */
    @SuppressLint("SetTextI18n")
    public void onReceiveMessage(String message) {
        if (serverResponseText != null) {
            serverResponseText.setText("Res: " + message);
        }
    }

    /* Send message through socket */
    private void sendCommand(String command) {
        if (socketClient != null) {
            socketClient.send(command);
            controlStatusText.setText(command);
        }
    }

    /* Load settings */
    private void loadSettings() {
        /* load control direction method: by game-pad, by joystick, by accelerometer */
        int controlMethod = preference.getControlMethod();
        setControlVisibility(controlMethod);

        int controlPosition = preference.getControlPosition();
        setControlPosition(controlPosition);
    }

    /* Notify message with Toast */
    public void pushNotify(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}