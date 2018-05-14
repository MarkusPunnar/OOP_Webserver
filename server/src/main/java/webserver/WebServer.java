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

    public static void main(String[] args) throws Exception {
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

    public void run() throws Exception {
        try (ServerSocket ss = new ServerSocket(1337);
             ServerSocket ss2 = new SSLHandler().getSSLHandler(1338)) {
            Map<String, String> mimeTypes = readMimeTypesFromFile();
            List<Filter> filters = createFilterInstances();
            ServerConfig motherOfAllPlugins = new ServerConfig(Paths.get(dirName), mimeTypes, new HashMap<>(), filters);
            createPluginInstances(motherOfAllPlugins, motherOfAllPlugins.getDynamicResponseURIs());
            Thread thread1 = new Thread(new MultipleListeningSockets(ss, motherOfAllPlugins));
            Thread thread2 = new Thread(new MultipleListeningSockets(ss2, motherOfAllPlugins));
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        }
    }

    private List<Filter> createFilterInstances() {
        List<Filter> appliedFilters = new ArrayList<>();
        for (Filter filter : ServiceLoader.load(Filter.class)) {
            appliedFilters.add(filter);
        }
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

    private void createPluginInstances(ServerConfig motherOfAllPlugins, Map<MappingInfo, HandlerInfo> pluginMap) {
        HandlerRegistration registration = new HandlerRegistration();
        for (RequestHandler requestHandler : ServiceLoader.load(RequestHandler.class)) {
            requestHandler.initialize(motherOfAllPlugins);
            registration.register(requestHandler, pluginMap);
        }
    }
}