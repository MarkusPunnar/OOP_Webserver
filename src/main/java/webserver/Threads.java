package webserver;

import java.io.File;
import java.net.Socket;

public class Threads implements Runnable {
    private String filename;
    private Socket socket;

    public Threads(String filename, Socket socket) {
        this.filename = filename;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection accepted");
            File directory = new File(filename);
            if (!directory.isDirectory()) {
                throw new RuntimeException("Server file directory not found");
            }
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
