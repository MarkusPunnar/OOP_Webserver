package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginHandler implements RequestHandler {

    private Map<String, String> currentUsers;

    @Mapping(URI = "/todoapp/login", method = "POST")
    public Response handle(Request request) throws Exception {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }
        String insertedUsername = dataMap.get("username");
        String insertedPassword = dataMap.get("password");
        if (currentUsers.containsKey(insertedUsername) && BCrypt.checkpw(insertedPassword, currentUsers.get(insertedUsername))) {
            String loginToken = BCrypt.gensalt(30);
            request.getAttributes().put("loginToken", loginToken);
            request.getAttributes().put("user", insertedUsername);
            body = "Login successful".getBytes(StandardCharsets.UTF_8);
            responseHeaders.put("Location", "/todoapp/form");
            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Set-Cookie", "login=" + loginToken + "; Path=/");
            return new Response(StatusCode.FOUND, responseHeaders, body);
        } else {
            body = "Invalid username or password".getBytes(StandardCharsets.UTF_8);
            responseHeaders.put("Content-Type", "text/plain");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            return new Response(StatusCode.BAD_REQUEST, responseHeaders, body);
        }
    }

    private Map<String,String> readHashedPasswordsToMap() throws IOException {
        Map<String, String> currentUsers = new HashMap<>();
        byte[] registeredUsersAsByteArray = WebServerUtil.readFileFromClasspath("passwords.txt");
        if (registeredUsersAsByteArray == null) {
            throw new RuntimeException("Can't access authentication password file");
        }
        String registeredUsersAsString = new String(registeredUsersAsByteArray, StandardCharsets.UTF_8);
        registeredUsersAsString = registeredUsersAsString.replace("\r\n", "\n");
        String[] userInfo = registeredUsersAsString.split("\n");
        for (String user: userInfo) {
            String[] userParts = user.split(": ");
            currentUsers.put(userParts[0], userParts[1]);
        }
        return currentUsers;
    }

    public void initialize(ServerConfig sc) {
        try {
            this.currentUsers = readHashedPasswordsToMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
