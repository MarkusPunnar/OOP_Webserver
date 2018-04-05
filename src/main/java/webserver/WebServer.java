package webserver;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(1337)) {
            if (args.length == 0) {
                throw new RuntimeException("Missing command line argument for server file directory");
            }
            String dirName = args[0];
            File directory = new File(dirName);
            if (!directory.isDirectory()) {
                throw new RuntimeException("Server file directory not found");
            }
            System.out.println("Server file directory set as " + dirName);
            System.out.println("Ready for clients to connect.");
            while (true) {
                Socket socket = ss.accept();
                System.out.println("Connection accepted");
                Thread thread = new Thread(new Threads(directory, socket));
                thread.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}