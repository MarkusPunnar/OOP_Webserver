package webserver;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public static void main(String[] args) throws IOException {
        try (ServerSocket ss = new ServerSocket(1337)) {
        	String fileName;
            if (args.length == 0) {
                fileName = ".\\src\\test\\java";
            } else {
	            fileName = args[0];

            }
            System.out.println("Server file directory set as " + fileName);
            System.out.println("Ready for clients to connect");
            while (true) {
                Socket s = ss.accept();
                Threads threads = new Threads(fileName, s);
                Thread thread = new Thread(threads);
                thread.start();
            }
        }
    }
}