import static org.junit.Assert.*;

import java.lang.Thread.State;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.text.AbstractDocument.Content;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.research.ws.wadl.Request;

import entity.JettyMain;


public class WebSericeTest {
	
	private static String url; 
	private static HttpClient client;
	
	@BeforeClass
	public static void first() throws Exception{
//		url = JettyMain.startServer(8080);
		JettyMain.startServer(8080);
		client = new HttpClient();
		client.start();
	}
	
	@AfterClass
	public static void last() throws Exception {
		JettyMain.stopServer();
	}
	
	@Test
	public void getPass() throws InterruptedException, ExecutionException, TimeoutException {
		ContentResponse response = client.GET("http://localhost:8080/contacts/1");
		assertEquals("200 OK", Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void getFail() throws InterruptedException, ExecutionException, TimeoutException {
		 ContentResponse response = client.GET("http://localhost:8080/contacts/9999");
		 assertEquals("404 Not Found", Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void postPass() throws InterruptedException, TimeoutException, ExecutionException {
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
		assertEquals("Conflict", Status.CONFLICT.getStatusCode(), response.getStatus());
	}

	@Test
	public void deletePass() throws InterruptedException, TimeoutException, ExecutionException {
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts/4");
		request.method(HttpMethod.DELETE);
		ContentResponse response = request.send();
		
		assertEquals("200 OK", Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void deleteFail() throws InterruptedException, TimeoutException, ExecutionException {
		org.eclipse.jetty.client.api.Request request = client.newRequest("http://localhost:8080/contacts/6789");
		request.method(HttpMethod.DELETE);
		ContentResponse response = request.send();
		
		assertEquals("404 NOT FOUND", Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
}