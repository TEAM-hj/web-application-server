package webserver;

import java.util.Iterator;

import db.DataBase;
import model.User;

public class ListUserController extends AbastractController {
	protected void doGet(HttpRequest request, HttpResponse response) {
		boolean isLogined = Boolean.parseBoolean(request.getCookie("logined"));
    	if(isLogined) {
    		
    		StringBuilder sb = new StringBuilder();
    		Iterator<User> iu = DataBase.findAll().iterator();
    		while(iu.hasNext()) {
    			User u = iu.next();
    			sb.append(u.getUserId());
    		}
    		byte[] body = sb.toString().getBytes();
    		response.response200Header(body.length);
    		response.responseBody(body);
    	}else {
    		response.sendRedirect("/index.html");
    	}
	}
}
