package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class RegisterHandler implements RequestHandler {

    private AuthUsersRegistry registeredUserInfo;

    @Mapping(URI = "/todoapp/register", method = "POST")
    public Response handle(Request request) throws Exception {
        Set<String> userNames = new HashSet<>();
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }
        String userName = dataMap.get("username");
        Path writeToFilePath = Paths.get(System.getProperty("todo.passwords"));
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
        registeredUserInfo.add(userName, hashedPw);
        responseHeaders.put("Location", "/todoapp/loginform.html");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }

    public void initialize(ServerConfig sc) {
        if (sc.getAttributes().get("user") instanceof AuthUsersRegistry) {
            this.registeredUserInfo = (AuthUsersRegistry) sc.getAttributes().get("user");
        }
    }
}
