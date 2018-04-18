package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GetResponseWithStaticFile {

    private Path directory;
    private Map<String, String> mimeTypes;

    public GetResponseWithStaticFile(Path directory, Map<String, String> mimeTypes) {
        this.directory = directory;
        this.mimeTypes = mimeTypes;
    }

    public Response handle(Request request) throws IOException {
        int statusCode = 200;
        if (!request.getRequestMethod().equals("GET")) {
            statusCode = 405;
        }
        String requestURI = request.getRequestURI();
        String fileExtension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
        Map<String, String> responseHeaders = new HashMap<>();
        Path requestedFilePathInDir = Paths.get(directory.toString() + requestURI);
        byte[] body = Files.readAllBytes(requestedFilePathInDir);
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        if (mimeTypes.get(fileExtension) != null) {
            responseHeaders.put("Content-Type", mimeTypes.get(fileExtension));
        }
        return new Response(statusCode, responseHeaders, body);
    }
}
