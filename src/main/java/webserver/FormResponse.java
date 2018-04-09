package webserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormResponse {

    public Response formResponse(Request request) throws UnsupportedEncodingException {
        int statusCode = 200;
        List<String> headers = new ArrayList<>();
        byte[] body;
        Map<String, String> map = request.bodyToForm();
        String response = "Data received";
        for (String sone : map.keySet()) {
            response += "\n" + sone + ": " + map.get(sone);
        }
        body = response.getBytes();
        System.out.println(response);
        return new Response(statusCode, headers, body);
    }
}
