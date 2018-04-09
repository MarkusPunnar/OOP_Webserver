package webserver;

import java.io.File;

public class RecursiveDeleter {

    public static boolean deleteDirectory(File deletable) {
        File[] content = deletable.listFiles();
        if (content != null) {
            for (File file : content) {
                deleteDirectory(file);
            }
        }
        return deletable.delete();
    }

}
