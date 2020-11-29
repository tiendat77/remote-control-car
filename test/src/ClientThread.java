
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private String clientName = null;
    private Socket clientSocket = null;
    private static DataInputStream is = null;
    private static PrintStream os = null;

    private int maxThreads;
    private OnMessageReceived messageListener;

    public ClientThread(Socket clientSocket, OnMessageReceived messageListener) {
        this.clientSocket = clientSocket;
        this.messageListener = messageListener;
    }

    public void run() {

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            /* Welcome the new the client. */
            os.println("Enter your name: ");

            while (true) {
                String name = is.readLine();

                if (!name.equals("") && name != null) {
                    clientName = name;

                    if (messageListener != null) {
                      messageListener.messageReceived("*** " + clientName + " joined");
                    }

                    break;
                }
            }

            /* Start the conversation. */
            while (true) {
              try {
                String line = is.readLine();

                /* Client quit the conversation by cmd /quit */
                if (line.startsWith("/quit")) {
                    break;
                }

                this.os.println("ok");
                messageListener.messageReceived("<" + clientName + ">: " + line);

              } catch (Exception e) {
                System.out.println(e.toString());
                break;
              }
            }

            messageListener.messageReceived("*** " + clientName + " disconnected.");
            messageListener.disconnected(this);

            /*
             * Close the output stream, close the input stream, close the socket.
             */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e);
            messageListener.disconnected(this);
        }
    }

    //must be implemented in Server
    public interface OnMessageReceived {
        public void messageReceived(String message);
        public void disconnected(ClientThread thread);
    }

}
