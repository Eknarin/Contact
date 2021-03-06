package contact.resource;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;
/**
 * 
 * @author Eknarin 5510546239
 * get request to DAO
 */

@Path("/contacts")
public class ContactResource {
	private ContactDao contactDao;
	private CacheControl cControl;

	public ContactResource() {
		contactDao = DaoFactory.getInstance().getContactDao();
		cControl = new CacheControl();
		cControl.setMaxAge(86400);
		cControl.setPrivate(true);
	}
	/**
	 * get all contacts
	 * search by contacts title
	 * @param title to find in contacts
	 * @return a list of contacts
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllContact( @QueryParam("title") String title) {
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
	 * search contact by id
	 * @param id 
	 * @return response code which tell us it can find the contact or not
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getContactById(@PathParam("id") long id, @HeaderParam ("If-None-Match") String head) {
		Contact contact = contactDao.find(id);
		if(contact == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else{
			//			Contact contactFromId = contactDao.find(id);
			EntityTag etag = new EntityTag(contact.findHashCode());
			if(head != null && head.equals(etag.toString())){
				return Response.status(Status.NOT_MODIFIED).build();
			}
			return Response.ok(contact).cacheControl(cControl).tag(etag).build();
		}
	}

	/**
	 * create a new contact
	 * @param element
	 * @param uriInfo
	 * @return response code about post successful
	 * @throws URISyntaxException 
	 */
	@POST
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo ) throws URISyntaxException {

		Contact contact = element.getValue();
		if(contactDao.find(contact.getId()) == null){
			contactDao.save( contact );
			URI location = uriInfo.getAbsolutePath();
			return Response.created(new URI("localhost:8080/contacts/" + contact.getId())).tag(new EntityTag(contact.findHashCode())).build();
		}
		else{
			return Response.status(Status.CONFLICT).build();
		}
	}

	/**
	 * update a contact by id
	 * @param element
	 * @param uriInfo
	 * @param id
	 * @return response code that it can update or not
	 */
	@PUT
	@Path("{id}")
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response put(JAXBElement<Contact> element, @Context UriInfo uriInfo, @PathParam("id") long id, @HeaderParam("If-Match") String head) {
		Contact contact = contactDao.find(id);
		if(contactDao.find(id) == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else{
			EntityTag etag = new EntityTag(contact.findHashCode());
			if(head != null){
				if(!head.equals(etag.toString())){
					return Response.status(Status.PRECONDITION_FAILED).build();
				}
			}
			Contact contact2 = element.getValue();
			contact2.setId(id);
			contactDao.update(contact2);
			URI location = uriInfo.getAbsolutePath();
			//			return Response.ok(location+"/"+contact.getId()).build();
			return Response.ok(location+"").build();


		}
	}

	/**
	 * delete contact by id
	 * @param id
	 */
	@DELETE
	@Path("{id}")
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response delete(@PathParam("id") long id, @HeaderParam("If-Match") String head) {
		Contact contact = contactDao.find(id);
		if(contact != null)	{
			EntityTag etag = new EntityTag(contact.findHashCode());
			if(head != null){
				if(!head.equals(etag.toString())){
					return Response.status(Status.PRECONDITION_FAILED).build();		
				}
			}
			
			contactDao.delete(id);
			return Response.ok().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
}
