package webserver;

public abstract class AbastractController implements Controller{
	public void service(HttpRequest request, HttpResponse response) {
		if("GET".equals(request.getMethod())) {
			doGet(request,response);
		}else if("POST".equals(request.getMethod())) {
			doPost(request,response);
		}
	}
	
	protected void doPost(HttpRequest request, HttpResponse response) {
		
	}
	
	protected void doGet(HttpRequest request, HttpResponse response) {
		
	}
}
