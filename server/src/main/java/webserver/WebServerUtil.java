package webserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebServerUtil {
    public static byte[] readFileFromClasspath(String path, String fileName) throws IOException {
        try (InputStream is = WebServer.class.getClassLoader().getResourceAsStream(path + "/" + fileName)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }
}
