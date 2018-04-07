package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryBrowserGenerator {
    private String dirName;

    public DirectoryBrowserGenerator(String dirName) throws IOException {
        Path defaultWebSiteDirPath = Paths.get("src","main","resources","defaultwebsite","filebrowser");
        File defaultWebSiteDir = new File(defaultWebSiteDirPath.toString());
        if (defaultWebSiteDir.isDirectory() && defaultWebSiteDir.exists()) {
            RecursiveDeleter.deleteDirectory(defaultWebSiteDir);
        }
        Files.createDirectory(defaultWebSiteDirPath);
        update();
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
