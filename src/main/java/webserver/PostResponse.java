package webserver;

import javax.lang.model.util.Elements;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostResponse {

    public Response postResponse(String fileName, File directory, Request request) throws IOException {
        String statusLine;
        List<String> headers = new ArrayList<>();
        byte[] body = null;
        Path filePath = Paths.get(directory.toString() + fileName);
        System.out.println(filePath.toString());
        if (fileName.equals("\\")) {
            statusLine = "HTTP/1.1 400 Bad Request\r\n";
        } else if (fileName.equals("form.html")) {
            File input = new File(Paths.get("src", "main", "resources", "defaultwebsite", "form.html").toString());
            /*Document doc = Jsoup.parse(input, "UTF-8", "http://localhost:1337/defweb/form.html");
            HashMap<String, String> map = new HashMap<String, String>();
            String nimi = doc.getElementById("nimi").toString;
            String message = doc.getElementById("message").toString;
            map.put(nimi, message);
*/
            statusLine = "HTTP/1.1 200 OK\r\n";

        } else {
            if (Files.exists(filePath)) {
                statusLine = "HTTP/1.1 200 OK\r\n";
            } else {
                statusLine = "HTTP/1.1 201 Created\r\n";
            }
            try (FileOutputStream fos = new FileOutputStream(filePath.toString())) {
                fos.write(request.getBody());
            }
        }
        return new Response(statusLine, headers, body);
    }
}
