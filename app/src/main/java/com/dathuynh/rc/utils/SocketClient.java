package com.dathuynh.rc.utils;

import android.util.Log;

import com.dathuynh.rc.Constants;

import java.io.*;
import java.net.Socket;

public class SocketClient extends Thread {

    private final String SERVER_ADDRESS;
    private final int SERVER_PORT;
    private final SocketEvent socketEvent;
    BufferedReader in;
    PrintWriter out;

    public SocketClient(String serverAddress, int serverPort, SocketEvent socketEvent) {
        this.SERVER_ADDRESS = serverAddress.equals("") ? Constants.SERVER_IP : serverAddress;
        this.SERVER_PORT = serverPort == 0 ? Constants.SERVER_PORT : serverPort;
        this.socketEvent = socketEvent;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void send(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (out != null) {
                        out.println(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void run() {
        try {
            Log.d("SocketClient", "Connecting to: " + this.SERVER_ADDRESS + ":" + this.SERVER_PORT);

            // create a socket to make the connection with the server
            Socket socket = new Socket(this.SERVER_ADDRESS, this.SERVER_PORT);

            Log.d("SocketClient", "Connected!");
            if (socketEvent != null) {
                socketEvent.connected();
            }

            try {
                // receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                // testing send message to server
                send("Android");

                // in this while the client listens for the messages sent by server
                while (true) {
                    String message = in.readLine();

                    if (message != null && socketEvent != null) {
                        socketEvent.messageReceived(message);
                    }
                }

            } catch (Exception e) {
                socket.close();

                if (socketEvent != null) {
                    socketEvent.disconnected();
                }
            }

        } catch (Exception e) {
            Log.e("SocketClient", "Connect error!", e);
            if (socketEvent != null) {
                socketEvent.connectError();
            }
        }
    }

    public interface SocketEvent {
        void messageReceived(String message);

        void connected();

        void connectError();

        void disconnected();
    }
}
