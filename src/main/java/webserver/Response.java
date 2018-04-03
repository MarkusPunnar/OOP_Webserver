package webserver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Response {

    private File directory;
    private String statusLine;
    private List<String> headers = new ArrayList<>();
    private byte[] body;
    private Request request;
    private final byte[] finalBytes = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);

    public Response(File directory, Request request) {
        this.directory = directory;
        this.request = request;
    }

    public String[] checkResponse() {
        String requestLine = request.getRequestLine();
        String[] requestParts = requestLine.split(" ");
        String requestType = requestParts[0];
        String requestProtocol = requestParts[2];
        StringBuilder file = new StringBuilder(requestParts[1]);
        file.setCharAt(0,'\\');
        String requestFile = file.toString();
        String[] info = new String[2];
        info[0] = requestType;
        info[1] = requestFile;
        return info;
    }

    public void getResponse(Socket socket, String fileName) throws Exception {
        if (fileName.equals("\\")) {
            fileName = "\\index.html";
        }
        String filePathString = directory.toString() + fileName;
        Path filePath = Paths.get(filePathString);
        if (!Files.exists(filePath)) {
            statusLine = "HTTP/1.1 404 Not Found\r\n";
        } else {
            statusLine = "HTTP/1.1 200 OK\r\n";
            body = Files.readAllBytes(filePath);
            headers.add("Content-Length: " + body.length);
        }
            try (BufferedOutputStream bof = new BufferedOutputStream(socket.getOutputStream())) {
                bof.write(statusLine.getBytes("UTF-8"));
                for (String header : headers) {
                    bof.write(header.getBytes("UTF-8"));
                }
                if (body != null) {
                    bof.write(finalBytes);
                    bof.write(body);
                }
            }
        }

    public String getStatusLine() {
        return statusLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
