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
        if (fileName.equals("\\")) {
            if (new File(directory.toString() + File.separatorChar + "index.html").exists()) {
                filePath = Paths.get(directory.toString(), "index.html");
                fileName = filePath.toString();
            } else {
                //filePath = Paths.get("src","main","resources","defaultwebsite","index.html");
                //fileName = filePath.toString();
	            filePath = directory.toPath();
	            fileName = filePath.toString();

            }
        }
        else if (fileName.startsWith(File.separatorChar + "defweb/")) {
            filePath = Paths.get("src", "main", "resources", "defaultwebsite", fileName.split("/")[1]);
            fileName = filePath.toString();
        }
        else {
            filePath = Paths.get(directory.toString() + fileName);
        }
        if (Files.exists(filePath)) {
            statusLine = "HTTP/1.1 200 OK\r\n";
            if (Files.isDirectory(filePath)) {
            	if (new File(filePath.toString() + File.separatorChar + "index.html").exists()) {
            		filePath = Paths.get(filePath.toString(), "index.html");
            		fileName = filePath.toString();
	            } else {
            		DirectoryBrowserGenerator.generate(filePath.toFile(), directory);
            		body = Files.readAllBytes(Paths.get("src","main","resources","defaultwebsite","generatedResponse.html"));
	            }
            }
            if (body == null) {
	            body = Files.readAllBytes(filePath);
            }
            headers.add("Content-Length: " + body.length + "\r\n");
	        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extension.equals("txt")) {
                headers.add("Content-Type: text/plain\r\n");
            } else if (extension.equals("html")) {
                headers.add("Content-Type: text/html\r\n");
            }
        } else {
            statusLine = "HTTP/1.1 404 Not Found\r\n";
            fileName = Paths.get("src","main","resources","defaultwebsite", "404page.html").toString();
            body = Files.readAllBytes(Paths.get(fileName));
        }
        return new Response(statusLine, headers, body);
    }
}
