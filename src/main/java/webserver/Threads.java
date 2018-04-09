package webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Threads implements Runnable {

    private Socket socket;
    private String directory;
    private final byte[] finalBytes = "\r\n".getBytes(StandardCharsets.UTF_8);
    private final byte[] finalRequestBytes = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);


    public Threads(String directory, Socket socket) {
        this.socket = socket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            Request request = readRequest(socket);
            Response response;
            switch (request.getRequestMethod()) {
                case "GET":
                    GetResponse getResponse = new GetResponse(Paths.get(directory));
                    response = getResponse.getResponse(request);
                    break;
                case "POST":
                    if (request.getRequestURI().equals("\\form/test")) {
                        FormResponse formResponse = new FormResponse();
                        response = formResponse.formResponse(request);
                        break;
                    } else {
                        PostResponse postResponse = new PostResponse(Paths.get(directory));
                        response = postResponse.postResponse(request);
                        break;
                    }
                case "DELETE":
                    DeleteResponse deleteResponse = new DeleteResponse(Paths.get(directory));
                    response = deleteResponse.deleteResponse(request);
                    break;
                default: {
                    response = new Response(500, null, null);
                }
            }
            try (BufferedOutputStream bof = new BufferedOutputStream(socket.getOutputStream())) {
                bof.write(constructStatusLine(response).getBytes("UTF-8"));
                for (String header : response.getHeaders()) {
                    bof.write(header.getBytes("UTF-8"));
                }
                if (response.getBody() != null) {
                    bof.write(finalBytes);
                    bof.write(response.getBody());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Request readRequest(Socket socket) throws Exception {
        BufferedInputStream bf = new BufferedInputStream(socket.getInputStream());
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        Map<String, String> headers = new HashMap<>();
        byte[] body = null;
        byte[] buf = new byte[1024];
        boolean finished = false;
        while (!finished) {
            int usefulBytes = 0;
            for (int i = 0; i < 1024; i++) {
                buf[i] = (byte) bf.read();
                usefulBytes++;
                if (i > 2 && Arrays.equals(Arrays.copyOfRange(buf, i - 3, i + 1), finalRequestBytes)) {
                    finished = true;
                    break;
                }
            }
            byteOut.write(buf, 0, usefulBytes);
        }
        byte[] info = byteOut.toByteArray();
        String requestInfo = new String(buf, 0, info.length);
        String[] infoArray = requestInfo.split("\r\n");
        String requestLine = infoArray[0];
        String requestMethod = requestLine.substring(0, requestLine.indexOf(" "));
        String requestURI = requestLine.substring(requestLine.indexOf(" ") + 1, requestLine.lastIndexOf(" "));

        for (int i = 1; i < infoArray.length; i++) {
            String[] headerLine = infoArray[i].split(": ");
            if (headerLine.length > 1) {
                headers.put(headerLine[0], headerLine[1]);
            }
        }
        if (headers.get("Content-Length") != null) {
            int bodyLength = Integer.parseInt(headers.get("Content-Length"));
            body = new byte[bodyLength];
            int read = 0;
            int bytesRead = 0;
            while (read != -1 && bytesRead != bodyLength) {
                read = bf.read();
                body[bytesRead] = (byte) read;
                bytesRead++;
            }
        }
        return new Request(requestMethod, requestURI, headers, body);
    }

    private String constructStatusLine(Response response) {
        int statusCode = response.getStatusCode();
        return "HTTP/1.1 " + statusCode + " " + findProperStatusMessage(statusCode);
    }

    private String findProperStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default: throw new IllegalArgumentException("Unknown status code.");
        }
    }
}
