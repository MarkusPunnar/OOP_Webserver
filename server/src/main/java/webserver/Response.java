package webserver;

import java.util.Map;

public class Response {

	private int statusCode;
	private Map<String, String> headers;
	private byte[] body;

	public Response(int statusCode, Map<String, String> headers, byte[] body) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}
}
