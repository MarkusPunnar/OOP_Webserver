package webserver;

import java.util.List;

public class Response {

    private String statusLine;
    private List<String> headers;
    private byte[] body;

    public Response(String statusLine, List<String> headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
