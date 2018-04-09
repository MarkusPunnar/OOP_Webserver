package webserver;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class WebServer {

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(1337)) {
            String dirName;
            if (args.length == 0) {
                dirName = Paths.get("").toAbsolutePath().toString();
            } else {
                dirName = args[0];
            }
            if (!new File(dirName).isDirectory()) {
                throw new RuntimeException("Server file directory not found: " + dirName);
            }
            System.out.println("Server file directory set as " + dirName);
            System.out.println("Ready for clients to connect");
            while (true) {
                Socket socket = ss.accept();
                Thread thread = new Thread(new Threads(dirName, socket));
                thread.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}