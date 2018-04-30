package webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

public class WebServer {

    private String dirName;

    public WebServer(String dirName) {
        this.dirName = dirName;
    }

    public static void main(String[] args) throws IOException {
        String dirName;
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
        new WebServer(dirName).run();
    }

    public void run() throws IOException {
        try (ServerSocket ss = new ServerSocket(1337)) {
            Map<String, String> mimeTypes = readMimeTypesFromFile();
            List<Filter> filters = createFilterInstances();
            ServerConfig motherOfAllPlugins = new ServerConfig(Paths.get(dirName), mimeTypes, new HashMap<>(), filters);
            createPluginInstances(motherOfAllPlugins);
            while (true) {
                Socket socket = ss.accept();
                Thread thread = new Thread(new HandleRequestAndSendResponse(socket, motherOfAllPlugins));
                thread.start();
            }
        }
    }

    private List<Filter> createFilterInstances() {
        List<Filter> appliedFilters = new ArrayList<>();
        appliedFilters.add(new LoginFilter());
        return appliedFilters;
    }

    private Map<String, String> readMimeTypesFromFile() throws IOException {
        Map<String, String> mimeMap = new HashMap<>();
        byte[] mimeTypesAsArray = WebServerUtil.readFileFromClasspath("extensions.txt");
        String[] mimeTypesAsString = new String(mimeTypesAsArray, "UTF-8").split("\n");
        for (String mimeType : mimeTypesAsString) {
            String[] mimeInfo = mimeType.split(" ");
            mimeMap.put(mimeInfo[0], mimeInfo[1]);
        }
        return mimeMap;
    }

    private void createPluginInstances(ServerConfig motherOfAllPlugins) {
        for (RequestHandler requestHandler : ServiceLoader.load(RequestHandler.class)) {
            requestHandler.initialize(motherOfAllPlugins);
            requestHandler.register(motherOfAllPlugins.getDynamicResponseURIs());
        }
    }
}