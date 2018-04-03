package webserver;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    public static void main(String[] args) throws Exception {
        final int portNumber = 1337;
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            System.out.println("Socket successfully initialized");
            while (true) {
                File directory = new File(args[0]);
                if (!directory.isDirectory()) {
                    throw new RuntimeException("Root directory not found");
                }
                Socket socket = ss.accept();
                Request request = new Request();
                request.readRequest(socket);
                System.out.println(request.getRequestLine());
                Response response = new Response(directory,request);
                String[] requestInfo = response.checkResponse();
                if (requestInfo[0].equals("GET")) {
                    response.getResponse(socket, requestInfo[1]);
                }
            }
        }
    }
}