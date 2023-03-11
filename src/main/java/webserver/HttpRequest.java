package webserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ExtractUtils;
import util.HttpRequestUtils;
import util.IOUtils;
import util.HttpRequestUtils.Pair;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private String method;
	private String path;
	private Map<String,String> header = new HashMap<String,String>();
	private Map<String,String> parameter = new HashMap<String,String>();
	Map<String,String> cookies = new HashMap<String,String>();

	public HttpRequest(InputStream in) throws Exception {
		// TODO Auto-generated constructor stub
		//BufferedReader를 사용해 스트림을 읽어들임
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String line = br.readLine();
        
        if(line == null) {
        	return;
        }
        
        log.debug(line);
        String url = ExtractUtils.extractUrl(line);
        method = ExtractUtils.extractMethod(line);
        log.debug("Extracted url : {}",url);
        path = ExtractUtils.extractRequestPath(url);
        
        while(line != null && !"".equals(line)) {
        	
        	line = br.readLine();
        	log.debug(line);
        	Pair headerPair = HttpRequestUtils.parseHeader(line);
        	if(headerPair != null) {
        		header.put(headerPair.getKey(),headerPair.getValue());
        	}
        }
        
        if(header.containsKey("Cookie")) {
        	cookies = HttpRequestUtils.parseCookies(header.get("Cookie"));
        }
        
        if(method.equals("GET")) {
        	try {
        		String params = ExtractUtils.extractParams(url);
        		parameter =  HttpRequestUtils.parseQueryString(params);
        	}catch(Exception e) {
        		log.debug(e.getMessage());
        	}
        }else if(method.equals("POST")) {
        	String reqBody = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
            log.debug("Request Body : {}",reqBody);
            parameter = HttpRequestUtils.parseQueryString(reqBody);
        }
	}

	public String getMethod() {
		// TODO Auto-generated method stub
		return method;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return path;
	}

	public String getHeader(String key) {
		// TODO Auto-generated method stub
		return header.get(key);
	}

	public String getParameter(String key) {
		// TODO Auto-generated method stub
		return parameter.get(key);
	}

	public String getCookie(String key) {
		// TODO Auto-generated method stub
		return cookies.get(key);
	}

}
