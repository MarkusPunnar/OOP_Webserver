package webserver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormResponse {
    public Response formResponse(byte[] data) {
        String statusLine = "HTTP/1.1 200 OK\r\n";
        List<String> headers = new ArrayList<>();
        byte[] body;
        String sdata = new String(data, StandardCharsets.UTF_8);
        String[] adata = sdata.split("\r\n");

        HashMap<String,String> map = new HashMap<String, String>();
        map.put("nimi", adata[3]);
        map.put("email", adata[7]);
        map.put("message", adata[11]);

        String response = "Data received" + "\nnimi: " + map.get("nimi") + "\nemail: " + map.get("email") + "\nmessage: " + map.get("message");
        System.out.println(response);
        body = response.getBytes();
        return new Response(statusLine, headers, body);
    }
}
