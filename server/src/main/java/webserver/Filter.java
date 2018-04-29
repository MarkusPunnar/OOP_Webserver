package webserver;

public interface Filter {

    Response doFilter(Request request) throws Exception;

    void initialize(ServerConfig sc);
}
