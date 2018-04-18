package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GetResponseWithDefaultPage {
    private Path directory;

    public GetResponseWithDefaultPage(Path directory) {
        this.directory = directory;
    }

    public Response handle(Request request) throws IOException {
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = Files.readAllBytes(Paths.get(directory.toString(), request.getRequestURI(), "index.html"));
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        responseHeaders.put("Content-Type", "text/html");
        return new Response(statusCode, responseHeaders, body);
    }
}
