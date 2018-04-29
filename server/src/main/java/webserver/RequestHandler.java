package webserver;

import java.util.Map;

public interface RequestHandler {

   Response handle(Request request) throws Exception;

   void register(Map<String, RequestHandler> patterns);

   void initialize(ServerConfig sc);

}
