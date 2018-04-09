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
        String sdata = new String(this.body, StandardCharsets.UTF_8);
        System.out.println(sdata);
        String[] adata = sdata.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String sone : adata) {
            String[] jupid = sone.split("=");
            map.put(URLDecoder.decode(jupid[0], "UTF-8"), URLDecoder.decode(jupid[1], "UTF-8"));
        }
        return map;
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

