package webserver;

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

