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
        StatusCode statusCode = null;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = "File deleted!".getBytes();
        String fileToDelete = request.getRequestURI().substring(8);
        Path filePath = Paths.get(directory.toString(), fileToDelete);
        if (request.getRequestURI().equals("/")) {
            statusCode = StatusCode.BAD_REQUEST;
        } else {
            if (Files.exists(filePath)) {
                if (Files.isDirectory(filePath)) {
                    RecursiveDeleter.deleteDirectory(filePath);
                    statusCode = StatusCode.OK;
                } else if (Files.isRegularFile(filePath)) {
                    statusCode = StatusCode.OK;
                    Files.delete(filePath);
                }
            }
            if (statusCode == null) {
                statusCode = StatusCode.NOT_FOUND;
            }
        }
        if (request.getParameters().containsKey("return")) {
            responseHeaders.put("Location", request.getParameters().get("return"));
        }
        return new Response(statusCode, responseHeaders, null);
    }

    @Override
    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }
}
