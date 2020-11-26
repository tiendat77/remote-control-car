package com.dathuynh.rc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class ControlFragment extends Fragment {

    private SocketClient socketClient;

    private Preference preference;
    private int controlMethod;

    private RelativeLayout controlGamePadLayout;
    private RelativeLayout controlJoystickLayout;
    private FloatingActionButton connectionStatusDot;
    private TextView connectionStatusText;

    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.preference = new Preference(getContext());
        this.loadSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setStatus(Constants.NOT_CONNECTED);
        this.loadSettings();
        this.setViewBySetting();
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
        this.setViewBySetting();
        this.setListener(view);
        this.setStatus(Constants.NOT_CONNECTED);
    }

    private void initView(View view) {
        controlGamePadLayout = (RelativeLayout) view.findViewById(R.id.control_game_pad);
        controlJoystickLayout = (RelativeLayout) view.findViewById(R.id.control_joystick);
        connectionStatusText = (TextView) view.findViewById(R.id.connection_status);
        connectionStatusDot = (FloatingActionButton) view.findViewById(R.id.button_connection_dot);
    }

    private void setListener(View view) {
        JoystickView joystick = (JoystickView) view.findViewById(R.id.joystick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (strength > 50) {
                    onJoystickMove(angle);
                }
            }
        }, 350);

        /* Direction buttons */
        view.findViewById(R.id.button_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("go");
            }
        });

        view.findViewById(R.id.button_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("ba");
            }
        });

        view.findViewById(R.id.button_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("le");
            }
        });

        view.findViewById(R.id.button_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("ri");
            }
        });

        /* Function buttons */
        view.findViewById(R.id.button_function1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("f1");
            }
        });

        view.findViewById(R.id.button_function2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("f2");
            }
        });

        view.findViewById(R.id.button_function3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("f3");
            }
        });

        view.findViewById(R.id.button_function4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("f4");
            }
        });

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

    /* Joystick handler */
    private void onJoystickMove(int angle) {
        if (angle > 45 && angle < 135) {
            sendCommand("go");
            return;
        }

        if (angle > 225 && angle < 315) {
            sendCommand("ba");
            return;
        }

        if (angle > 135 && angle < 225) {
            sendCommand("le");
            return;
        }

        if ((angle > 0 && angle < 45) || (angle < 359 && angle > 315)) {
            sendCommand("ri");
            return;
        }
    }

    /* Set view by user setting */
    private void setViewBySetting() {
        if (this.controlMethod == 0) { // show game-pad
            this.controlJoystickLayout.setVisibility(View.GONE);
            this.controlGamePadLayout.setVisibility(View.VISIBLE);

        } else { // show joystick
            this.controlJoystickLayout.setVisibility(View.VISIBLE);
            this.controlGamePadLayout.setVisibility(View.GONE);
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
            connectionStatusDot.setImageDrawable(getResources().getDrawable(R.drawable.ic_rocket_24));
            return;
        }

        /* Connect error or Not connected yet */
        connectionStatusDot.setImageDrawable(getResources().getDrawable(R.drawable.ic_setting_24));
    }

    /* Call device API get function status then update button icon */
    private void getFunctionStatus() {

    }

    private void createSocket() {
        if (socketClient == null) {
            SocketClient.SocketEvent socketEvent = new SocketClient.SocketEvent() {
                @Override
                public void messageReceived(String message) {
                    onReceiveMessage(message);
                }

                @Override
                public void connected() {
                    pushNotify("Connected!");
                    setStatus(Constants.CONNECTED);
                }

                @Override
                public void connectError() {
                    pushNotify("Connect error!");
                    setStatus(Constants.CONNECT_ERROR);
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

        } else {
            socketClient.send("bye");
            socketClient.stop();
            socketClient = null;
        }
    }

    /* On message received from socket */
    public void onReceiveMessage(String message) {
        Log.d("Socket", message);
    }

    /* Send message through socket */
    private void sendCommand(String command) {
        if (socketClient != null) {
            socketClient.send(command);
        }
    }

    /* Load settings */
    private void loadSettings() {
        this.controlMethod = preference.getControlMethod();
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