package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StaticGetResponse implements RequestHandler {

    private Path directory;
    private Map<String, String> mimeTypes;
    
    @Mapping(URI = "/*")
    public Response handle(Request request) {
        Response response;
        if (!request.getRequestMethod().equals("GET")) {
            return new Response(405, Collections.emptyMap(), null);
        }
        try {
            if (Files.isRegularFile(Paths.get(directory.toString(), request.getRequestURI()))) {
                response = responseWithStaticFile(request);
            } else if (Files.isDirectory(Paths.get(directory.toString(), request.getRequestURI()))) {
                if (Files.exists(Paths.get(directory.toString(), request.getRequestURI(), "index.html"))) {
                    response = responseWithDefaultPage(request);
                } else {
                    response = responseWithFileTree(request);
                }
            } else {
                int statusCode = 404;
                Map<String, String> responseHeaders = new HashMap<>();
                responseHeaders.put("Content-Type", "text/html");
                byte[] body = WebServerUtil.readFileFromClasspath("requiredfiles","404page.html");
                response = new Response(statusCode, responseHeaders, body);
            }
            return response;
        } catch (Exception e) {
            return new Response(500, Collections.emptyMap(), null);
        }
    }

    private Response responseWithStaticFile(Request request) throws IOException {
        int statusCode = 200;
        if (!request.getRequestMethod().equals("GET")) {
            statusCode = 405;
        }
        String requestURI = request.getRequestURI();
        String fileExtension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
        Map<String, String> responseHeaders = new HashMap<>();
        Path requestedFilePathInDir = Paths.get(directory.toString() + requestURI);
        byte[] body = Files.readAllBytes(requestedFilePathInDir);
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        if (mimeTypes.get(fileExtension) != null) {
            responseHeaders.put("Content-Type", mimeTypes.get(fileExtension));
        }
        return new Response(statusCode, responseHeaders, body);
    }

    private Response responseWithFileTree(Request request) throws IOException {
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        Path requestedFilePathInDir = Paths.get(directory.toString(), request.getRequestURI());
        byte[] body = DirectoryBrowserGenerator.generate(requestedFilePathInDir.toFile(), directory.toFile());
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        return new Response(statusCode, responseHeaders, body);
    }

    private Response responseWithDefaultPage(Request request) throws IOException {
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = Files.readAllBytes(Paths.get(directory.toString(), request.getRequestURI(), "index.html"));
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        responseHeaders.put("Content-Type", "text/html");
        return new Response(statusCode, responseHeaders, body);
    }

    @Override
    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
        this.mimeTypes = sc.getMimeTypes();
    }
}
