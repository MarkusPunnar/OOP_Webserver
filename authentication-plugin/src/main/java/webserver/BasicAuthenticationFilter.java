package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthenticationFilter implements Filter {

    private Map<String, String> registeredUsers = new HashMap<>();
    private Map<String, String> authorizedUsers = new HashMap<>();

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
            readRegisteredUsersToMap();
            if (authorizedUsers.containsKey(loginParts[0])) {
                return chain.filter(request);
            }
            if (registeredUsers.containsKey(loginParts[0]) && BCrypt.checkpw(loginParts[1], registeredUsers.get(loginParts[0]))) {
                authorizedUsers.put(loginParts[0], loginParts[1]);
                return chain.filter(request);
            }
        }
        responseHeaders.put("WWW-Authenticate", "Basic realm=\"Access to toDo App\", charset=\"UTF-8\"");
        return new Response(StatusCode.UNAUTHORIZED, responseHeaders, null);
    }

    private void readRegisteredUsersToMap() throws IOException {
        byte[] allRegisteredUsersAsByteArray = WebServerUtil.readFileFromClasspath("passwords.txt");
        if (allRegisteredUsersAsByteArray != null) {
            String allRegisteredUsers = new String(allRegisteredUsersAsByteArray, StandardCharsets.UTF_8);
            allRegisteredUsers = allRegisteredUsers.replaceAll("\r\n", "\n");
            String[] users = allRegisteredUsers.split("\n");
            for (String user : users) {
                String[] userDataParts = user.split(": ");
                registeredUsers.put(userDataParts[0], userDataParts[1]);
            }
        }
    }
}
