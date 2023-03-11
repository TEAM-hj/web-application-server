package webserver;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController {

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws IOException {
		Map<String, String> cookieMap = null;
		cookieMap = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));
		
		if (Boolean.parseBoolean(cookieMap.get("logined"))) {
			StringBuilder sb = new StringBuilder();
			Collection<User> users = DataBase.findAll();
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

}
