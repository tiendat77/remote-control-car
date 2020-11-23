package com.dathuynh.rc;

import android.os.Bundle;

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
            socketClient.send("hello");
        }
    }

    public void onReceiveMessage(String message) {
        Log.d("Socket", message);
    }
}