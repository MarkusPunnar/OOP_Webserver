package webserver;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class WebServer {

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(1337)) {
        	String fileName;
            if (args.length == 0) {
                fileName = Paths.get("src", "test", "java").toString();
            } else {
	            fileName = args[0];

            }
            File directory = new File(fileName);
            if (!directory.isDirectory()) {
                throw new RuntimeException("Server file directory not found");
            }
            System.out.println("Server file directory set as " + fileName);
            System.out.println("Ready for clients to connect");
            while (true) {
                Socket s = ss.accept();
                Threads threads = new Threads(directory, s);
                Thread thread = new Thread(threads);
                thread.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}