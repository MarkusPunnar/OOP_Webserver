package webserver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PostResponse {

    private Path directory;

    public PostResponse(Path directory) {
        this.directory = directory;
    }

    public Response postResponse(Request request) throws IOException {
        int statusCode;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = null;
        Path filePath = Paths.get(directory.toString() + request.getRequestURI());
        System.out.println(filePath.toString());
        if (request.getRequestURI().equals("\\")) {
            statusCode = 400;
        } else {
            if (Files.exists(filePath)) {
                statusCode = 200;
            } else {
                statusCode = 201;
            }
            try (FileOutputStream fos = new FileOutputStream(filePath.toString())) {
                fos.write(request.getBody());
            }
        }
        return new Response(statusCode, responseHeaders, body);
    }
}
