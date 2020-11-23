package com.dathuynh.rc;

import android.util.Log;

import java.io.*;
import java.net.Socket;

public class SocketClient extends Thread {

    private final MessageListener messageListener;
    BufferedReader in;
    PrintWriter out;

    public SocketClient(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void send(final String message) {
        new Thread(() -> {
            try {
                if (out != null) {
                    out.println(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void run() {
        try {
            Log.d("SocketClient", "Connecting to: " + Constants.SERVER_IP);

            // create a socket to make the connection with the server
            Socket socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);

            Log.d("SocketClient", "Connected!");

            try {
                // receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                // testing send message to server
                send("Android");

                // in this while the client listens for the messages sent by server
                while(true) {
                    String message = in.readLine();

                    if (message != null && messageListener != null) {
                        messageListener.messageReceived(message);
                    }
                }

            } catch (Exception e) {
                Log.e("SocketClient", "Send Error", e);
                socket.close();
            }

        } catch (Exception e) {
            Log.e("SocketClient", "Connect error!", e);
        }
    }

    public interface MessageListener {
        void messageReceived(String message);
    }
}
