package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Response {

    private final byte[] finalBytes = "\r\n".getBytes(StandardCharsets.UTF_8);
    private File directory;
    private String statusLine;
    private List<String> headers = new ArrayList<>();
    private byte[] body;
    private Request request;

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
        file.setCharAt(0, '\\');
        String requestFile = file.toString();
        String[] info = new String[2];
        info[0] = requestType;
        info[1] = requestFile;
        return info;
    }

    public void getResponse(Socket socket, String fileName) throws IOException {
        if (fileName.equals("\\")) {
            fileName = "\\index.html";
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        Path filePath = Paths.get(directory.toString() + fileName);
        if (Files.exists(filePath)) {
            statusLine = "HTTP/1.1 200 OK\r\n";
            body = Files.readAllBytes(filePath);
            headers.add("Content-Length: " + body.length + "\r\n");
            if (extension.equals("txt")) {
                headers.add("Content-Type: text/plain\r\n");
            } else if (extension.equals("html")) {
                headers.add("Content-Type: text/html\r\n");
            }
        } else {
            statusLine = "HTTP/1.1 404 Not Found\r\n";
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

    public void postResponse(Socket socket, String fileName) throws IOException {
        Path filePath = Paths.get(directory.toString() + fileName);
        if (fileName.equals("\\")) {
            statusLine = "HTTP/1.1 400 Bad Request\r\n";
        } else {
            if (Files.exists(filePath)) {
                statusLine = "HTTP/1.1 200 OK\r\n";
            } else {
                statusLine = "HTTP/1.1 201 Created\r\n";
            }
            try (FileOutputStream fos = new FileOutputStream(directory.toString() + fileName)) {
                fos.write(request.getBody());
            }
        }
        try (BufferedOutputStream bof = new BufferedOutputStream(socket.getOutputStream())) {
            bof.write(statusLine.getBytes("UTF-8"));
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
