package entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * create a list for collect contacts 
 * can be trn into XML type 
 * 
 * @author eknarin
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactList {

	private List<Contact> contactlist;
	
	public ContactList() {
		contactlist = new ArrayList<Contact>(); 
	}
	/**
	 * get the contactlist
	 * @return contactlist
	 */
	public List<Contact> getContactlist() {
		return contactlist;
	}

	/**
	 * set contactlist
	 * @param contactlist
	 */
	public void setContactlist(List<Contact> contactlist) {
		this.contactlist = contactlist;
	}
	
	
}
