package webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
                throw new RuntimeException("Command line argument " + dirName + " is not an directory.");
            }
            System.out.println("Server file directory set as " + dirName);
            System.out.println("Ready for clients to connect");
            byte[] mimeTypesAsArray = readExtensionMimesFromFile();
            String[] mimeTypesAsString = new String(mimeTypesAsArray, 0, mimeTypesAsArray.length).split("\n");
            for (String mimeType : mimeTypesAsString) {
                String[] mimeInfo = mimeType.split(" ");
                mimeTypes.put(mimeInfo[0], mimeInfo[1]);
            }
            while (true) {
                Socket socket = ss.accept();
                Thread thread = new Thread(new HandleRequestAndSendResponse(socket, dirName, mimeTypes));
                thread.start();
            }
        }
    }

    private static byte[] readExtensionMimesFromFile() throws IOException {
        try (InputStream is = GetResponse.class.getClassLoader().getResourceAsStream("extensions.txt")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }
}