package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;


public class HandleRequestAndSendResponse implements Runnable {

    private Socket socket;
    private String directory;
    private Map<String, String> mimeTypes;
    private Map<String, RequestHandler> dynamicResponseURIs = new HashMap<>();
    private final byte[] finalBytes = "\r\n".getBytes(StandardCharsets.UTF_8);
    private final byte[] finalRequestBytes = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);

    public HandleRequestAndSendResponse(Socket socket, String directory, Map<String, String> mimeTypes) {
        this.socket = socket;
        this.directory = directory;
        this.mimeTypes = mimeTypes;
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    public void run() {
        Response response = null;
        //List<RequestHandler> dynamicResponseClasses = createDynamicResponseObjects();

        for (RequestHandler requestHandler : ServiceLoader.load(RequestHandler.class)) {
            requestHandler.register(dynamicResponseURIs);
        }

        /*
        for (RequestHandler responseClass : dynamicResponseClasses) {
            responseClass.register(dynamicResponseURIs);
        }
        */

        try {
            Request request = readRequest(socket);
            boolean foundProperClass = false;
            int matchesTried = 0;
            while (!foundProperClass && matchesTried != dynamicResponseURIs.keySet().size()) {
                for (String matchingRequestURI : dynamicResponseURIs.keySet()) {
                    foundProperClass = checkURIMatching(matchingRequestURI,request);
                    if (foundProperClass) {
                        response = dynamicResponseURIs.get(matchingRequestURI).handle(request);
                    }
                    matchesTried++;
                }
            }
            if (response == null) {
                response = new StaticGetResponse(Paths.get(directory), mimeTypes).handle(request);
            }
            try (BufferedOutputStream bof = new BufferedOutputStream(socket.getOutputStream())) {
                bof.write(constructStatusLine(response).getBytes("UTF-8"));
                for (String header : response.getHeaders().keySet()) {
                    bof.write((header + ": " + response.getHeaders().get(header) + "\r\n").getBytes("UTF-8"));
                }
                bof.write(finalBytes);
                if (response.getBody() != null) {
                    bof.write(response.getBody());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readRequestAsByteArray(BufferedInputStream bf) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        boolean finished = false;
        while (!finished) {
            int usefulBytes = 0;
            for (int i = 0; i < 1024; i++) {
                buf[i] = (byte) bf.read();
                usefulBytes++;
                if (i > 2 && Arrays.equals(Arrays.copyOfRange(buf, i - 3, i + 1), finalRequestBytes)) {
                    finished = true;
                    break;
                }
            }
            byteOut.write(buf, 0, usefulBytes);
        }
        return byteOut.toByteArray();
    }

    private Request readRequest(Socket socket) throws Exception {
        BufferedInputStream bf = new BufferedInputStream(socket.getInputStream());
        byte[] requestBody = null;
        byte[] requestLineAndHeaders = readRequestAsByteArray(bf);
        String requestLineAndHeadersAsString = new String(requestLineAndHeaders, 0, requestLineAndHeaders.length);
        List<String> requestLineComponents = parseRequestLine(requestLineAndHeadersAsString);
        Map<String, String> requestHeadersAsMap = readRequestHeadersToMap(requestLineAndHeadersAsString);
        if (requestHeadersAsMap.get("Content-Length") != null) {
            int requestBodyLength = Integer.parseInt(requestHeadersAsMap.get("Content-Length"));
            requestBody = readRequestBodyAsBytes(requestBodyLength, bf);
        }
        return new Request(requestLineComponents.get(0), requestLineComponents.get(1), requestHeadersAsMap, requestBody);
    }

    private List<String> parseRequestLine(String requestInfo) {
        List<String> requestInfoAsList = new ArrayList<>();
        String requestLine = requestInfo.split("\r\n")[0];
        String requestType = requestLine.substring(0, requestLine.indexOf(" "));
        String requestURI = requestLine.substring(requestLine.indexOf(" ") + 1, requestLine.lastIndexOf(" "));
        requestInfoAsList.add(requestType);
        requestInfoAsList.add(requestURI);
        return requestInfoAsList;
    }

    private Map<String, String> readRequestHeadersToMap(String requestInfo) {
        Map<String, String> requestHeadersAsMap = new HashMap<>();
        String[] requestHeadersArray = Arrays.copyOfRange(requestInfo.split("\r\n"), 1, requestInfo.split("\r\n").length);
        for (String requestHeader : requestHeadersArray) {
            String[] requestHeaderLine = requestHeader.split(": ");
            if (requestHeaderLine.length > 1) {
                requestHeadersAsMap.put(requestHeaderLine[0], requestHeaderLine[1]);
            }
        }
        return requestHeadersAsMap;
    }

    private byte[] readRequestBodyAsBytes(int requestBodyLength, BufferedInputStream bf) throws IOException {
        byte[] requestBody = new byte[requestBodyLength];
        int read = 0;
        int bytesRead = 0;
        while (read != -1 && bytesRead != requestBodyLength) {
            read = bf.read();
            requestBody[bytesRead] = (byte) read;
            bytesRead++;
        }
        return requestBody;
    }

    private String constructStatusLine(Response response) {
        int statusCode = response.getStatusCode();
        return "HTTP/1.1 " + statusCode + " " + findProperStatusMessage(statusCode) + "\r\n";
    }

    private String findProperStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 500:
                return "Internal Server Error";
            default:
                throw new IllegalArgumentException("Unknown status code.");
        }
    }

    /*private List<RequestHandler> createDynamicResponseObjects() {

        List<RequestHandler> responseObjects = new ArrayList<>();

        PostResponse postResponseClass = new PostResponse();
        DeleteResponse deleteResponseClass = new DeleteResponse();
        responseObjects.add(postResponseClass);
        responseObjects.add(deleteResponseClass);
        return responseObjects;
    }*/

    private boolean checkURIMatching(String matchingRequestURI, Request request) {
        if (matchingRequestURI.contains("*")) {
            int indexOfStar = matchingRequestURI.indexOf('*');
            if (request.getRequestURI().length() >= indexOfStar) {
	            return matchingRequestURI.substring(0, indexOfStar).equals(request.getRequestURI().substring(0, indexOfStar));
            }
        }
	    return matchingRequestURI.equals(request.getRequestURI());
    }
}
