package util;

import static org.junit.Assert.*;

import org.junit.Test;

import util.ExtractUtils;

public class ExtractUtilsTest {

	@Test
	public void extractUrl() {
		try {
			assertEquals("/index.html",ExtractUtils.extractUrl("GET /index.html HTTP/1.1"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void extractRequestPath() {
		assertEquals("/",ExtractUtils.extractRequestPath("/?data=234"));
	}
	
	@Test
	public void extractParams() {
		try {
			assertEquals("data=234",ExtractUtils.extractParams("/?data=234"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void extractContentLength() {
		assertEquals(64,ExtractUtils.extractContentLength("Content-Length: 64"));
	}
}
