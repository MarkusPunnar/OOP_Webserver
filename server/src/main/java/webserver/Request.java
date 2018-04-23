package webserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private Map<String, String> headers;
    private byte[] body;
    private String requestMethod;
    private String requestURI;


    public Request(String requestMethod, String requestURI, Map<String, String> headers, byte[] body) {
        this.requestMethod = requestMethod;
        this.requestURI = requestURI;
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> bodyToForm() throws UnsupportedEncodingException {
        String requestData = new String(body, StandardCharsets.UTF_8);
        String[] requestDataParts = requestData.split("&");
        if (requestDataParts.length == 1 && countChars(requestDataParts[0]) != 1) {
            return null;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        for (String string : requestDataParts) {
            String[] dataTypeAndValue = string.split("=");
            map.put(URLDecoder.decode(dataTypeAndValue[0], "UTF-8"), URLDecoder.decode(dataTypeAndValue[1], "UTF-8"));
        }
        return map;
    }

    private int countChars(String dataPart) {
        int count = 0;
        char[] stringSymbols = dataPart.toCharArray();
        for (char symbol: stringSymbols) {
            if (symbol == '=') {
                count++;
            }
        }
        return count;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

}
