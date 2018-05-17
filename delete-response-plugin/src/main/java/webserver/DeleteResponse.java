package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DeleteResponse implements RequestHandler {

    private Path directory;

    @Mapping(URI = "/delete/*", method = "GET")
    public Response handle(Request request) throws IOException {
        int statusCode = 0;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = "File deleted!".getBytes();
        System.out.println(request.getRequestURI());
        String fileToDelete = request.getRequestURI().substring(8);
        Path filePath = Paths.get(directory.toString(), fileToDelete);
        if (request.getRequestURI().equals("/")) {
            statusCode = 400;
        } else {
            if (Files.exists(filePath)) {
                if (Files.isDirectory(filePath)) {
                    RecursiveDeleter.deleteDirectory(filePath);
                    statusCode = 302;
                } else if (Files.isRegularFile(filePath)) {
                        statusCode = 302;
                        Files.delete(filePath);
                }
            }
            if (statusCode == 0) {
                statusCode = 400;
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
