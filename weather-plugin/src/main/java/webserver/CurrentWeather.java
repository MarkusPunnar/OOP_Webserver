package webserver;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrentWeather implements RequestHandler {

    @Mapping(URI = "/weather")
    public Response handle(Request request) {
        try {
            Map<String, String> responseHeaders = new HashMap<>();
            double temperature = Math.round(Math.random() * 20);
            byte[] body = String.valueOf(temperature).getBytes("UTF-8");
            responseHeaders.put("Content-Length", String.valueOf(body.length));
            responseHeaders.put("Content-Type", "text/plain");
            return new Response(StatusCode.OK, responseHeaders, body);
        } catch (UnsupportedEncodingException e) {
            return new Response(StatusCode.INTERNAL_ERROR, Collections.emptyMap(), null);
        }
    }

    @Override
    public void initialize(ServerConfig sc) {
    }

    public void getPluginName() {
        System.out.println("Current Weather Plugin");
    }
}
