package webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class WebServer {

    public static void main(String[] args) throws IOException {
        try (ServerSocket ss = new ServerSocket(1337)) {
            String dirName;
            Map<String, String> mimeTypes = new HashMap<>();
            if (args.length == 0) {
                throw new RuntimeException("Server file directory not assigned.");
            } else {
                dirName = args[0];
            }
            if (!new File(dirName).isDirectory()) {
                throw new RuntimeException("Command line argument " + dirName + " is not a directory.");
            }
            System.out.println("Server file directory set as " + dirName);
            System.out.println("Ready for clients to connect");
            byte[] mimeTypesAsArray = WebServerUtil.readFileFromClasspath("extensions.txt");
            String[] mimeTypesAsString = new String(mimeTypesAsArray, "UTF-8").split("\n");
            for (String mimeType : mimeTypesAsString) {
                String[] mimeInfo = mimeType.split(" ");
                mimeTypes.put(mimeInfo[0], mimeInfo[1]);
            }
            Map<String, RequestHandler> dynamicResponseURIs = new HashMap<>();
            ServerConfig motherOfAllPlugins = new ServerConfig(Paths.get(dirName), mimeTypes, dynamicResponseURIs);
            for (RequestHandler requestHandler : ServiceLoader.load(RequestHandler.class)) {
                requestHandler.initialize(motherOfAllPlugins);
                requestHandler.register(dynamicResponseURIs);
            }
            while (true) {
                Socket socket = ss.accept();
                Thread thread = new Thread(new HandleRequestAndSendResponse(socket, motherOfAllPlugins));
                thread.start();
            }
        }
    }
}