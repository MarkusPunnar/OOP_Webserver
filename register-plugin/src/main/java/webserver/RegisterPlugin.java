package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RegisterPlugin implements RequestHandler {

    private Path directory;
    private Set<String> userNames = new HashSet<>();

    public Response handle(Request request) throws Exception {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(400, Collections.emptyMap(), null);
        }
        String userName = dataMap.get("username");
        if (userNames.contains(userName)) {
            body = "Username already exists".getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(400, responseHeaders, body);
        }
        userNames.add(userName);
        String pw = dataMap.get("password");
        String confirmPw = dataMap.get("confirm_password");
        if (!pw.equals(confirmPw)) {
            body = "Passwords don't match".getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(400, responseHeaders, body);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(directory.toString(), "passwords.txt").toString()))) {
            String hashedPw = BCrypt.hashpw(pw, BCrypt.gensalt());
            writer.write(userName + ": " + hashedPw);
        }
        return new Response(200, Collections.emptyMap(), null);
    }

    public void register(Map<String, RequestHandler> patterns, ServerConfig sc) {
        RegisterPlugin register = new RegisterPlugin();
        register.initialize(sc);
        patterns.put("/register", register);
    }

    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }
}
