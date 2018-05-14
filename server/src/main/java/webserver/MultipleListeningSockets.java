package webserver;

import java.net.ServerSocket;
import java.net.Socket;

public class MultipleListeningSockets implements Runnable {

    private ServerSocket serverSocket;
    private ServerConfig config;

    public MultipleListeningSockets(ServerSocket serverSocket, ServerConfig config) {
        this.serverSocket = serverSocket;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new HandleRequestAndSendResponse(socket, config));
                thread.start();
            }
            } catch(Exception e){
                throw new RuntimeException(e);
            }
    }
}
