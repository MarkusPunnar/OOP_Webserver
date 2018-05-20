package webserver;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthenticationFilter implements Filter {

    public Response doFilter(Request request, FilterChain chain) throws Exception {
        Response response;

        if (!request.getRequestURI().startsWith("/todoapp")) {
            response = chain.filter(request);
        } else {
            Map<String, String> responseHeaders = new HashMap<>();
            if (request.getHeaders().containsKey("Authorization")) {
                String loginDataInBase64 = request.getHeaders().get("Authorization").get(0).split(" ")[1];
                byte[] loginDataByteArray = Base64.getDecoder().decode(loginDataInBase64);
                String loginData = new String(loginDataByteArray, StandardCharsets.UTF_8);
                String[] loginParts = loginData.split(":");
                byte[] allRegisteredUsersAsByteArray = WebServerUtil.readFileFromClasspath("passwords.txt");
                if (allRegisteredUsersAsByteArray != null) {
                    Map<String, String> userInfo = new HashMap<>();
                    String allRegisteredUsers = new String(allRegisteredUsersAsByteArray, StandardCharsets.UTF_8);
                    String[] users = allRegisteredUsers.split("\r\n");
                    for (String user: users) {
                        String[] userDataParts = user.split(": ");
                        userInfo.put(userDataParts[0], userDataParts[1]);
                    }
                    if (userInfo.containsKey(loginParts[0]) && BCrypt.checkpw(loginParts[1], userInfo.get(loginParts[0]))) {
                        return chain.filter(request);
                    }
                }
            }
            responseHeaders.put("WWW-Authenticate", "Basic realm=\"Access to toDo App\", charset=\"UTF-8\"");
            return new Response(StatusCode.UNAUTHORIZED, responseHeaders, null);
        }
        return response;
    }
}
