 package contact.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import entity.Contact;
import entity.ContactList;
import service.ContactDao;
import service.DaoFactory;

/**
 * Manage instances of Data Access Objects (DAO) used in the app.
 * This enables you to change the implementation of the actual ContactDao
 * without changing the rest of your application.
 * 
 * @author jim
 */
public class MemDaoFactory extends DaoFactory{

	// singleton instance of this factory
	private static MemDaoFactory factory;
	private MemContactDao daoInstance;
	private ContactList contactlist;
	
	public MemDaoFactory() {
		daoInstance = new MemContactDao();
		contactlist = new ContactList();
	//unmarshall	
		try{
			 JAXBContext context = JAXBContext.newInstance(ContactList.class);
			 Unmarshaller unmarshaller = context.createUnmarshaller();
			 File file = new File("/tmp/XMLfile.xml");
			 contactlist = (ContactList)unmarshaller.unmarshal(file);
			 
			 for(int i=0; i<contactlist.getContactlist().size(); i++){
				 daoInstance.save(contactlist.getContactlist().get(i));
			 }
			 
		} catch (JAXBException e){
			e.printStackTrace();
		}
	}
	//singleton
	public static MemDaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}
	
	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		List<Contact> contacts = daoInstance.findAll();
		contactlist.setContactlist(contacts);
		
		try{
			 JAXBContext context = JAXBContext.newInstance(ContactList.class);
			 Marshaller marshaller = context.createMarshaller();
			 File file = new File("/tmp/XMLfile.xml");
			 marshaller.marshal(contactlist, file);
		} catch (JAXBException e){
			e.printStackTrace();
		}
	}
	
}
