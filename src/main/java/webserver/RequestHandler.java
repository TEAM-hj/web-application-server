package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	private Map<String,Controller> controllers = new HashMap<String,Controller>();

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/login", new LoginUserController());
		controllers.put("/user/list", new ListUserController());
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);

			if(controllers.containsKey(request.getPath())) {
				Controller controller = controllers.get(request.getPath());
				controller.service(request, response);
			}else {
				response.forward(request.getPath());
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
