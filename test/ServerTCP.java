import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
public class ServerTCP {
    public static void main(String argv[]) throws Exception
    {
        String sentence_from_client;
        String sentence_to_client;

        //Tạo socket server, chờ tại cổng '8000'
        ServerSocket welcomeSocket = new ServerSocket(8000);

        while(true) {
            //chờ yêu cầu từ client
            Socket connectionSocket = welcomeSocket.accept();

            //Tạo input stream, nối tới Socket
            BufferedReader inFromClient =
                new BufferedReader(new
                    InputStreamReader(connectionSocket.getInputStream()));

            //Tạo outputStream, nối tới socket
            DataOutputStream outToClient =
                new DataOutputStream(connectionSocket.getOutputStream());

            //Đọc thông tin từ socket
            sentence_from_client = inFromClient.readLine();

            sentence_to_client = sentence_from_client +" (Server accepted!)" + '\n';
            //ghi dữ liệu ra socket
            outToClient.writeBytes(sentence_to_client);
            return;
        }

    }
}
