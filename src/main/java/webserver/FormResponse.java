package webserver;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FormResponse {

    public Response handle(Request request) throws UnsupportedEncodingException {
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> map = request.bodyToForm();
        if (map == null) {
            return new Response(400, null, null);
        }
        String response = "Data received";
        for (String sone : map.keySet()) {
            response += "\n" + sone + ": " + map.get(sone);
        }
        body = response.getBytes();
        System.out.println(response);
        return new Response(statusCode, responseHeaders, body);
    }
}