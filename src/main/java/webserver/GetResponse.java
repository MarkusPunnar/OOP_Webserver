package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GetResponse {

    public Response getResponse(String fileName, File directory) throws IOException {
        String statusLine;
        List<String> headers = new ArrayList<>();
        byte[] body = null;
        Path filePath;
        String extension;
        if (fileName.equals("\\")) {
            fileName = Paths.get("resources","defaultwebsite","index.html").toString();
            filePath = Paths.get(fileName);
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        } /*else {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extension.equals("css")) {
                filePath = Paths.get("src","main","css","style.css");
            } else {
                filePath = Paths.get(directory.toString() + fileName);
            }
        }

*/
        else {
            filePath = Paths.get(directory.toString() + fileName);
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        System.out.println(filePath.toString());
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
            fileName = Paths.get("resources", "defaultwebsite", "404page.html").toString();
            body = Files.readAllBytes(Paths.get(fileName));
        }
        return new Response(statusLine, headers, body);
    }
}
