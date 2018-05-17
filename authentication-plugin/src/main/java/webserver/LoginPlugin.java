package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginPlugin implements RequestHandler {

    private Path directory;

    @Mapping(URI = "/login", method = "POST")
    public Response handle(Request request) throws Exception {
        Map<String, String> responseHeaders = new HashMap<>();
        Map<String, String> currentUsers = new HashMap<>();
        byte[] body;
        Path readUsersFromFilePath = Paths.get(directory.toString(), "passwords.txt");
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }
        try (Scanner sc = new Scanner(readUsersFromFilePath, "UTF-8")) {
            while (sc.hasNextLine()) {
                String user = sc.nextLine();
                String[] userInfo = user.split(": ");
                currentUsers.put(userInfo[0], userInfo[1]);
            }
        }
        String insertedUsername = dataMap.get("username");
        String insertedPassword = dataMap.get("password");
        if (currentUsers.containsKey(insertedUsername) && BCrypt.checkpw(insertedPassword, currentUsers.get(insertedUsername))) {
            String loginToken = BCrypt.gensalt(30);
            request.getRequestAttributes().put("loginToken", loginToken);
            body = "Login successful".getBytes(StandardCharsets.UTF_8);
            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Set-Cookie", "login=" + loginToken +"; Path=/");
            return new Response(StatusCode.OK, responseHeaders, body);
        }
        else {
            body = "Invalid username or password".getBytes(StandardCharsets.UTF_8);
            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            return new Response(StatusCode.BAD_REQUEST, responseHeaders, body);
        }
    }

    public void initialize(ServerConfig sc) {
        this.directory = sc.getDirectory();
    }
}
