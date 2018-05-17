package webserver;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FormResponse implements RequestHandler {

    @Mapping(URI = "/form/test", method = "POST")
    public Response handle(Request request) throws UnsupportedEncodingException {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, String> dataMap = request.bodyToForm();
        if (dataMap == null) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }
        String response = "Data received";
        for (String dataPart : dataMap.keySet()) {
            response += "\n" + dataPart + ": " + dataMap.get(dataPart);
        }
        body = response.getBytes();
        System.out.println(response);
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Mapping(URI = "/form/multipart", method = "POST")
    public Response handleMultipart(Request request) throws UnsupportedEncodingException {
        Map<String, String> responseHeaders = new HashMap<>();
        byte[] body;
        Map<String, Part> dataMap = request.multipartBodyToForm();

        if (dataMap == null) {
            return new Response(StatusCode.BAD_REQUEST, Collections.emptyMap(), null);
        }

        String response = "Data received";

        for (String dataPart : dataMap.keySet()) {
            response += "\n" + dataPart + ": " + dataMap.get(dataPart).getBody().length;
        }

        body = response.getBytes("UTF-8");
        System.out.println(response);
        return new Response(StatusCode.OK, responseHeaders, body);
    }

    @Override
    public void initialize(ServerConfig sc) {

    }
}