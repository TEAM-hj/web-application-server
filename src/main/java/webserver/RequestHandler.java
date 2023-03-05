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
import java.nio.file.Files;
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

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			Reader reader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(reader);
			DataOutputStream dos = new DataOutputStream(out);
			String line = br.readLine();
			if (line == null) {
				return;
			}
			String url = HttpRequestUtils.getPageAddress(line);
			byte[] body = "Hello World".getBytes();
			if ("/index.html".equals(url)) {
				body = Files.readAllBytes(new File("./webapp" + url).toPath());
			}
			if ("/user/form.html".equals(url)) {
				body = Files.readAllBytes(new File("./webapp" + url).toPath());
			}
			if ("/user/login.html".equals(url)) {
				body = Files.readAllBytes(new File("./webapp" + url).toPath());
			}
			if (url.contains("/user/create")) {
				int contentLength = 0;
				while (!(line = br.readLine()).isEmpty()) {
					log.debug("http line: {}", line);
					if (line.contains("Content-Length")) {
						contentLength = Integer.parseInt(line.split(" ")[1]);
						log.debug("contentLength: {}", contentLength);
					}
				}
				String postData = IOUtils.readData(br, contentLength);
				log.debug("post data: {}", postData);
				Map<String, String> userMap = HttpRequestUtils.parseQueryString(postData);
				User user = new User(userMap.get("userId"), userMap.get("password"), userMap.get("name"),
						userMap.get("email"));
				DataBase.addUser(user);
				log.debug("user: {}", user.toString());
				response302Header(dos, body.length);
			} else {
				response200Header(dos, body.length);
				responseBody(dos, body);
			}
			if (("/user/login").equals(url)) {
				int contentLength = 0;
				while (!(line = br.readLine()).isEmpty()) {
					log.debug("http line: {}", line);
					if (line.contains("Content-Length")) {
						contentLength = Integer.parseInt(line.split(" ")[1]);
						log.debug("contentLength: {}", contentLength);
					}
				}
				String postData = IOUtils.readData(br, contentLength);
				log.debug("post data: {}", postData);
				Map<String, String> userMap = HttpRequestUtils.parseQueryString(postData);
				User loginUser = new User(userMap.get("userId"), userMap.get("password"), userMap.get("name"),
						userMap.get("email"));
				User dbUser = DataBase.findUserById(loginUser.getUserId());
				if (dbUser.getPassword().equals(loginUser.getPassword())) {
					log.debug("login 성공");
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://localhost:8080/index.html \r\n");
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
}
