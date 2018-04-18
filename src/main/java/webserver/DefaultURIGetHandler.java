package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DefaultURIGetHandler {

    private Path directory;
    private Map<String, String> mimeTypes;

    public DefaultURIGetHandler(Path directory, Map<String, String> mimeTypes) {
        this.directory = directory;
        this.mimeTypes = mimeTypes;
    }

    public Response handle(Request request) throws IOException {
        String requestURI = request.getRequestURI();
        String fileExtension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
        Map<String, String> responseHeaders = new HashMap<>();
        int statusCode = 500;
        byte[] body;
        Path requestedFilePathInDir = requestURIHandler();
        if (!Files.exists(requestedFilePathInDir)) {
            statusCode = 404;
            responseHeaders.put("Content-Type", "text/html");
            body = ClasspathUtil.readFileFromClasspath("404page.html");
            return new Response(statusCode, responseHeaders, body);
        }
        if (checkRequestPath(requestedFilePathInDir)) {
            statusCode = 200;
            body = Files.readAllBytes(Paths.get(requestedFilePathInDir.toString(), "index.html"));
        } else {
            body = DirectoryBrowserGenerator.generate(requestedFilePathInDir.toFile(), directory.toFile());
        }
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        if (mimeTypes.get(fileExtension) != null) {
            responseHeaders.put("Content-Type", mimeTypes.get(fileExtension));
        }
        return new Response(statusCode, responseHeaders, body);
    }

    private Path requestURIHandler() {
        Path requestedFilePathInDir;
        if (Files.exists(Paths.get(directory.toString(), "index.html"))) {
            requestedFilePathInDir = Paths.get(directory.toString(), "index.html");
        } else {
            requestedFilePathInDir = directory;
        }
        return requestedFilePathInDir;
    }

    private boolean checkRequestPath(Path requestedPath) {
        if (Files.exists(requestedPath)) {
            return Files.isDirectory(requestedPath) && Files.exists(Paths.get(directory.toString(), "index.html"));
        }
        return false;
    }
}
