package webserver;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerConfig {

    private Path directory;
    private Map<String, String> mimeTypes;
    private Map<MappingInfo, HandlerInfo> dynamicResponseURIs = new HashMap<>();
    private List<Filter> filters;
    private Map<String, Object> attributes = new HashMap<>();

    public ServerConfig(Path directory, Map<String, String> mimeTypes, List<Filter> filters) {
        this.directory = directory;
        this.mimeTypes = mimeTypes;
        this.filters = filters;
    }

    public Map<String, String> getMimeTypes() {
        return mimeTypes;
    }

    public Map<MappingInfo, HandlerInfo> getDynamicResponseURIs() {
        return dynamicResponseURIs;
    }

    public Path getDirectory() {
        return directory;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
