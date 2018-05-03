package webserver;

public interface RequestHandler {

    Response handle(Request request) throws Exception;

    void initialize(ServerConfig sc);
}
