package util;

public class UrlUtils {

	public static String getRequestUrl(String httpHeader) {
		return httpHeader.split(" ")[1];
	}
}
