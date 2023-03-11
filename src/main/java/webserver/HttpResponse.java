package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	DataOutputStream dos;
	Map<String, String> header = new HashMap<>();
	byte[] body = null;
			
	public HttpResponse(OutputStream createOutputStream) {
		dos = new DataOutputStream(createOutputStream);
	}
	
	public void addHeader(String name, String value) {
		header.put(name, value);
	}

	public void forward(String url) throws IOException {
		String ext = url.substring(url.lastIndexOf(".")+1);
		header.put("Content-Type", "text/" + ext + ";charset=utf-8 \r\n");
		body = Files.readAllBytes(new File("./webapp" + url).toPath());
		
		response200Header(body.length);
		responseBody(body);
	}

	public void forwardBody(String bodyString) {
		header.put("Content-Type", "text/html;charset=utf-8 \r\n");
		body = bodyString.getBytes();
		response200Header(body.length);
		responseBody(body);
	}
	
	private void response200Header(int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("Content-Type: " + header.get("Content-Type"));
			processHeaders();
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void sendRedirect(String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			processHeaders();
			dos.writeBytes("\r\n\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void processHeaders() throws IOException {
		if(header.get("Set-Cookie") != null) {
			dos.writeBytes("Set-Cookie: " + header.get("Set-Cookie") + "\r\n");
		}
	}
}
