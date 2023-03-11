package webserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import db.DataBase;
import model.User;

public class CreateUserController extends AbstractController {

	@Override
	public void doPost(HttpRequest request, HttpResponse response) throws UnsupportedEncodingException {
		User user = new User(request.getParameter("userId"), 
		request.getParameter("password"), 
		URLDecoder.decode(request.getParameter("name"), "UTF-8"),
		URLDecoder.decode(request.getParameter("email"), "UTF-8"));
		DataBase.addUser(user);
		response.sendRedirect("/index.html");
	}

}
