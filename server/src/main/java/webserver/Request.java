package webserver;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {

    private Map<String, List<String>> headers;
    private byte[] body;
    private String requestMethod;
    private String requestURI;
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> requestAttributes = new HashMap<>();

    public Request(String requestMethod, String requestURI, Map<String, List<String>> headers, byte[] body) throws UnsupportedEncodingException {
        int parameterStart = requestURI.indexOf("?");
        if (parameterStart == -1) {
            this.requestURI = URLDecoder.decode(requestURI, "UTF-8");
        } else {
            this.requestURI = URLDecoder.decode(requestURI.substring(0, parameterStart), "UTF-8");
            String[] parameterArray = requestURI.substring(parameterStart + 1).split("&");
            for (String s : parameterArray) {
                String[] parameterValues = s.split("=");
                for (int i = 0; i < parameterValues.length; i++) {
                    parameterValues[i] = URLDecoder.decode(parameterValues[i], "UTF-8");
                }
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

    public Map<String, Part> multipartBodyToForm() throws UnsupportedEncodingException {
        Map<String, Part> map = new HashMap<>();
        byte[] lineBreakD = "\r\n\r\n".getBytes();

        if (headers.get("Content-Type") == null)
            throw new RuntimeException("Content-Type header not found");

        String boundaryX = headers.get("Content-Type").get(0).split(";")[1].replace("boundary=", "");
        byte[] boundary = boundaryX.substring(1, boundaryX.length() - 1).getBytes("UTF-8");
        List<byte[]> parts = split(body, boundary);

        for (byte[] part : parts) {
            String[] info = split(part, lineBreakD).get(0).toString().split("\r\n");
            byte[] partValue = Arrays.copyOfRange(part, indexOf(part, lineBreakD,0) + lineBreakD.length, part.length);
            String contentDispositionInfo = info[0].split(":")[1];
            String partNameValue = contentDispositionInfo.split(";")[1];
            String partName = partNameValue.substring(partNameValue.indexOf(":"), partNameValue.length());
            map.put(partName, new Part(partName, partValue));
        }
        return map;
    }

    private int indexOf(byte[] array, byte[] string, int start) {
        for (int i = start; i < array.length - string.length + 1; i++) {
            if (Arrays.equals(Arrays.copyOfRange(array, i, i + string.length), string)) {
                return i;
            }
        }
        return -1;
    }

    private List<byte[]> split(byte[] body, byte[] splitter) {
        List<byte[]> array = new ArrayList<>();
        int start = 0;
        int a = indexOf(body, splitter, start);
        while (a != -1) {
            if (a != 0)
                array.add(Arrays.copyOfRange(body, start, a));
            start = a + splitter.length + 1;
            a = indexOf(body, splitter, start);
        }
        return array;
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

    public Map<String, List<String>> getHeaders() {
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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getRequestAttributes() {
        return requestAttributes;
    }
}

