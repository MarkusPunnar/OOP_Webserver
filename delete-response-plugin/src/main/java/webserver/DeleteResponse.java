package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DeleteResponse implements RequestHandler {

    private Path directory;

    @Mapping(URI = "/delete/*", method = "DELETE")
    public Response handle(Request request) throws IOException {
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

    @Override
    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }

}
