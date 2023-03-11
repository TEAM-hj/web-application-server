package webserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ExtractUtils;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	OutputStream os;
	Map<String,String> header = new HashMap<String,String>();

	public HttpResponse(OutputStream outputStream) {
		// TODO Auto-generated constructor stub
		os = outputStream;
	}

	public void forward(String url) throws Exception {
		// TODO Auto-generated method stub
		byte[] body = Files.readAllBytes(new File("./webapp"+url).toPath());
		String urlExtension = ExtractUtils.extractExtension(url);
        log.debug("Extracted urlExtension : {}",urlExtension);
        
        try {
            os.write("HTTP/1.1 200 OK \r\n".getBytes());
        	os.write(("Content-Type: text/"+urlExtension+";charset=utf-8\r\n").getBytes());
            os.write(("Content-Length: " + body.length + "\r\n").getBytes());
            for(String key:header.keySet()) {
        		os.write((key+": "+header.get(key)+"\r\n").getBytes());
        	}
            os.write("\r\n".getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        
        responseBody(body);
	}

	public void sendRedirect(String url) {
		// TODO Auto-generated method stub
    	try {
        	os.write("HTTP/1.1 302 Found \r\n".getBytes());
        	os.write(("Location: "+url+" \r\n").getBytes());
        	for(String key:header.keySet()) {
        		os.write((key+": "+header.get(key)+"\r\n").getBytes());
        	}
            os.write("\r\n".getBytes());
            os.flush();
    	}catch(IOException e) {
    		log.error(e.getMessage());
    	}
	}

	public void addHeader(String key, String value) {
		// TODO Auto-generated method stub
		header.put(key, value);
	}
	
    public void responseBody(byte[] body) {
        try {
            os.write(body, 0, body.length);
            os.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

	public void response200Header(int length) {
		// TODO Auto-generated method stub
        try {
            os.write("HTTP/1.1 200 OK \r\n".getBytes());
        	os.write(("Content-Type: text/html;charset=utf-8\r\n").getBytes());
            os.write(("Content-Length: " + length + "\r\n").getBytes());
            os.write("\r\n".getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}

}
