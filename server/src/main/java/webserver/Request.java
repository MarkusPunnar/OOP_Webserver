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
    private HashMap<String, String> parameters = new HashMap<>();

    public Request(String requestMethod, String requestURI, Map<String, String> headers, byte[] body) throws UnsupportedEncodingException {
        requestURI = URLDecoder.decode(requestURI, "UTF-8");
        if (!requestURI.contains("?")) {
            this.requestURI = requestURI;
        } else {
            int parameterStart = requestURI.indexOf("?");
            this.requestURI = requestURI.substring(0,parameterStart);
            String[] parameterArray = requestURI.substring(parameterStart+1).split("&");
            for (String s : parameterArray) {
                String[] parameterValues = s.split("=");
                parameters.put(parameterValues[0], parameterValues[1]);
            }
        }
        this.requestMethod = requestMethod;
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
        for (char symbol : stringSymbols) {
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

    public HashMap<String, String> getParameters() {
        return parameters;
    }
}

