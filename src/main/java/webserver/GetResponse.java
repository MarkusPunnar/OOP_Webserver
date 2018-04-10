package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GetResponse {

    private Path directory;

    public GetResponse(Path directory) {
        this.directory = directory;
    }

    public Response getResponse(Request request) throws IOException {
        String requestURI = request.getRequestURI();
        String extension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
        Map<String, String> responseHeaders = new HashMap<>();
        int statusCode;
        byte[] body = null;
        Path requestedFilePathInDir;
        if (requestURI.equals("/")) {
            requestedFilePathInDir = defaultRequestURIHandler();
        } else {
            requestedFilePathInDir = Paths.get(directory.toString() + requestURI);
        }
        if (Files.exists(requestedFilePathInDir)) {
            statusCode = 200;
            if (Files.isDirectory(requestedFilePathInDir)) {
                if (Paths.get(directory.toString(), "index.html").toFile().exists()) {
                    requestedFilePathInDir = Paths.get(requestedFilePathInDir.toString(), "index.html");
                } else {
                    body = DirectoryBrowserGenerator.generate(requestedFilePathInDir.toFile(), directory.toFile());
                }
            }
            if (body == null) {
                body = Files.readAllBytes(requestedFilePathInDir);
            }
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            if (extension.equals("txt")) {
                responseHeaders.put("Content-Type", "text/plain");
            } else if (extension.equals("html")) {
                responseHeaders.put("Content-Type", "text/html");
            }
        } else {
            statusCode = 404;
            responseHeaders.put("Content-Type", "text/plain");
            body = "HTTP/1.1 404 Not Found".getBytes();
        }
        return new Response(statusCode, responseHeaders, body);
    }

    private Path defaultRequestURIHandler() {
        Path requestedFilePathInDir;
        if (Paths.get(directory.toString(), "index.html").toFile().exists()) {
            requestedFilePathInDir = Paths.get(directory.toString(), "index.html");
        } else {
            requestedFilePathInDir = directory;
        }
        return requestedFilePathInDir;
    }
}
