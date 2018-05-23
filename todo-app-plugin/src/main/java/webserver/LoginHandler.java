package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements RequestHandler {

    private Map<String, String> currentRegisteredUsers;

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
        if (currentRegisteredUsers.containsKey(insertedUsername) && BCrypt.checkpw(insertedPassword, currentRegisteredUsers.get(insertedUsername))) {
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

    public void initialize(ServerConfig sc) throws Exception {
        if (System.getProperty("todo.passwords") == null) {
            System.out.println("Password file not configured. Only default user accessible.");
            System.out.println("Username: admin");
            System.out.println("Password: admin");
            System.out.println("To configure passwords file, set System property \"todo.passwords=filePath\"");
            this.currentRegisteredUsers = Map.of("admin", BCrypt.hashpw("admin", BCrypt.gensalt()));
        } else {
            this.currentRegisteredUsers = AuthenticationUtil.readHashedPasswordsToMap(Paths.get(System.getProperty("todo.passwords")));
        }
        AuthUserInfo info = new AuthUserInfo(currentRegisteredUsers);
        sc.getAttributes().put("user", info);
    }
}
