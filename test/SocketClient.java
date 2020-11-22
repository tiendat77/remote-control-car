import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient extends Thread {

    private String serverMessage;
    private String SERVER_IP;
    private OnMessageReceived mMessageListener = null;
    private Socket socket;
    private boolean mRun = false;

    BufferedReader in;
    PrintWriter out;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages
     * received from server
     */
    public SocketClient(OnMessageReceived listener, String SERVER_IP) {
        this.mMessageListener = listener;
        if (SERVER_IP.equals(""))
            this.SERVER_IP = Constants.SERVER_IP;
        else
            this.SERVER_IP = SERVER_IP;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (out != null)
                        out.println(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopClient() {
        mRun = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        mRun = true;

        try {

            // here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Log.e("serverAddr", serverAddr.toString());
            Log.e("TCP Client", "C: Connecting...");

            // create a socket to make the connection with the server
            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
            Log.e("TCP Server IP", SERVER_IP);

            try {

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                // receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);

                // send the message to the server
                sendMessage("hello");

                // in this while the client listens for the messages sent by the
                // server
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        // call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '"
                        + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                // the socket must be closed. It is not possible to reconnect to
                // this socket
                // after it is closed, which means a new socket instance has to
                // be created.
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    // Declare the interface. The method messageReceived(String message) will
    // must be implemented in the MyActivity
    // class at on asyncTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
