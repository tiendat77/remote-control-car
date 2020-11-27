
import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server {

    // The server socket.
    private static ServerSocket serverSocket = null;

    // The client socket.
    private static Socket clientSocket = null;
    private static DataInputStream is = null;
    private static PrintStream os = null;

    // The user thread
    private static final int maxThreadsCount = 10;
    private static final ClientThread[] clientThreads = new ClientThread[maxThreadsCount];

    public static JTextArea textArea;

    /* ******************************************************** */

    /*Server Frame*/
    public static class ServerBoard extends JFrame {

        public ServerBoard() {
            textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
        }

    }

    /**
     * Updates the UI
     */
    public static void updateTextArea(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.append(message);
                textArea.append("\n");
            }
        });
    }

    /* ******************************************************** */
    /* Main */
    public static void main(String args[]) {
        //Create Frame
        JFrame frame = new ServerBoard();
        frame.setTitle("Server is Running");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // The default port number.
        int portNumber = 8000;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadServerSync <portNumber>\n"
                    + "Now using port " + portNumber);

        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        /*
         * Open a server socket on the portNumber (default 2222). Note that we can
         * not choose a port less than 1023 if we are not privileged users (root).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
            updateTextArea("*** Server is running on port: " + portNumber);

        } catch (IOException e) {
            System.out.println(e);
            updateTextArea(e.toString());
        }

        /*
         * Create a client socket for each connection and pass it
         * to a new client thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                is = new DataInputStream(clientSocket.getInputStream());
                os = new PrintStream(clientSocket.getOutputStream());

                int i = 0;
                  for (i = 0; i < maxThreadsCount; i++) {
                    if (clientThreads[i] == null) {
                      clientThreads[i] = new ClientThread(clientSocket, clientThreads, new ClientThread.OnMessageReceived() {
                        @Override
                        public void messageReceived(String message) {
                            updateTextArea(message);
                        }
                      });

                      clientThreads[i].start();
                      System.out.println("Client connect" + i);

                      break;
                    }
                  }

                  // Clean up threads
                  if (i == maxThreadsCount) {
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();

                    for (int j = 0; j < maxThreadsCount; j++) {
                      clientThreads[j].stop();
                      clientThreads[j] = null;
                    }
                  }

            } catch (IOException e) {
                System.out.println(e);
                updateTextArea(e.toString());
            }
        }
    }
}
