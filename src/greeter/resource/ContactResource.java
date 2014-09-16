package greeter.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import entity.Contact;
import service.ContactDao;
import service.DaoFactory;
/**
 * 
 * @author Eknarin 5510546239
 * get request to DAO
 */

@Path("/contacts")
public class ContactResource {
	private ContactDao contactDao;
	
	public ContactResource() {
		contactDao = DaoFactory.getInstance().getContactDao();
	}
	/**
	 * get all contacts
	 * contacts can search by title
	 * @param title to find in contacts
	 * @return all contacts
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllContact( @QueryParam("q") String title) {
		List<Contact> listContact = new ArrayList<Contact>();
		
		if( title == null)
			listContact = contactDao.findAll();
		else
			listContact = contactDao.getContactByTitle(title);
		
		GenericEntity<List<Contact>> contactEntity = new GenericEntity<List<Contact>>(listContact) {};
		if(contactEntity == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else{
			return Response.ok(contactEntity).build();
		}
	}
	
	/**
	 * get contact by searching id
	 * @param id 
	 * @return response code
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getContactById(@PathParam("id") long id) {
		if(contactDao.find(id) == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else{
			Contact contactFromId = contactDao.find(id);
			return Response.ok(contactFromId).build();
		}
	}
	
	/**
	 * create a contact
	 * @param element
	 * @param uriInfo
	 * @return response code
	 */
	@POST
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo ) {
			Contact contact = element.getValue();
			contactDao.save( contact );
			URI location = uriInfo.getAbsolutePath();
			return Response.ok(location+"/"+contact.getId()).build();
	}

	/**
	 * update a contact by id
	 * @param element
	 * @param uriInfo
	 * @param id
	 * @return response code
	 */
	@PUT
	@Path("{id}")
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response put(JAXBElement<Contact> element, @Context UriInfo uriInfo, @PathParam("id") long id) {
		if(contactDao.find(id) == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else{
			Contact contact = element.getValue();
			contact.setId(id);
			contactDao.update(contact);
			URI location = uriInfo.getAbsolutePath();
			return Response.ok(location+"/"+contact.getId()).build();
		}
	}
	
	/**
	 * delete contact by id
	 * @param id
	 */
	@DELETE
	@Path("{id}")
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public void delete(@PathParam("id") long id) {
			contactDao.delete(id);
	}
		
	
}