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

    @Mapping(URI = "/upload/*", method = "POST")
    public Response handle(Request request) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        String uploadedFileName = request.getRequestURI().substring(8);
        Path filePath = Paths.get(directory.toString(), uploadedFileName);
        if (Files.exists(filePath) || request.getRequestURI().equals("/")) {
            return new Response(StatusCode.BAD_REQUEST, responseHeaders, null);
        }
        try (FileOutputStream fos = new FileOutputStream(filePath.toString())) {
            fos.write(request.getBody());
        }
        return new Response(StatusCode.CREATED, responseHeaders, null);
    }

    @Override
    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }

    public void getPluginName() {
        System.out.println("Post Response Plugin");
    }

}
