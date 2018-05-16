package webserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebServerUtil {
    public static byte[] readFileFromClasspath(String fileName) throws IOException {
        try (InputStream is = WebServer.class.getClassLoader().getResourceAsStream(fileName)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            try {
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
            } catch (NullPointerException e) {
                return new byte[1024];
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    public static byte[] readFileFromClasspathDirectory (String dir, String fileName) throws IOException {
        return readFileFromClasspath(dir + "/" + fileName);
    }
}
