import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainController {

    public static void main(String[] args) {

        int serverPort = Integer.parseInt(System.getenv("PORT"));

        try {
            ServerSocket socket = new ServerSocket(serverPort);

            while (true) {
                Socket clientSocket = socket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}