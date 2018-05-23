package webserver;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoginFilter implements Filter {

    private final Map<String, String> loggedUsers = new ConcurrentHashMap<>();
    private final Set<String> publicResources = Set.of("/todoapp/register", "/todoapp/login", "/todoapp/loginform.html", "/todoapp/registerform.html");

    public Response doFilter(Request request, FilterChain chain) throws Exception {
        Response response;
        Map<String, String> attributes = request.getAttributes();
        String requestURI = request.getRequestURI();
        Map<String, String> responseHeaders = new HashMap<>();
        if (!requestURI.startsWith("/todoapp") || publicResources.contains(requestURI)) {
            if (System.getProperty("todo.passwords") == null && request.getRequestURI().equals("/todoapp/registerform.html")) {
                responseHeaders.put("Content-Type", "text/plain");
                return new Response(StatusCode.UNAVAILABLE, responseHeaders, "Registration not allowed".getBytes(StandardCharsets.UTF_8));
            }
            response = chain.filter(request);
            if (attributes.containsKey("loginToken")) {
                loggedUsers.put(attributes.get("loginToken"), attributes.get("user"));
                attributes.remove("loginToken");
            }
            return response;
        }
        if (request.getRequestURI().equals("/todoapp/logout")) {
            if (!request.getRequestMethod().equals("POST")) {
                return new Response(StatusCode.NOT_ALLOWED, responseHeaders, null);
            }
            loggedUsers.remove(request.getCookieValue("login"));
            responseHeaders.put("Location", "/todoapp/loginform.html");
            return new Response(StatusCode.FOUND, responseHeaders, null);
        }
        String cookieValue = loggedUsers.get(request.getCookieValue("login"));
        if (cookieValue != null) {
            attributes.put("authorized-user", cookieValue);
            return chain.filter(request);
        }
        responseHeaders.put("Location", "/todoapp/loginform.html");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }
}