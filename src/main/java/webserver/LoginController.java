package webserver;

import java.io.IOException;

import db.DataBase;
import model.User;

public class LoginController extends AbstractController {

	@Override
	public void doPost(HttpRequest request, HttpResponse response) throws IOException {
		User loginUser = new User(request.getParameter("userId"), 
				  				  request.getParameter("password"), "", "");
		
		if (isLogin(loginUser)) {
			response.addHeader("Set-Cookie", "logined=true");
			response.sendRedirect("/index.html");
			return;
		} else {
			response.forward("./webapp/user/login_failed.html");
		}
	}
	
	private boolean isLogin(User loginUser) {
		User dbUser = DataBase.findUserById(loginUser.getUserId());
		return dbUser!=null && dbUser.getPassword().equals(loginUser.getPassword());
	}

}
