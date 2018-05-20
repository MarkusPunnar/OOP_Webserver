package webserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WebServerUtil {
    protected static byte[] readFileFromClasspath(String fileName) throws IOException {
        try (InputStream is = WebServer.class.getClassLoader().getResourceAsStream(fileName)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (is == null) {
                return null;
            }
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    public static byte[] readFileFromClasspathDirectory(String dir, String fileName) throws IOException {
        return readFileFromClasspath(dir + "/" + fileName);
    }
}
