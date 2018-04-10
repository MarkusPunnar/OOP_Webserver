package webserver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DeleteResponse {

    private Path directory;

    public DeleteResponse(Path directory) {
        this.directory = directory;
    }

    public Response deleteResponse(Request request) {
        System.out.println("Delete request received");
        int statusCode;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = null;
        Path filePath = Paths.get(directory.toString() + request.getRequestURI());
        System.out.println(filePath.toString());
        if (request.getRequestURI().equals("\\")) {
            statusCode = 400;
        } else {
            if (Files.exists(filePath)) {
                File deletable = new File(filePath.toString());
                if (deletable.isDirectory()) {
                    if (RecursiveDeleter.deleteDirectory(deletable)) {
                        statusCode = 200;
                    } else {
                        statusCode = 500;
                    }
                } else if (deletable.isFile()) {
                    if (deletable.delete()) {
                        statusCode = 200;
                    } else {
                        statusCode = 500;
                    }
                } else {
                    statusCode = 500;
                }
            } else {
                statusCode = 500;
            }
        }
        return new Response(statusCode, responseHeaders, body);
    }
}
