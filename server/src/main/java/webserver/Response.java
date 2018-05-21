package webserver;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class Response {

    private StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body;

    public Response(StatusCode statusCode, Map<String, String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        if(!headers.containsKey("Content-Type"))
            headers.put("Content-Type", "text/plain");
        this.body = body;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
