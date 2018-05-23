package webserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            return new Response(StatusCode.NOT_ALLOWED, Collections.emptyMap(), null);
        }
        try {
            response = responseWithPluginFile(request);
            if (request.getRequestURI().equals("/") || response == null) {
                if (Files.isRegularFile(Paths.get(directory.toString(), request.getRequestURI()))) {
                    response = responseWithStaticFile(request);
                } else if (Files.isDirectory(Paths.get(directory.toString(), request.getRequestURI()))) {
                    if (Files.exists(Paths.get(directory.toString(), request.getRequestURI(), "index.html"))) {
                        response = responseWithDefaultPage(request);
                    } else {
                        response = responseWithFileTree(request);
                    }
                } else {
                    Map<String, String> responseHeaders = new HashMap<>();
                    responseHeaders.put("Content-Type", "text/html");
                    byte[] body = WebServerUtil.readFileFromClasspathDirectory("requiredfiles", "404page.html");
                    response = new Response(StatusCode.NOT_FOUND, responseHeaders, body);
                }
            }
            return response;
        } catch (Exception e) {
            return new Response(StatusCode.INTERNAL_ERROR, Collections.emptyMap(), null);
        }
    }

    private Response responseWithStaticFile(Request request) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        if (!request.getRequestMethod().equals("GET")) {
            return new Response(StatusCode.NOT_ALLOWED, responseHeaders, null);
        }
        String requestURI = request.getRequestURI();
        String fileExtension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
        Path requestedFilePathInDir = Paths.get(directory.toString() + requestURI);
        byte[] body = Files.readAllBytes(requestedFilePathInDir);
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        if (mimeTypes.get(fileExtension) != null) {
            responseHeaders.put("Content-Type", mimeTypes.get(fileExtension));
        }
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    private Response responseWithFileTree(Request request) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        Path requestedFilePathInDir = Paths.get(directory.toString(), request.getRequestURI());
        byte[] body = DirectoryBrowserGenerator.generate(requestedFilePathInDir.toFile(), directory.toFile());
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    private Response responseWithDefaultPage(Request request) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body = Files.readAllBytes(Paths.get(directory.toString(), request.getRequestURI(), "index.html"));
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        responseHeaders.put("Content-Type", "text/html");
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    private Response responseWithPluginFile(Request request) throws IOException {
        Map<String, String> responseHeaders = new HashMap<>();
        if (System.getProperty("todo.passwords") == null && request.getRequestURI().equals("/todoapp/registerform.html")) {
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(StatusCode.BAD_REQUEST, responseHeaders, "Registration not allowed".getBytes(StandardCharsets.UTF_8));
        }
        byte[] body = WebServerUtil.readFileFromClasspathDirectory("requiredfiles", request.getRequestURI().substring(1));
        if (body == null) {
            return null;
        }
        String requestURI = request.getRequestURI();
        String fileExtension = requestURI.substring(requestURI.lastIndexOf(".") + 1);
        if (mimeTypes.get(fileExtension) != null) {
            responseHeaders.put("Content-Type", mimeTypes.get(fileExtension));
        }
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Override
    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
        this.mimeTypes = sc.getMimeTypes();
    }
}
