package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.ContactList;
import contact.service.ContactDao;
import contact.service.DaoFactory;

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

	public MemDaoFactory(){

		daoInstance = new MemContactDao();
		contactlist = new ContactList();
		//unmarshall	
		try{
			JAXBContext context = JAXBContext.newInstance(ContactList.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			File file = new File("XMLfile.xml");

			if(!file.exists()) {
				contactlist.setContactlist( new ArrayList<Contact>());
				JAXBContext context2 = JAXBContext.newInstance(ContactList.class);
				Marshaller marshaller = context2.createMarshaller();
				file = new File("XMLfile.xml");
				marshaller.marshal(contactlist, file);
			}
			File file2 = new File("XMLfile.xml");
			contactlist = (ContactList)unmarshaller.unmarshal(file2);

			for(int i=0; i<contactlist.getContactlist().size(); i++){
				daoInstance.save(contactlist.getContactlist().get(i));
			}

		} catch (Exception e){
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
			File file = new File("XMLfile.xml");
			marshaller.marshal(contactlist, file);
		} catch (JAXBException e){
			e.printStackTrace();
		}
	}

}
