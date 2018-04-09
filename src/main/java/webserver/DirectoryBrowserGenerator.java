package webserver;
import java.io.File;
import java.io.IOException;


public class DirectoryBrowserGenerator {

    public static byte[] generate(File givenDir, File defaultDir) throws IOException {
    	String relativeDirname = "";
    	String parentDirname = "";
    	String[] givenDirPathSplit = givenDir.getAbsolutePath().split("\\\\");
    	String[] defaultDirPathSplit = defaultDir.getAbsolutePath().split("\\\\");

	    for (int i = defaultDirPathSplit.length; i < givenDirPathSplit.length; i++) {
		    relativeDirname = relativeDirname + "/" + givenDirPathSplit[i];
		    if (i == givenDirPathSplit.length-2) {
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
		    htmlContent = htmlContent + "<a href=\"" + parentDirname + "\">" + "..." + "</a> \n <br>";
	    }
	    if (filesInGivenDir.length > 0) {
		    for (File file : filesInGivenDir) {
			    htmlContent = htmlContent + "<a href=\"";
			    htmlContent = htmlContent + relativeDirname + "/" + file.getName() + "\">" + file.getName() + "</a>\n <br>";

		    }
	    } else {
	    	htmlContent = htmlContent + "<p>Empty directory</p>\n";
	    }
	    htmlContent = htmlContent + "</body>\n" + "</html>";
	    return htmlContent.getBytes("UTF-8");
    }


}
