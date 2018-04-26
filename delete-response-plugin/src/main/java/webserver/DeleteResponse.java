package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DeleteResponse implements RequestHandler {

    private Path directory;

    public Response handle(Request request) throws IOException {
        if (!request.getRequestMethod().equals("DELETE")) {
            return new Response(405, Collections.emptyMap(), null);
        }
        int statusCode = 0;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = null;
        String fileToDelete = request.getRequestURI().substring(8);
        Path filePath = Paths.get(directory.toString(), fileToDelete);
        if (request.getRequestURI().equals("/")) {
            statusCode = 400;
        } else {
            if (Files.exists(filePath)) {
                if (Files.isDirectory(filePath)) {
                    RecursiveDeleter.deleteDirectory(filePath);
                    statusCode = 200;
                } else if (Files.isRegularFile(filePath)) {
                        statusCode = 200;
                        Files.delete(filePath);
                }
            }
            if (statusCode == 0) {
                statusCode = 400;
            }
        }
        return new Response(statusCode, responseHeaders, body);
    }

    public void register(Map<String, RequestHandler> patterns, ServerConfig sc) {
        DeleteResponse dr = new DeleteResponse();
        dr.initialize(sc);
        patterns.put("/delete/*", dr);

    }

    @Override
    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }

}
