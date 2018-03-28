package main.java;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class Request {

    private String requestLine;
    private HashMap<String, String> headers = new HashMap<>();
    private byte[] body;
    private final byte[] finalBytes = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);


    public void readRequest(Socket socket) throws Exception {
        try (BufferedInputStream bf = new BufferedInputStream(socket.getInputStream())) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            boolean finished = false;
            int usefulBytes = 0;
            while (!finished) {
                for (int i = 0; i < 1024; i++) {
                    buf[i] = (byte) bf.read();
                    usefulBytes++;
                    if (i > 2 && Arrays.equals(Arrays.copyOfRange(buf, i - 3, i + 1), finalBytes)) {
                        finished = true;
                        break;
                    }
                }
                byteOut.write(buf, 0, usefulBytes);
            }
            byte[] info = byteOut.toByteArray();
            String requestInfo = new String(buf, 0, info.length);
            String[] infoArray = requestInfo.split("\r\n");
            requestLine = infoArray[0];
            for (int i = 1; i < infoArray.length; i++) {
                String[] headerLine = infoArray[i].split(": ");
                if (headerLine.length > 1) {
                    headers.put(headerLine[0], headerLine[1]);
                }
            }
            if (headers.get("Content-Length") != null) {
                int bodyLength = Integer.parseInt(headers.get("Content-Length"));
                body = new byte[bodyLength];
                int read = bf.read();
                int bytesRead = 1;
                while(read != -1 && bytesRead != bodyLength) {
                    body[bytesRead - 1] = (byte) read;
                    bytesRead++;
                    read = bf.read();
                }
            }
        } finally {
            socket.close();
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}

