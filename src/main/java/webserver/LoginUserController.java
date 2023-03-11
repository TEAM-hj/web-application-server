package webserver;

import db.DataBase;
import model.User;

public class LoginUserController extends AbastractController {
	protected void doPost(HttpRequest request, HttpResponse response) {
		
		User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
    	User loginUser = DataBase.findUserById(user.getUserId());
    	
    	if(loginUser != null && loginUser.getPassword().equals(user.getPassword())) {
    		response.addHeader("Set-Cookie", "logined=true");
    		response.sendRedirect("/index.html");
    	}else {
    		response.sendRedirect("/index.html");
    	}
	}
}
