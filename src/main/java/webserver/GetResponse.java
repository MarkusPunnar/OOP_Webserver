package webserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GetResponse {

    private Path directory;

    public GetResponse(Path directory) {
        this.directory = directory;
    }

    public Response getResponse(Request request) throws IOException {
        String fileName = request.getRequestURI();
        int statusCode;
        List<String> headers = new ArrayList<>();
        byte[] body = null;
        Path filePath;
        if (fileName.equals("\\")) {
            if (new File(directory.toString() + File.separatorChar + "index.html").exists()) {
                filePath = Paths.get(directory.toString(), "index.html");
                fileName = filePath.toString();
            } else {
                filePath = directory;
                fileName = filePath.toString();
            }
        } else {
            filePath = Paths.get(directory.toString() + fileName);
            fileName = filePath.toString();
        }
        if (Files.exists(filePath)) {
            statusCode = 200;
            if (Files.isDirectory(filePath)) {
                if (new File(filePath.toString() + File.separatorChar + "index.html").exists()) {
                    filePath = Paths.get(filePath.toString(), "index.html");
                    fileName = filePath.toString();
                } else {
                    body = DirectoryBrowserGenerator.generate(filePath.toFile(), directory.toFile());
                }
            }
            if (body == null) {
                String[] resource = fileName.split("\\\\");
                String resourcePath = resource[resource.length - 2] + "\\" + resource[resource.length - 1];
                try (InputStream is = GetResponse.class.getClassLoader().getResourceAsStream(resourcePath)) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    body = buffer.toByteArray();
                }
            }
            headers.add("Content-Length: " + body.length + "\r\n");
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extension.equals("txt")) {
                headers.add("Content-Type: text/plain\r\n");
            } else if (extension.equals("html")) {
                headers.add("Content-Type: text/html\r\n");
            }
        } else {
            statusCode = 404;
            body = "HTTP/1.1 404 Not Found".getBytes();
        }
        return new Response(statusCode, headers, body);
    }
}
