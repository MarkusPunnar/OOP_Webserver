package webserver;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    public static void main(String[] args) throws Exception {
        final int portNumber = 1337;
        if (args.length == 0) {
            throw new RuntimeException("Missing command line argument for server file directory");
        }
        System.out.println("Server file directory set as " + args[0]);
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            System.out.println("Socket successfully initialized");
            while (true) {
                System.out.println("Awaiting new connection");
                File directory = new File(args[0]);
                if (!directory.isDirectory()) {
                    throw new RuntimeException("Server file directory not found");
                }
                Socket socket = ss.accept();
                Request request = new Request();
                request.readRequest(socket);
                System.out.println(request.getRequestLine());
                Response response = new Response(directory, request);
                String[] requestInfo = response.checkResponse();
                if (requestInfo[0].equals("GET")) {
                    response.getResponse(socket, requestInfo[1]);
                } else if (requestInfo[0].equals("POST")) {
                    response.postResponse(socket, requestInfo[1]);
                }
            }
        }
    }
}