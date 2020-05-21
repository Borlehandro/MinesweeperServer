import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class MainController {

    public static void main(String[] args) throws IOException {

        int serverPort = Integer.parseInt(System.getenv("PORT"));

        System.err.println(serverPort);

        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        server.createContext("/api/hello", (exchange -> {
            String respText = "Hello!";
            BufferedReader input = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String requestText = input.readLine();
            System.err.println("GET USER INPUT");
            if(!requestText.isEmpty())
                respText = requestText;
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        }));

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}