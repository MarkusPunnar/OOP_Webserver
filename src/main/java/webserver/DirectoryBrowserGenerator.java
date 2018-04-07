package webserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryBrowserGenerator {

    public static boolean generate(File givenDir, File defaultDir) throws IOException {
	    Path generatedHtml = Paths.get("src","main","resources","defaultwebsite", "generatedResponse.html");
	    File previouslyGeneratedFile = generatedHtml.toFile();
	    if (previouslyGeneratedFile.exists()) {
		    if (!previouslyGeneratedFile.delete()) {
		    	return false;
		    }
	    }
	    String htmlContent = "<!DOCTYPE html>\n" +
			    "<html lang=\"en\">\n" +
			    "<head>\n" +
			    "    <meta charset=\"UTF-8\">\n" +
			    "    <title>Title</title>\n" +
			    "</head>\n" +
			    "<body>\n";

	    File[] filesInGivenDir = givenDir.listFiles();
	    if (filesInGivenDir.length > 0) {
		    for (File file : filesInGivenDir) {
			    htmlContent = htmlContent + "<a href=" + "\"" + "http://localhost:1337/";
			    if (givenDir.equals(defaultDir)) {
				    htmlContent = htmlContent + file.getName() + "\"" +">" + file.getName() + "</a>\n <br>";
			    } else {
				    htmlContent = htmlContent + givenDir.getName() + "/" + file.getName() + "\"" +">" + file.getName() + "</a>\n <br>";
			    }
		    }
	    } else {
	    	htmlContent = htmlContent + "<p>Empty directory</p>\n";
	    }
	    htmlContent = htmlContent + "</body>\n" + "</html>";
	    try(PrintWriter out = new PrintWriter(previouslyGeneratedFile)) {
		    out.print(htmlContent);
	    }
	    return true;
    }


}
