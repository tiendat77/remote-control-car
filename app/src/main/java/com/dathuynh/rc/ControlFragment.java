package com.dathuynh.rc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ControlFragment extends Fragment {

    private SocketClient socketClient;

    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.createSocket();

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
    }

    public void createSocket() {
        if (socketClient == null) {
            socketClient = new SocketClient(new SocketClient.MessageListener() {
                @Override
                public void messageReceived(String message) {
                    onReceiveMessage(message);
                }
            });

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
}