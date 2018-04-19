package webserver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PostResponse implements RequestHandler {

    private Path directory;

    public PostResponse(Path directory) {
        this.directory = directory;
    }

    public Response handle(Request request) throws IOException {
        if (!request.getRequestMethod().equals("POST")) {
            return new Response(405, Collections.emptyMap(), null);
        }
        int statusCode;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = null;
        String uploadedFileName = request.getRequestURI().substring(8);
        Path filePath = Paths.get(directory.toString(), uploadedFileName);
        System.out.println(filePath.toString());
        if (Files.exists(filePath) || request.getRequestURI().equals("/")) {
            statusCode = 400;
        } else {
            statusCode = 201;
        }
        try (FileOutputStream fos = new FileOutputStream(filePath.toString())) {
            fos.write(request.getBody());
        }
        return new Response(statusCode, responseHeaders, body);
    }

    public void register(Map<String, RequestHandler> patterns) {
        patterns.put("/upload/*", new PostResponse(directory));
    }
}
