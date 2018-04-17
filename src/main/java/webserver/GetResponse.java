package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GetResponse {

    private Path directory;
    private Map<String, String> mimeTypes;

    public GetResponse(Path directory, Map<String, String> mimeTypes) {
        this.directory = directory;
        this.mimeTypes = mimeTypes;
    }

    public Response getResponse(Request request) throws IOException {
        String requestURI = request.getRequestURI();
        String fileExtension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
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
                if (Files.exists(Paths.get(directory.toString(), "index.html"))) {
                    requestedFilePathInDir = Paths.get(requestedFilePathInDir.toString(), "index.html");
                } else {
                    body = DirectoryBrowserGenerator.generate(requestedFilePathInDir.toFile(), directory.toFile());
                }
            }
            if (body == null) {
                body = Files.readAllBytes(requestedFilePathInDir);
            }
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            if (mimeTypes.get(fileExtension) != null) {
                responseHeaders.put("Content-Length", mimeTypes.get(fileExtension));
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
        if (Files.exists(Paths.get(directory.toString(), "index.html"))) {
            requestedFilePathInDir = Paths.get(directory.toString(), "index.html");
        } else {
            requestedFilePathInDir = directory;
        }
        return requestedFilePathInDir;
    }
}
