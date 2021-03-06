package webserver;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrentDateTime implements RequestHandler {

    @Mapping(URI = "/date/now")
    public Response handle(Request request) {
        try {
            Map<String, String> responseHeaders = new HashMap<>();
            Instant currentLocalTime = Instant.now();
            byte[] body = currentLocalTime.toString().getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(StatusCode.OK, responseHeaders, body);
        } catch (UnsupportedEncodingException e) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }
    }

    @Override
    public void initialize(ServerConfig sc) {
    }
}
