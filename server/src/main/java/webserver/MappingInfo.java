package webserver;

public class MappingInfo {

    private String requestURI;
    private String requestMethod;

    public MappingInfo(String requestURI, String requestMethod) {
        this.requestURI = requestURI;
        this.requestMethod = requestMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getRequestMethod() {
        return requestMethod;
    }
}
