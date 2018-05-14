package webserver;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FormResponse implements RequestHandler {

    @Mapping(URI = "/form/test", method = "POST")
    public Response handle(Request request) throws UnsupportedEncodingException {
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(400, Collections.emptyMap(), null);
        }
        String response = "Data received";
        for (String dataPart : dataMap.keySet()) {
            response += "\n" + dataPart + ": " + dataMap.get(dataPart);
        }
        body = response.getBytes();
        System.out.println(response);
        return new Response(statusCode, responseHeaders, body);
    }

    /*public Response handleMultipart(Request request){
        int statusCode = 200;
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> dataMap = request.;
        if (dataMap == null) {
            return new Response(400, Collections.emptyMap(), null);
        }
        String response = "Data received";
        for (String dataPart : dataMap.keySet()) {
            response += "\n" + dataPart + ": " + dataMap.get(dataPart);
        }
        body = response.getBytes();
        System.out.println(response);
        return new Response(statusCode, responseHeaders, body);
    }*/

    @Override
    public void initialize(ServerConfig sc) {

    }
}