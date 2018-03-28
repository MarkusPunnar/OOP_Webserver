package Webserver;

import java.net.Socket;

public class WebThread implements Runnable {

    private Socket socket;
    private Request request;

    public WebThread(Socket socket, Request request) {
        this.socket = socket;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            request.readRequest(socket);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
