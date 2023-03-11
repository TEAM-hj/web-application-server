package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;
	private static boolean logined = false;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			String url = request.getPath();
			
			if (url.endsWith(".html") || url.endsWith(".css")) {
				response.forward(request.getPath());
				return;
			}
			
			if (url.contains("/user/create")) {
				User user = new User(request.getParameter("userId"), 
									 request.getParameter("password"), 
									 URLDecoder.decode(request.getParameter("name"), "UTF-8"),
									 URLDecoder.decode(request.getParameter("email"), "UTF-8"));
				DataBase.addUser(user);
				log.debug("user: {}", user.toString());
				response.sendRedirect("/index.html");
				return;
			}
			
			if (("/user/login").equals(url)) {
				User loginUser = new User(request.getParameter("userId"), 
						 				  request.getParameter("password"), "", "");
				User dbUser = DataBase.findUserById(loginUser.getUserId());
				
				if (dbUser!=null && dbUser.getPassword().equals(loginUser.getPassword())) {
					log.debug("login 성공");
					response.addHeader("Set-Cookie", "logined=true");
					response.sendRedirect("/index.html");
					return;
				} else {
					logined = false;
					response.forward("./webapp/user/login_failed.html");
				}
			}
			
			if (("/user/list").equals(url)) {
				Map<String, String> cookieMap = null;
				cookieMap = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));
				
				if (Boolean.parseBoolean(cookieMap.get("logined"))) {
					StringBuilder sb = new StringBuilder();
					ArrayList<User> users = new ArrayList(DataBase.findAll());
					for (User user : users) {
						sb.append("<li>사용자 이름: ").append(user.getName()).append("</li>");
					}
					response.forwardBody(sb.toString());
				} else {
					response.addHeader("Set-Cookie", "logined=false");
					response.sendRedirect("/user/login.html");
					return;
				}
			}
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}
