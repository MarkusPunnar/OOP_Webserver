package webserver;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrentWeather implements RequestHandler {


    public Response handle(Request request) {
        try {
            Map<String, String> responseHeaders = new HashMap<>();
            double temperature = Math.round(Math.random() * 20);
            byte[] body = String.valueOf(temperature).getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(200, responseHeaders, body);
        } catch (UnsupportedEncodingException e) {
            return new Response(500, Collections.emptyMap(), null);
        }
    }

    public void register(Map<String, RequestHandler> patterns) {
        patterns.put("/weather", new CurrentWeather());
    }
}
