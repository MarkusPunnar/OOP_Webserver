package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class RegisterPlugin implements RequestHandler {

    private Path directory;
    private Set<String> userNames = new HashSet<>();

    public Response handle(Request request) throws Exception {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Path writeToFilePath = Paths.get(directory.toString(), "passwords.txt");
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(400, Collections.emptyMap(), null);
        }
        String userName = dataMap.get("username");
        try (Scanner sc = new Scanner(writeToFilePath.toFile(), "UTF-8")) {
            while (sc.hasNextLine()) {
                String user = sc.nextLine();
                userNames.add(user.split(": ")[0]);
            }
        }
        if (userNames.contains(userName)) {
            body = "Username already exists".getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(400, responseHeaders, body);
        }
        String pw = dataMap.get("password");
        String confirmPw = dataMap.get("confirm_password");
        if (!pw.equals(confirmPw)) {
            body = "Passwords don't match".getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(400, responseHeaders, body);
        }
        String hashedPw = BCrypt.hashpw(pw, BCrypt.gensalt());
        try (BufferedWriter writer = Files.newBufferedWriter(writeToFilePath, StandardOpenOption.APPEND)) {
            writer.write(userName + ": " + hashedPw);
            writer.newLine();
        }
        return new Response(200, Collections.emptyMap(), null);
    }

    public void register(Map<String, RequestHandler> patterns) {
        patterns.put("/register", this);
    }

    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }
}
