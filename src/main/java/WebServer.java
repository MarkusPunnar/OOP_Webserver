package main.java;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    public static void main(String[] args) throws Exception {
        final int portNumber = 1337;
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            while (true) {
                Socket socket = ss.accept();
                Request request = new Request();
                request.readRequest(socket);
                System.out.println(request.getRequestLine());
                System.out.println(request.getHeaders().get("Content-Length"));
                if (request.getBody() != null && request.getBody().length != 0) {
                    System.out.println(new String(request.getBody()));
                }
            }
        }
    }
}