package webserver;

import java.nio.file.Path;
import java.util.Map;

public class ServerConfig {

    private Path directory;
    private Map<String, String> mimeTypes;
    private Map<String, RequestHandler> dynamicResponseURIs;
    private FilterChain chain;

    public ServerConfig(Path directory, Map<String, String> mimeTypes, Map<String, RequestHandler> dynamicResponseURIs, FilterChain chain) {
        this.directory = directory;
        this.mimeTypes = mimeTypes;
        this.dynamicResponseURIs = dynamicResponseURIs;
        this.chain = chain;
    }

    public Map<String, String> getMimeTypes() {
        return mimeTypes;
    }

    public Map<String, RequestHandler> getDynamicResponseURIs() {
        return dynamicResponseURIs;
    }

    public Path getDirectory() {
        return directory;
    }

    public String getDirectoryAsString() {
        return directory.toString();
    }

    public FilterChain getChain() {
        return chain;
    }
}
