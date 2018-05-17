package webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginFilter implements Filter {

    private List<String> loggedUsers = new ArrayList<>();

    public Response doFilter(Request request, FilterChain chain) throws Exception {
        Response response;
        Map<String, String> attributes = request.getRequestAttributes();
        if (!request.getRequestURI().startsWith("/weather")) {
            response = chain.filter(request);
            if (attributes.containsKey("loginToken")) {
                loggedUsers.add(attributes.get("loginToken"));
                attributes.remove("loginToken");
            }
        } else {
            Map<String, String> responseHeaders = new HashMap<>();
            if (request.getHeaders().containsKey("Cookie") && loggedUsers.contains(request.getHeaders().get("Cookie").get(0).split("=")[1])) {
                return chain.filter(request);
            }
            responseHeaders.put("Location", "/loginform.html");
            return new Response(StatusCode.FOUND, responseHeaders, null);
        }
        return response;
    }
}
