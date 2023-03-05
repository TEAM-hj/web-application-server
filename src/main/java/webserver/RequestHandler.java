package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.ExtractUtils;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            
            //BufferedReader를 사용해 스트림을 읽어들임
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String line = br.readLine();
            log.debug(line);
            String url = ExtractUtils.extractUrl(line);
            log.debug("Extracted url : {}",url);
            String urlExtension = ExtractUtils.extractExtension(url);
            log.debug("Extracted urlExtension : {}",urlExtension);
            
            String requestPath = ExtractUtils.extractRequestPath(url);
            Map<String,String> paramMap = null;
            User user = null;
            
            try {
            	String params = ExtractUtils.extractParams(url);
            	paramMap =  HttpRequestUtils.parseQueryString(params);
            }catch(Exception e) {
            	log.debug(e.getMessage());
            }
            
            if(paramMap != null) {
            	user = new User(paramMap.get("userId"), paramMap.get("password"), paramMap.get("name"), paramMap.get("email"));
            }
            
            int contentLength = 0;
            Map<String,String> cookies = new HashMap<String,String>();
            
            while(!"".equals(line)) {
            	if(line == null) {
            		return;
            	}
            	
            	line = br.readLine();
            	log.debug(line);
            	if(line.indexOf("Content-Length") != -1) {
            		contentLength = ExtractUtils.extractContentLength(line);
            	}else if(line.indexOf("Cookie") != -1) {
            		cookies = HttpRequestUtils.parseCookies(HttpRequestUtils.parseHeader(line).getValue());
            	}
            }
            
            String reqBody = IOUtils.readData(br, contentLength);
            log.debug("Request Body : {}",reqBody);
            
            try {
            	paramMap =  HttpRequestUtils.parseQueryString(reqBody);
            }catch(Exception e) {
            	log.debug(e.getMessage());
            }
            
            if(paramMap != null) {
            	user = new User(paramMap.get("userId"), paramMap.get("password"), paramMap.get("name"), paramMap.get("email"));
            }
            
            byte[] body = null;
            if("/user/create".equals(requestPath)) {
            	DataBase.addUser(user);
            	response302Header(dos, "/index.html");
            }else if("/user/login".equals(requestPath)){
            	User loginUser = DataBase.findUserById(user.getUserId());
            	
            	if(loginUser != null && loginUser.getPassword().equals(user.getPassword())) {
            		response302HeaderCookie(dos, "/index.html","logined=true");
            	}else {
            		response302HeaderCookie(dos, "/user/login_failed.html","logined=false");
            	}
            	
            }else if("/user/list".equals(requestPath)){
            	boolean isLogined = Boolean.parseBoolean(cookies.get("logined"));
            	if(isLogined) {
            		
            		StringBuilder sb = new StringBuilder();
            		Iterator<User> iu = DataBase.findAll().iterator();
            		while(iu.hasNext()) {
            			User u = iu.next();
            			sb.append(u.getUserId());
            		}
            		body = sb.toString().getBytes();
            		response200Header(dos, body.length,"html");
            		responseBody(dos, body);
            	}else {
            		response302Header(dos, "/index.html");
            	}
            }else {
            	body = Files.readAllBytes(new File("./webapp"+url).toPath());
            	response200Header(dos, body.length, urlExtension);
            	responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
        	log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String urlExtension) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
        	dos.writeBytes("Content-Type: text/"+urlExtension+";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos, String url) {
    	try {
        	dos.writeBytes("HTTP/1.1 302 Found \r\n");
        	dos.writeBytes("Location: "+url+" \r\n");
            dos.writeBytes("\r\n");
            dos.flush();
    	}catch(IOException e) {
    		log.error(e.getMessage());
    	}
    }
    
    private void response302HeaderCookie(DataOutputStream dos, String url, String cookie) {
    	try {
    		//dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
    		dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: "+url+" \r\n");
    		dos.writeBytes("Set-Cookie: "+cookie);
    		dos.writeBytes("\r\n");
    		dos.flush();
    	}catch(IOException e) {
    		log.error(e.getMessage());
    	}
    }
}
