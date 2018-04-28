package webserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecursiveDeleter {

    public static void deleteDirectory(Path deletable) throws IOException {
        List<Path> itemsInDir = new ArrayList<>();
        Files.list(deletable).forEach(itemsInDir::add);
        for (Path filePath : itemsInDir) {
            if (Files.isDirectory(filePath) && Files.list(filePath).toArray().length != 0) {
                deleteDirectory(filePath);
            } else {
                Files.delete(filePath);
            }
        }
    }
}
