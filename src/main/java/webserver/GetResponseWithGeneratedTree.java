package webserver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GetResponseWithGeneratedTree {

    private Path directory;

    public GetResponseWithGeneratedTree(Path directory) {
        this.directory = directory;
    }

    public Response handle(Request request) throws IOException {
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        Path requestedFilePathInDir = Paths.get(directory.toString(), request.getRequestURI());
        byte[] body = DirectoryBrowserGenerator.generate(requestedFilePathInDir.toFile(), directory.toFile());
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        return new Response(statusCode, responseHeaders, body);
    }
}
