package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthenticationFilter implements Filter {

    private final Map<String, String> registeredUsers;
    private final Map<String, String> authorizedUsers;

    public BasicAuthenticationFilter() throws IOException {
        this.registeredUsers = AuthenticationUtil.readRegisteredUsersToMap();
        this.authorizedUsers = new HashMap<>();
    }

    public Response doFilter(Request request, FilterChain chain) throws Exception {
        if (!request.getRequestURI().startsWith("/todoapp")) {
            return chain.filter(request);
        }
        Map<String, String> responseHeaders = new HashMap<>();
        if (request.getHeaders().containsKey("Authorization")) {
            String[] headerData = request.getHeaders().get("Authorization").get(0).split(" ");
            if (!headerData[0].equals("Basic")) {
                return new Response(StatusCode.FORBIDDEN, responseHeaders, null);
            }
            String loginDataInBase64 = headerData[1];
            byte[] loginDataByteArray = Base64.getDecoder().decode(loginDataInBase64);
            String loginData = new String(loginDataByteArray, StandardCharsets.UTF_8);
            String[] loginParts = loginData.split(":");
            String insertedUsername = loginParts[0];
            String insertedPassword = loginParts[1];
            if (checkAuthorizedAndRegisteredUsers(insertedUsername, insertedPassword)) {
                request.getRequestAttributes().put("authorized-user", insertedUsername);
                return chain.filter(request);
            }
        }
        responseHeaders.put("WWW-Authenticate", "Basic realm=\"Access to toDo App\", charset=\"UTF-8\"");
        return new Response(StatusCode.UNAUTHORIZED, responseHeaders, null);
    }

    private boolean checkAuthorizedAndRegisteredUsers(String insertedUsername, String insertedPassword) {
        synchronized (authorizedUsers) {
            if (authorizedUsers.containsKey(insertedUsername) && authorizedUsers.get(insertedUsername).equals(insertedPassword)) {
                return true;
            }
        }
        if (registeredUsers.containsKey(insertedUsername) && BCrypt.checkpw(insertedPassword, registeredUsers.get(insertedUsername))) {
            synchronized (authorizedUsers) {
                authorizedUsers.put(insertedUsername, insertedPassword);
                return true;
            }
        }
        return false;
    }
}
