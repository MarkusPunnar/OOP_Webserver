package webserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
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

    private void run() throws Exception {
        try (ServerSocket httpSocket = new ServerSocket(1337);
             ServerSocket httpsSocket = new SSLHandler().getSSLHandler(1338)) {
            Map<String, String> mimeTypes = readMimeTypesFromFile();
            List<Filter> filters = createFilterInstances();
            ServerConfig motherOfAllPlugins = new ServerConfig(Paths.get(dirName), mimeTypes, filters);
            createPluginInstances(motherOfAllPlugins, motherOfAllPlugins.getDynamicResponseURIs());
            Thread httpSocketListener = new Thread(new MultipleListeningSockets(httpSocket, motherOfAllPlugins));
            Thread httpsSocketListener = new Thread(new MultipleListeningSockets(httpsSocket, motherOfAllPlugins));
            httpSocketListener.start();
            httpsSocketListener.start();
            httpSocketListener.join();
            httpsSocketListener.join();
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
        byte[] mimeTypesAsArray = WebServerUtil.readFileFromClasspathDirectory("requiredfiles", "extensions.txt");
        String[] mimeTypesAsString = new String(mimeTypesAsArray, "UTF-8").split("\n");
        for (String mimeType : mimeTypesAsString) {
            String[] mimeInfo = mimeType.split(" ");
            mimeMap.put(mimeInfo[0], mimeInfo[1]);
        }
        return mimeMap;
    }

    private void createPluginInstances(ServerConfig motherOfAllPlugins, Map<MappingInfo, HandlerInfo> pluginMap) throws Exception {
        HandlerRegistration registration = new HandlerRegistration();
        for (RequestHandler requestHandler : ServiceLoader.load(RequestHandler.class)) {
            System.out.println("Registered plugin: " + requestHandler.getClass().getName());
            requestHandler.initialize(motherOfAllPlugins);
            registration.register(requestHandler, pluginMap);
        }
    }
}