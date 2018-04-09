package webserver;

import java.io.File;
import java.io.IOException;


public class DirectoryBrowserGenerator {

    public static byte[] generate(File givenDir, File defaultDir) throws IOException {
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + givenDir.getName() + "</title>\n" +
                "</head>\n" +
                "<body>\n";

        File[] filesInGivenDir = givenDir.listFiles();
        if (!givenDir.equals(defaultDir)) {
            File parentFile = givenDir.getParentFile();
            if (parentFile.equals(defaultDir)) {
                htmlContent = htmlContent + "<a href=\"http://localhost:1337/" + "\">" + "..." + "</a> \n <br>";
            } else {
                htmlContent = htmlContent + "<a href=\"http://localhost:1337/" + givenDir.getParentFile().getName() + "\">" + "..." + "</a> \n <br>";
            }
        }
        if (filesInGivenDir.length > 0) {
            for (File file : filesInGivenDir) {
                htmlContent = htmlContent + "<a href=\"http://localhost:1337/";
                if (givenDir.equals(defaultDir)) {
                    htmlContent = htmlContent + file.getName() + "\"" + ">" + file.getName() + "</a>\n <br>";
                } else {
                    htmlContent = htmlContent + givenDir.getName() + "/" + file.getName() + "\">" + file.getName() + "</a>\n <br>";
                }
            }
        } else {
            htmlContent = htmlContent + "<p>Empty directory</p>\n";
        }
        htmlContent = htmlContent + "</body>\n" + "</html>";
        return htmlContent.getBytes("UTF-8");
    }


}
