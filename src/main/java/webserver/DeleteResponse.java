package webserver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DeleteResponse {

	public Response deleteResponse(String fileName, File directory) {
		System.out.println("Delete request received");
		String statusLine;
		List<String> headers = new ArrayList<>();
		byte[] body = null;
		Path filePath = Paths.get(directory.toString() + fileName);
		System.out.println(filePath.toString());
		if (fileName.equals("\\")) {
			statusLine = "HTTP/1.1 400 Bad Request\r\n";
		} else {
			if (Files.exists(filePath)) {
				File deletable = new File(filePath.toString());
				if (deletable.isDirectory()) {
					if (RecursiveDeleter.deleteDirectory(deletable)) {
						statusLine = "HTTP/1.1 200 OK\r\n";
					}
					else {
						statusLine = "HTTP/1.1 500 Internal Server Error\r\n";
					}
				}
				else if (deletable.isFile()) {
					if (deletable.delete()) {
						statusLine = "HTTP/1.1 200 OK\r\n";
					} else {
						statusLine = "HTTP/1.1 500 Internal Server Error\r\n";
					}
				}
				else {
					statusLine = "HTTP/1.1 500 Internal Server Error\r\n";
				}
			} else {
				statusLine = "HTTP/1.1 500 Internal Server Error\r\n";
			}
		}
		return new Response(statusLine, headers, body);
	}
}
