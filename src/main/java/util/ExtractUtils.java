package util;

public class ExtractUtils {
	public static String extractMethod(String httpHeaderLine) throws Exception {
		String[] tokens = httpHeaderLine.split(" ");
		
		if(tokens.length == 3) {
			return tokens[0];
		}else {
			throw new Exception("method 추출 가능 라인이 아닙니다.");
		}
	}
	
	public static String extractUrl(String httpHeaderLine) throws Exception {
		String[] tokens = httpHeaderLine.split(" ");
		
		if(tokens.length == 3) {
			return tokens[1];
		}else {
			throw new Exception("url 추출 가능 라인이 아닙니다.");
		}
	}
	
	public static String extractRequestPath(String url) {
		int index = url.indexOf("?");
		if(index != -1) {
			return url.substring(0, index);
		}else {
			return url;
		}
	}
	
	public static String extractParams(String url) throws Exception {
		int index = url.indexOf("?");
		if(index != -1) {
			return url.substring(index+1);
		}else {
			throw new Exception("params 추출 가능한 url이 아닙니다.");
		}
	}
	
	public static int extractContentLength(String line) {
		String[] tokens = line.split(" ");
		int length = Integer.parseInt(tokens[1]);
		return length;
	}
	
	public static String extractExtension(String url) {
		String[] tokens = url.split("\\.");
		String extension = tokens[tokens.length-1];
		return extension;
	}
}
