package webserver;

import java.util.*;

public class LoginFilter implements Filter {

    private final Map<String, String> loggedUsers = new HashMap<>();
    private final Set<String> publicResources = Set.of("todoapp/register", "/todoapp/login", "/todoapp/loginform.html", "/todoapp/registerform.html");

    public Response doFilter(Request request, FilterChain chain) throws Exception {
        Response response;
        Map<String, String> attributes = request.getAttributes();
        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/todoapp") || publicResources.contains(requestURI)) {
            response = chain.filter(request);
            synchronized (loggedUsers) {
                if (attributes.containsKey("loginToken")) {
                    loggedUsers.put(attributes.get("loginToken"), attributes.get("user"));
                    attributes.remove("loginToken");
                }
            }
            return response;
        }
        Map<String, String> responseHeaders = new HashMap<>();
        if (request.getRequestURI().equals("/todoapp/logout")) {
            if (!request.getRequestMethod().equals("POST")) {
                return new Response(StatusCode.NOT_ALLOWED, responseHeaders, null);
            }
            synchronized (loggedUsers) {
                loggedUsers.remove(request.getCookieValue("login"));
                responseHeaders.put("Location", "/todoapp/loginform.html");
                return new Response(StatusCode.FOUND, responseHeaders, null);
            }
        }
        if (request.getHeaders().containsKey("Cookie") && loggedUsers.containsKey(request.getCookieValue("login"))) {
            attributes.put("authorized-user", loggedUsers.get(request.getCookieValue("login")));
            return chain.filter(request);
        }
        responseHeaders.put("Location", "/todoapp/loginform.html");
        return new Response(StatusCode.FOUND, responseHeaders, null);
    }
}