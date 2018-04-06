package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryBrowserGenerator {
    private String dirName;

    public DirectoryBrowserGenerator(String dirName) throws IOException {
        Path defaultWebSiteDir = Paths.get("src","main","resources","defaultwebsite","filebrowser");
        deleteDirectory(new File(defaultWebSiteDir.toString()));
        Files.createDirectory(defaultWebSiteDir);
        update();
    }

    public boolean deleteDirectory(File deletable) {
        File[] content = deletable.listFiles();
        if (content != null) {
            for (File file : content) {
                deleteDirectory(file);
            }
        }
        return deletable.delete();
    }

    public void update() {

    }

    private void scanFolder() {
        File currentDir = new File(dirName);
        if (!currentDir.isDirectory()) {
            return;
        }


    }

    private void generate() {

    }


}
