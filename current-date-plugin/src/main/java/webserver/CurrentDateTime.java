package webserver;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrentDateTime implements RequestHandler {
    
    public Response handle(Request request) {
        try {
            Map<String, String> responseHeaders = new HashMap<>();
            Instant currentLocalTime = Instant.now();
            byte[] body = currentLocalTime.toString().getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(200, responseHeaders, body);
        } catch (UnsupportedEncodingException e) {
            return new Response(400, Collections.emptyMap(), null);
        }
    }

    public void register(Map<String, RequestHandler> patterns, ServerConfig sc) {
        patterns.put("/date/now", new CurrentDateTime());
    }

    @Override
    public void initialize(ServerConfig sc) {

    }
}
