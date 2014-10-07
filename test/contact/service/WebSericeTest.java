package contact.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;


/**
 * J-Unit test of ContactResource.java
 * @author Eknarin Thirayothin 5510546239
 *
 */

public class WebSericeTest {
	
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
	 * test Get data in pass case
	 */
	@Test
	public void getPass() throws InterruptedException, ExecutionException, TimeoutException {
		ContentResponse response = client.GET("http://localhost:8080/contacts/1");
		assertEquals("200 OK", Status.OK.getStatusCode(), response.getStatus());
	}
	
	/*
	 * Test Get data in fail case
	 */
	@Test
	public void getFail() throws InterruptedException, ExecutionException, TimeoutException {
		 ContentResponse response = client.GET("http://localhost:8080/contacts/9999");
		 assertEquals("404 Not Found", Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
	
	/*
	 * Test Post data in pass case
	 */
	@Test
	public void postPass() throws InterruptedException, TimeoutException, ExecutionException {
		org.eclipse.jetty.client.api.Request request2 = client.newRequest("http://localhost:8080/contacts/4");
		request2.method(HttpMethod.DELETE);
		ContentResponse response2 = request2.send();
		
		StringContentProvider content = new StringContentProvider("<contact id=\"4\">" +
				"<title>title4</title>" +
				"<name>name4 lastname4</name>" +
				"<email>444@email</email>" +
				"<phoneNumber>4444</phoneNumber>"+
				"</contact>");
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts");
		request.method(HttpMethod.POST);
		request.content(content, "application/xml");
		
		ContentResponse response = request.send();
		
		
		assertEquals("201 Created", Status.CREATED.getStatusCode(), response.getStatus());		
	}
	
	/*
	 * Test Post data in fail case
	 */
	@Test
	public void postFail() throws InterruptedException, TimeoutException, ExecutionException {
		StringContentProvider content = new StringContentProvider("<contact id=\"1\">" +
				"<title>title1</title>" +
				"<name>name1 lastname1</name>" +
				"<email>111@email</email>" +
				"<phoneNumber>1111111</phoneNumber>"+
				"</contact>");
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts");
		request.method(HttpMethod.POST);
		request.content(content, "application/xml");
		
		ContentResponse response = request.send();
		assertEquals("Conflict", Status.CONFLICT.getStatusCode(), response.getStatus());
	}
	
	/*
	 * Test Put/Update data in pass case
	 */
	@Test
	public void putPass() throws InterruptedException, TimeoutException, ExecutionException{
		StringContentProvider content = new StringContentProvider("<contact id=\"1\">" +
				"<title>new title1</title>" +
				"<name>new_name1 new_lastname1</name>" +
				"<email>new_111@email</email>" +
				"<phoneNumber>new_1111111</phoneNumber>"+
				"</contact>");
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts/1");
		request.method(HttpMethod.PUT);
		request.content(content, "application/xml");
		
		ContentResponse response = request.send();
		assertEquals("200 OK", Status.OK.getStatusCode(), response.getStatus());
	}
	
	/*
	 * Test Put/Update data in fail case
	 */
	@Test
	public void putFail() throws InterruptedException, TimeoutException, ExecutionException {
		StringContentProvider content = new StringContentProvider("<contact id=\"0\">" +
				"<title>error title1</title>" +
				"<name>error_name error_lastname</name>" +
				"<email>error@email</email>" +
				"<phoneNumber>error</phoneNumber>"+
				"</contact>");
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts/0");
		request.method(HttpMethod.PUT);
		request.content(content, "application/xml");
		
		ContentResponse response = request.send();
		assertEquals("Not Found", Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	/*
	 * Test Delete data in pass case
	 */
	@Test
	public void deletePass() throws InterruptedException, TimeoutException, ExecutionException {
		StringContentProvider content = new StringContentProvider("<contact id=\"8\">" +
				"<title>title8</title>" +
				"<name>name4 lastname8</name>" +
				"<email>888@email</email>" +
				"<phoneNumber>8888</phoneNumber>"+
				"</contact>");
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts");
		request.method(HttpMethod.POST);
		request.content(content, "application/xml");
		
		ContentResponse response = request.send();
		org.eclipse.jetty.client.api.Request request2 = client.newRequest("http://localhost:8080/contacts/8");
		request.method(HttpMethod.DELETE);
		ContentResponse response2 = request2.send();
		
		assertEquals("200 OK", Status.OK.getStatusCode(), response2.getStatus());
	}
	
	/*
	 * Test Delete data in fail case
	 */
	@Test
	public void deleteFail() throws InterruptedException, TimeoutException, ExecutionException {
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts/6789");
		request.method(HttpMethod.DELETE);
		ContentResponse response = request.send();
		
		assertEquals("404 NOT FOUND", Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
}
