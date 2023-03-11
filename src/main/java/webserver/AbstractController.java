package webserver;

public abstract class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {}
	
	public void doGet(HttpRequest request, HttpResponse response) throws Exception {}
	
	public void doPost(HttpRequest request, HttpResponse response) throws Exception {}
	

}
