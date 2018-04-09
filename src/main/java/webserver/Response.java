package webserver;

import java.util.List;

public class Response {

    private int statusCode;
    private List<String> headers;
    private byte[] body;

    public Response(int statusCode, List<String> headers, byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
