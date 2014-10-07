package contact.service;


import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;



public class EtagTest {
	
	private static HttpClient client;
	
	/*
	 * Do before the test begin
	 */
	@BeforeClass
	public static void first() throws Exception{
		JettyMain.startServer(8080);
		client = new HttpClient();
		client.start();
	}
	
	/*
	 * Do after the test finished
	 */
	@AfterClass
	public static void last() throws Exception {
		JettyMain.stopServer();
	}
	
	/*
	 * test get with etag
	 */
	@Test
	public void etagGet() throws InterruptedException, ExecutionException, TimeoutException {
		ContentResponse response = client.GET("http://localhost:8080/contacts/1");
		String etag = response.getHeaders().get("ETag");
		assertTrue("Show etag", etag != null);
	}

	/*
	 * test post with etag
	 */
	@Test
	public void etagPost() throws InterruptedException, TimeoutException, ExecutionException {
		StringContentProvider content = new StringContentProvider("<contact id=\"5\">" +
				"<title>title5</title>" +
				"<name>name5 lastname5</name>" +
				"<email>555@email</email>" +
				"<phoneNumber>5555</phoneNumber>"+
				"</contact>");
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts");
		request.method(HttpMethod.POST);
		request.content(content, "application/xml");
		
		ContentResponse response = request.send();
		String etag = response.getHeaders().get("ETag");
		assertTrue("Show etag", etag != null);
		
	}
	
	
}
