package webserver;

import java.nio.file.Path;

public class ServerConfig {

    private Path directory;

    public ServerConfig(Path directory) {
        this.directory = directory;
    }

    public Path getDirectory() {
        return directory;
    }
}
