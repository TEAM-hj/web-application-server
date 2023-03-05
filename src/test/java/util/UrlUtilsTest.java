package util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

public class UrlUtilsTest {

	@Test
	public void getRequestUrl() {
		String requestUrl = UrlUtils.getRequestUrl("GET /index.html HTTP/1.1");
		assertThat(requestUrl, is("/index.html"));
	}

}
