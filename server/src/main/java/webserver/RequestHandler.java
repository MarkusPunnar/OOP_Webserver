package webserver;
import java.io.IOException;

public interface RequestHandler {

    void initialize(ServerConfig sc) throws IOException;

}
