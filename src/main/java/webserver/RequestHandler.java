package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;
	Map<String, Controller> controllers = new HashMap<>();

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/login", new LoginController());
		controllers.put("/user/list", new ListUserController());
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
				controllers.get("/user/create").service(request, response);
				return;
			}
			
			if (("/user/login").equals(url)) {
				controllers.get("/user/login").service(request, response);
				return;
			}
			
			if (("/user/list").equals(url)) {
				controllers.get("/user/list").service(request, response);
				return;
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
