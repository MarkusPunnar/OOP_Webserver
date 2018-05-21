package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class RegisterPlugin implements RequestHandler {

    private Path directory;
    private Set<String> userNames = new HashSet<>();

    @Mapping(URI = "/register", method = "POST")
    public Response handle(Request request) throws Exception {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Path writeToFilePath = Paths.get(directory.toString(), "passwords.txt");
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }
        String userName = dataMap.get("username");
        try (Scanner sc = new Scanner(writeToFilePath.toFile(), "UTF-8")) {
            while (sc.hasNextLine()) {
                String user = sc.nextLine();
                userNames.add(user.split(": ")[0]);
            }
        }
        if (userNames.contains(userName)) {
            body = "Username already exists".getBytes(StandardCharsets.UTF_8);
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(StatusCode.BAD_REQUEST, responseHeaders, body);
        }
        String pw = dataMap.get("password");
        String confirmPw = dataMap.get("confirm_password");
        if (!pw.equals(confirmPw)) {
            body = "Passwords don't match".getBytes(StandardCharsets.UTF_8);
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(StatusCode.BAD_REQUEST, responseHeaders, body);
        }
        String hashedPw = BCrypt.hashpw(pw, BCrypt.gensalt());
        try (BufferedWriter writer = Files.newBufferedWriter(writeToFilePath, StandardOpenOption.APPEND)) {
            writer.write(userName + ": " + hashedPw);
            writer.newLine();
        }
        return new Response(StatusCode.OK, Collections.emptyMap(), null);
    }

    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }
}
