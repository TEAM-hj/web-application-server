package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private final BufferedReader br;
	private Map<String, String> header = new HashMap<>();
	private Map<String, String> parameter = new HashMap<>();
	private String firstLine;
	
	public HttpRequest(InputStream in) throws IOException {
		br = new BufferedReader(new InputStreamReader(in));
		firstLine = br.readLine();
		String line = "";
		
		while ((line = br.readLine()) != null && !line.isEmpty()) {
			String[] headerArr = line.split(": ");
			header.put(headerArr[0].trim(), headerArr[1].trim());
		}
		
		if (getMethod().equals("GET")) {
			String pathAndQueryString = firstLine.split(" ")[1];
			parameter = HttpRequestUtils.getParams(pathAndQueryString);
		} else {
			String postData = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
			parameter = HttpRequestUtils.parseQueryString(postData);
		}
	}
	
	public String getMethod() {
		return firstLine.split(" ")[0];
	}
	
	public String getPath() {
		if (getMethod().equals("GET")) {
			return firstLine.split(" ")[1].split("\\?")[0];
		}
		return firstLine.split(" ")[1];
	}
	
	public String getHeader(String name) throws IOException {
		return header.get(name);
	}
	
	public String getParameter(String name) {
		return parameter.get(name);
	}
}
