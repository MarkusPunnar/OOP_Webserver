package webserver;

import java.io.UnsupportedEncodingException;

public class Part {
    private String name;
    private byte[] body;

    public Part(String name, byte[] body) {
        this.name = name;
        this.body = body;
    }

    public String bodyAsString() throws UnsupportedEncodingException {
        return new String(body, "UTF-8");
    }

    public byte[] getBody() {
        return body;
    }
}
