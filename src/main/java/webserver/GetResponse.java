package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GetResponse {

    private Response response;

    public void getResponse(String fileName, File directory) throws IOException {
        String statusLine;
        List<String> headers = new ArrayList<>();
        byte[] body = null;
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
        response = new Response(statusLine, headers, body);
    }

    public Response getResponse() {
        return response;
    }
}
