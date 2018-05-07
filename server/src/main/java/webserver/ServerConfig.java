package webserver;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ServerConfig {

    private Path directory;
    private Map<String, String> mimeTypes;
    private Map<MappingInfo, HandlerInfo> dynamicResponseURIs;
    private List<Filter> filters;

    public ServerConfig(Path directory, Map<String, String> mimeTypes, Map<MappingInfo, HandlerInfo> dynamicResponseURIs, List<Filter> filters) {
        this.directory = directory;
        this.mimeTypes = mimeTypes;
        this.dynamicResponseURIs = dynamicResponseURIs;
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

    public String getDirectoryAsString() {
        return directory.toString();
    }

    public List<Filter> getFilters() {
        return filters;
    }
}
