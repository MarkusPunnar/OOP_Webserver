package webserver;

import java.io.File;
import java.io.IOException;


public class DirectoryBrowserGenerator {

    public static byte[] generate(File givenDir, File defaultDir) throws IOException {
        String relativeDirname = "";
        String parentDirname = "";
        String separator = File.separator;
        if (!separator.equals("/")) {
            separator = "\\\\";
        }
        String[] givenDirPathSplit = givenDir.getAbsolutePath().split(separator);
        String[] defaultDirPathSplit = defaultDir.getAbsolutePath().split(separator);

        for (int i = defaultDirPathSplit.length; i < givenDirPathSplit.length; i++) {
            relativeDirname += "/" + givenDirPathSplit[i];
            if (i == givenDirPathSplit.length - 2) {
                parentDirname = relativeDirname;
            }
        }
        if (givenDirPathSplit.length - defaultDirPathSplit.length == 1) {
            parentDirname = "/";
        }


        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + givenDir.getName() + "</title>\n" +
                "</head>\n" +
                "<body>\n";

        File[] filesInGivenDir = givenDir.listFiles();
        if (!givenDir.equals(defaultDir)) {
            htmlContent += "<a href=\"" + parentDirname + "\">" + "..." + "</a> \n <br>";
        }
        String returndir;
        if (relativeDirname.equals("")) {
            returndir = "/";
        } else {
            returndir = relativeDirname;
        }
        if (filesInGivenDir.length > 0) {
            for (File file : filesInGivenDir) {
                htmlContent += "<a href=\"" + relativeDirname + "/" + file.getName() + "\">" + file.getName() + "</a>"
                        + "<a style=\"color: red;float: right\" href=\"delete" + relativeDirname + "/" + file.getName()
                        + "?return=" + returndir + "\">Delete</a> <br>";
            }
        } else {
            htmlContent += "<p>Empty directory</p>\n";
        }
        htmlContent += "</body>\n" + "</html>";
        return htmlContent.getBytes("UTF-8");
    }
}
