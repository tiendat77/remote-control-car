
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private String clientName = null;
    private Socket clientSocket = null;
    private static DataInputStream is = null;
    private static PrintStream os = null;

    private int maxClientsCount;
    private final ClientThread[] threads;
    private OnMessageReceived messageListener;

    public ClientThread(Socket clientSocket, ClientThread[] threads, OnMessageReceived messageListener) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.maxClientsCount = threads.length;
        this.messageListener = messageListener;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            /* Welcome the new the client. */
            os.println("Welcome-You are connected");
            os.println("Enter your name: ");

            String name;
            while (true) {
                name = is.readLine().trim();
                if (!name.equals("")) {
                    break;
                }
            }

            this.clientName = name;
            messageListener.messageReceived("*** " + clientName + " joined");

            /* Start the conversation. */
            while (true) {
                String line = is.readLine();

                /* Client quit the conversation by cmd /quit */
                if (line.startsWith("/quit")) {
                    break;
                }

                /* The message is public, broadcast it to all other clients. */
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i].clientName != null) {
                            threads[i].os.println(line);
                        }
                    }
                }

                this.os.println(line);
                messageListener.messageReceived("<" + clientName + ">: " + line);
            }

            messageListener.messageReceived("*** " + clientName + " disconnected.");

            /*
             * Clean up. Set the current thread variable to null so that a new client
             * could be accepted by the server.
             */
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            /*
             * Close the output stream, close the input stream, close the socket.
             */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    //must be implemented in Server
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
