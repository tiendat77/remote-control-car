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

import com.dathuynh.rc.ui.AccelerometerControl;
import com.dathuynh.rc.ui.GamePadControl;
import com.dathuynh.rc.ui.JoystickControl;
import com.dathuynh.rc.ui.FunctionControl;
import com.dathuynh.rc.service.Loader;
import com.dathuynh.rc.service.Notify;
import com.dathuynh.rc.service.Preference;
import com.dathuynh.rc.socket.SocketClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ControlFragment extends Fragment {

    private SocketClient socketClient;

    /* Util Services */
    private Preference preference; // Preferences management
    private Notify notify; // Notify message with Toast
    private Loader loader;

    /* Controller */
    private JoystickControl joystickControl; // Direction control
    private GamePadControl gamePadControl; // Direction control
    private AccelerometerControl accelerometerControl; // Direction control
    private FunctionControl functionControl; // Function control

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

        this.initServices();
    }

    @Override
    public void onResume() {
        super.onResume();

        closeSocket();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_control, container, false);

        this.initUIRef(root);
        this.setListener(root);

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.loadSettings();
        this.setConnectionStatus(Constants.NOT_CONNECTED);
    }

    /**
     * Initialize Views and Event listeners
     */
    private void initServices() {
        preference = new Preference(getContext());
        notify = new Notify(getActivity(), getContext());
        loader = new Loader(getActivity());
    }

    private void initUIRef(View view) {
        loader.setView(view.findViewById(R.id.loader_wrapper));

        functionControl = (FunctionControl) view.findViewById(R.id.control_function);
        gamePadControl = (GamePadControl) view.findViewById(R.id.control_game_pad);
        joystickControl = (JoystickControl) view.findViewById(R.id.control_joystick);
        accelerometerControl = (AccelerometerControl) view.findViewById(R.id.control_accelerometer);

        controlStatusText = (TextView) view.findViewById(R.id.control_status);
        connectionStatusText = (TextView) view.findViewById(R.id.connection_status);
        serverResponseText = (TextView) view.findViewById(R.id.server_response);
        connectionStatusDot = (FloatingActionButton) view.findViewById(R.id.button_connection_dot);

        directionLayoutContainer = (RelativeLayout) view.findViewById(R.id.control_direction_wrapper);
        functionLayoutContainer = (RelativeLayout) view.findViewById(R.id.control_function_wrapper);
    }

    private void setListener(View view) {
        /* Controller */
        if (functionControl != null) {
            functionControl.setOnCommandListener(new FunctionControl.OnCommandListener() {
                @Override
                public void onCommand(String command) {
                    sendCommand(command);
                }
            });
        }

        if (gamePadControl != null) {
            gamePadControl.setOnCommandListener(new GamePadControl.OnCommandListener() {
                @Override
                public void onCommand(String command) {
                    sendCommand(command);
                }
            });
        }

        if (joystickControl != null) {
            joystickControl.setOnCommandListener(new JoystickControl.OnCommandListener() {
                @Override
                public void onCommand(String command) {
                    sendCommand(command);
                }
            });
        }

        if (accelerometerControl != null) {
            accelerometerControl.setOnCommandListener(new AccelerometerControl.OnCommandListener() {
                @Override
                public void onCommand(String command) {
                    sendCommand(command);
                }
            });
        }

        /* Connection */
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

    /**
     * Config View by user settings
     */
    /* Load user settings */
    private void loadSettings() {
        /* Control direction method: by game-pad, by joystick, by accelerometer */
        int controlMethod = preference.getControlMethod();
        setControlVisibility(controlMethod);

        /* Control direction position: on left-hand or right-hand */
        int controlPosition = preference.getControlPosition();
        setControlPosition(controlPosition);

        /* Functions control theme */
        int functionsTheme = preference.getFunctionsTheme();
        setControlFunctionTheme(functionsTheme);
    }

    /* Set view by user setting */
    private void setControlVisibility(int controlMethod) {
        if (controlMethod == Preference.GAME_PAD) { // show game-pad
            joystickControl.hide();
            accelerometerControl.hide();
            gamePadControl.show();
            return;
        }

        if (controlMethod == Preference.JOYSTICK) { // show joystick
            gamePadControl.hide();
            accelerometerControl.hide();
            joystickControl.show();
            return;
        }

        if (controlMethod == Preference.ACCELEROMETER) { // show accelerometer
            joystickControl.hide();
            gamePadControl.hide();
            accelerometerControl.show();
            notify.pushNotify("Accelerometer feature is not supported yet :(");
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

    private void setControlFunctionTheme(int theme) {
        if (functionControl != null) {
            functionControl.setTheme(theme);
        }
    }

    /**
     * Socket Client
     */
    /* Show connection status */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setConnectionStatus(String status) {
        if (connectionStatusText != null) {
            connectionStatusText.setText(status);
        }

        notify.pushNotify(status);

        /* Connected successfully */
        if (status.equals(Constants.CONNECTED)) {
            connectionStatusDot.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_on));
            return;
        }

        /* Connect error or Not connected yet or Connection closed */
        connectionStatusDot.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_off));
        socketClient = null;
    }

    private void createSocket() {
        if (socketClient == null) {
            loader.show();

            SocketClient.SocketEvent socketEvent = new SocketClient.SocketEvent() {
                @Override
                public void messageReceived(String message) {
                    onReceiveMessage(message);
                }

                @Override
                public void connected() {
                    setConnectionStatus(Constants.CONNECTED);
                    loader.dismiss();
                }

                @Override
                public void connectError() {
                    setConnectionStatus(Constants.CONNECT_ERROR);
                    loader.dismiss();
                }

                @Override
                public void disconnected() {
                    setConnectionStatus(Constants.DISCONNECTED);
                }
            };

            String serverAddress = preference.getDeviceAddress();
            int serverPort = preference.getDevicePort();

            socketClient = new SocketClient(serverAddress, serverPort, socketEvent);
            socketClient.start();
        }
    }

    private void closeSocket() {
        if (socketClient != null) {
            socketClient.disconnect();
            socketClient = null;
        }
    }

    /* On message received from socket */
    @SuppressLint("SetTextI18n")
    public void onReceiveMessage(String message) {
        if (serverResponseText != null && message != null) {
            serverResponseText.setText("Res: " + message);
        }
    }

    /* Send message through socket */
    private void sendCommand(String command) {
        controlStatusText.setText(command);

        if (socketClient != null && command != null) {
            socketClient.send(command);
        }
    }

}