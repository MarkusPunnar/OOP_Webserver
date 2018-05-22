package webserver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginFilter implements Filter {

    private Map<String, String> loggedUsers = new HashMap<>();

    public Response doFilter(Request request, FilterChain chain) throws Exception {
        Response response;
        Map<String, String> attributes = request.getRequestAttributes();
        if (!request.getRequestURI().startsWith("/todoapp")) {
            response = chain.filter(request);
            if (attributes.containsKey("loginToken")) {
                loggedUsers.put(attributes.get("loginToken"), attributes.get("user"));
                attributes.remove("loginToken");
            }
        } else {
            Map<String, String> responseHeaders = new HashMap<>();
            if (request.getRequestURI().equals("/todoapp/logout")) {
                loggedUsers.remove(request.getHeaders().get("Cookie").get(0).split("=")[1]);
                byte[] body = "Logout successful".getBytes(StandardCharsets.UTF_8);
                responseHeaders.put("Content-Type", "text/plain");
                responseHeaders.put("Content-Length", String.valueOf(body.length));
                return new Response(StatusCode.OK, responseHeaders, body);
            }
            if (request.getHeaders().containsKey("Cookie") && loggedUsers.containsKey(request.getHeaders().get("Cookie").get(0).split("=")[1])) {
                attributes.put("authorized-user", loggedUsers.get(request.getHeaders().get("Cookie").get(0).split("=")[1]));
                return chain.filter(request);
            }
            responseHeaders.put("Location", "/loginform.html");
            return new Response(StatusCode.FOUND, responseHeaders, null);
        }
        return response;
    }
}
